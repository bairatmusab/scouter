package org.atos.scouter.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.atos.scouter.models.Event;
import org.atos.scouter.models.LatLong;
import org.atos.scouter.util.MongoDBConnectionManager;
import org.atos.scouter.util.PropertiesManager;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * This class represent the data access layer for Open Weather Map {@link Event}, as it meant to be stored in
 * a separate collection in Mongo database.
 *
 * Created by Musab on 31/03/2017.
 */

public class OWMDao {

	/**
	 * Properties of this class
	 *
	 * @see PropertiesManager
	 * @see PropertiesManager#getProperty(String)
	 * @see PropertiesManager#getInstance()
	 */

	private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();

	/**
	 * Logger used to log all information in this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(OWMDao.class);

	/**
	 * Singleton object of {@link OWMDao}
	 */

	private static final OWMDao dao = new OWMDao();


	/**
	 * Static strings used as keys for document storage.
	 */
	private static final String TIME_FIELD_KEY = "time";

	private static final String LONGITUDE_KEY = "longitude";

	private static final String LATITUDE_KEY = "latitude";

	private static final String PLACE_KEY = "place";

	private static final String TEMP_KEY = "tempreture";

	private static final String DESCRIPTION_KEY = "description";

	private static final ObjectMapper mapper = new ObjectMapper();


	/**
	 * Object that provides access to mongo collections
	 */

	private final MongoCollection<Document> mongoCollection;

	/**
	 * Constructor method, initiates mongo collection access object to be used.
	 */
	public OWMDao() {
		try {
			
			MongoClient mongoClient = MongoDBConnectionManager.getInstance().getMongoClient();
			
			final MongoDatabase mongoDatabase = mongoClient
					.getDatabase(PROPERTIES_MANAGER.getProperty("database.events.db_name"));
			this.mongoCollection = mongoDatabase
					.getCollection(PROPERTIES_MANAGER.getProperty("database.events.weather.coll.name"));
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
			throw new IllegalStateException(e.getMessage());
		}
		LOGGER.info("OWM data access layer has been instantiate");
	}

	/**
	 * Getter for singleton object
	 * @return {@link OWMDao} singleton object
	 */
	public static OWMDao getInstance() {
		return dao;
	}


	/**
	 * Method to insert OWM {@link Event} into mongo collection
	 * @param event: the  {@link Event} to be inserted.
	 * @throws NullPointerException    if event is null
	 */
	public void insertWeatherEvent(Event event) {
		Objects.requireNonNull(event);

		HashMap<String,Object> weatherStatus = parseWeatherStatusFromJson(event.getDescription());

		Document doc = new Document();
		doc.append(TIME_FIELD_KEY, new Date());
		doc.append(TEMP_KEY,weatherStatus.containsKey(TEMP_KEY)?weatherStatus.get(TEMP_KEY):"");
		doc.append(PLACE_KEY,weatherStatus.containsKey(PLACE_KEY)?weatherStatus.get(PLACE_KEY):"");
		doc.append(DESCRIPTION_KEY,weatherStatus.containsKey(DESCRIPTION_KEY)?weatherStatus.get(DESCRIPTION_KEY):"");
		doc.append(LONGITUDE_KEY, event.getLocation()[0].getLongitude());
		doc.append(LATITUDE_KEY, event.getLocation()[0].getLatitude());
		this.mongoCollection.insertOne(doc, (result, t) -> {
			if (t != null) {
				LOGGER.error(t.getMessage());
			}
		});
	}

	/**
	 * Method to query OWM collection with the following params:
	 * @param start_date: start date to consider events after
	 * @param end_date: end data to consider events before
	 * @param longLat: location parameter, see {@link LatLong}
	 * @param lonMargin: get events within lonMargin around @param longLat.
	 * @param latMargin: get events within latMargin around @param longLat.
	 * @param <T>: return type class.
	 * @return: list of Json objects representing events.
	 */

	public <T> ArrayList<JSONObject> getWeatherWithin(Date start_date, Date end_date, LatLong longLat, int lonMargin,
			int latMargin) {
		List<Document> docs = new ArrayList<>();
		
		Bson filter = Filters.and(
				Filters.gt(TIME_FIELD_KEY, start_date),
				Filters.lte(TIME_FIELD_KEY, end_date),
				Filters.gt(LONGITUDE_KEY, longLat.getLongitude() - lonMargin),
				Filters.lte(LONGITUDE_KEY, longLat.getLongitude() + lonMargin),
				Filters.gt(LATITUDE_KEY, longLat.getLatitude() - latMargin),
				Filters.lte(LATITUDE_KEY, longLat.getLatitude() + latMargin)
				);
		
		
		
		mongoCollection.find(filter).into(docs, new SingleResultCallback<List<Document>>() {
			@Override
			public void onResult(final List<Document> result, final Throwable t) {
				LOGGER.info("Fetched " + result.size() + " events from " + mongoCollection.getNamespace());
			}
		});

		return null;
	}

	/**
	 * Method to query OWM collection with the following params:
	 * @param start_date: start date to consider events after
	 * @param end_date: end data to consider events before
	 * @param longLat: location parameter, see {@link LatLong}
	 * @return: list of Json objects representing events.
	 */

	public ArrayList<JSONObject> getWeatherWithin(Date start_date, Date end_date, LatLong longLat) {
		return getWeatherWithin(start_date, end_date, longLat, 0, 0);
	}


	/**
	 * Method to parse whole weather json object to retrieve specific fields.
	 * @param jsonObj: json object serialized as String
	 * @return
	 */
	private HashMap<String, Object> parseWeatherStatusFromJson(String jsonObj){


		HashMap<String, Object> status = new HashMap<>();
		try {
			// get place
			HashMap<String,Object> data = mapper.readValue(jsonObj,HashMap.class);
			if (data.containsKey("name")){
				status.put(PLACE_KEY,data.get("name"));
			}

			// get tempreture
			if (data.containsKey("main")){
				HashMap<String,Object> mainMap = (HashMap<String, Object>) data.get("main");
				if (mainMap.containsKey("temp"))
				status.put(TEMP_KEY,mainMap.get("temp"));
			}

			if (data.containsKey("weather")){
				ArrayList<HashMap<String,Object>> weahterMapArr = (ArrayList<HashMap<String, Object>>) data.get("weather");
				if (weahterMapArr.size() > 0 && weahterMapArr.get(0).containsKey("description")){
					status.put(DESCRIPTION_KEY,weahterMapArr.get(0).get("description"));
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.error("Exception while parsing weather Json Object {}",e.getMessage());
		}

		return status;
	}

}
