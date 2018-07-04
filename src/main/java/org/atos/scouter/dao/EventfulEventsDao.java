package org.atos.scouter.dao;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import org.atos.scouter.models.EventfulEvent;
import org.atos.scouter.util.MongoDBConnectionManager;
import org.atos.scouter.util.PropertiesManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class represent the data access layer for eventful-based event, as it meant to be stored in
 * a separate collection in Mongo database.
 *
 * Created by Musab on 31/03/2017.
 */
public class EventfulEventsDao {
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
    private static final Logger LOGGER = LoggerFactory.getLogger(EventfulEventsDao.class);

    /**
     * Singleton object of {@link EventfulEventsDao}
     */

    private static final EventfulEventsDao dao = new EventfulEventsDao();

    /**
     * Static strings used as keys for document storage.
     */
    private static final String TIME_FIELD_KEY = "time";

    private static final String LONGITUDE_KEY = "longitude";

    private static final String LATITUDE_KEY = "latitude";

    private static final String DESCRIPTION_KEY = "description";

    private static final String TITLE_KEY = "title";

    private static final String CITY_KEY = "city";

    private static final String COUNTRY_KEY = "country";

    private static final String ADDRESS_KEY = "address";

    private static final String SOURCE_KEY = "source";

    /**
     * Object that provides access to mongo collections
     */

    private final MongoCollection<Document> mongoCollection;

    /**
     * Constructor method, initiates mongo collection access object to be used.
     */

    public EventfulEventsDao() {
        try {

            // get mongo client
            MongoClient mongoClient = MongoDBConnectionManager.getInstance().getMongoClient();

            // access database to fetch collection
            final MongoDatabase mongoDatabase = mongoClient
                    .getDatabase(PROPERTIES_MANAGER.getProperty("database.events.db_name"));
            // fetch collection
            this.mongoCollection = mongoDatabase
                    .getCollection(PROPERTIES_MANAGER.getProperty("database.events.eventful.events.coll.name"));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
        LOGGER.info("EventfulEventsDao data access layer has been instantiate");
    }

    /**
     * Getter for singleton object
     * @return {@link EventfulEventsDao} singleton object
     */
    public static EventfulEventsDao getInstance() {
        return dao;
    }

    /**
     * Method to insert {@link EventfulEvent} into mongo collection
     * @param event: the  {@link EventfulEvent} to be inserted.
     * @throws NullPointerException    if event is null
     */
    public void insertEvent(EventfulEvent event) {
        Objects.requireNonNull(event);

        Document doc = new Document();
        doc.append(TITLE_KEY,event.getTitle());
        doc.append(TIME_FIELD_KEY,event.getTime());
        doc.append(DESCRIPTION_KEY,event.getDescription());
        doc.append(LATITUDE_KEY,event.getLocation().getLatitude());
        doc.append(LONGITUDE_KEY,event.getLocation().getLongitude());
        doc.append(SOURCE_KEY,event.getSource());
        doc.append(CITY_KEY,event.getCity());
        doc.append(COUNTRY_KEY,event.getCountry());
        doc.append(ADDRESS_KEY, event.getAddress());


        this.mongoCollection.insertOne(doc, (result, t) -> {
            if (t != null) {
                LOGGER.error(t.getMessage());
            }
        });
    }

    /**
     * Method to insert list of {@link EventfulEvent} into mongo collection
     * @param events: the list of {@link EventfulEvent} to be inserted.
     * @throws NullPointerException    if the list null
     */

    public void insertEvents(ArrayList<EventfulEvent> events){
        Objects.requireNonNull(events);
        for(EventfulEvent ev : events){
            insertEvent(ev);
        }
    }
}
