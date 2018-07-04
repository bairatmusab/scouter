package org.atos.scouter.metrics;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;
import org.atos.scouter.util.CommonUtils;
import org.atos.scouter.util.MongoDBConnectionManager;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class to report Mongo database statistics into Json Object
 */

public class MongoMetricsReporter {

	/**
	 * Logger used to log information of this class
	 */
	 private static final Logger LOGGER = LoggerFactory.getLogger(MongoMetricsReporter.class);

	/**
	 * Singleton object of this class.
	 */
	private static final MongoMetricsReporter reporter = new MongoMetricsReporter();

	/**
	 * Mongo Client to access mongo database.
	 */
	private MongoClient mongoClient = mongoClient = MongoDBConnectionManager.getInstance().getMongoClient();

	private static final MetricsLogger METRICS_LOGGER = MetricsLogger.getMetricsLogger();


	private static final String MONGODB_MEASUREMENT_NAME = "mongodb";

	/**
	 * Public constructor.
	 */
	public MongoMetricsReporter (){
		 
	}

	/**
	 * Getter for singleton object
	 * @return {@link MongoMetricsReporter} singleton object
	 */
	 public static MongoMetricsReporter getInstance(){
			return reporter;
		}

	/**
	 * Method to retrieve statistics from mongo database
	 * @param databaseName: database to get stats about
	 */
	 public void reportMongoDatabaseToInflux(String databaseName){
		MongoDatabase db = mongoClient.getDatabase(databaseName);
	
		db.runCommand( new Document("serverStatus",1), new SingleResultCallback<Document>() {

			@Override
			public void onResult(Document result, Throwable t) {
				HashMap<String,Object> statsMap = parseMongoDBStats(result);
				for (Map.Entry entry : statsMap.entrySet()) {
					//System.out.println(entry.getKey() + " === " + entry.getValue());
					String key = String.valueOf(entry.getKey());
					String val = String.valueOf(entry.getValue());
					METRICS_LOGGER.log(MONGODB_MEASUREMENT_NAME, key,val);
				}
			}
		});
		 //System.out.println("\n\n\n");
	 }

	/**
	 * Method to parse returned stats query results, as we are not interested in all information returned.
	 * @param doc: returned document from mongo query.
	 * @return HashMap<String,Object> containing required information.
	 */
	 private HashMap<String,Object> parseMongoDBStats(Document doc){
		 Objects.requireNonNull(doc);

		 HashMap<String,Object> statsMap = new HashMap<>();

		 Long uptime = (Long) doc.get("uptimeMillis");
		 statsMap.put("uptime",DurationFormatUtils.formatDurationHMS(uptime));

		 Map<String,Object> networkMap = (Map<String, Object>) doc.get("network");

		 Long bytesIn = (Long) networkMap.get("bytesIn");
		 Long bytesOut = (Long)networkMap.get("bytesOut");

		 statsMap.put("bytesIn", CommonUtils.humanReadableByteCount(bytesIn,false));
		 statsMap.put("bytesOut",CommonUtils.humanReadableByteCount(bytesOut,false));

		 Map<String,Object> metrics = (Map<String, Object>) doc.get("metrics");
		 Map<String,Object> documentStatsMap = (Map<String, Object>) metrics.get("document");

		 statsMap.put("deleted_documents",documentStatsMap.get("deleted"));
		 statsMap.put("inserted_documents",documentStatsMap.get("inserted"));
		 statsMap.put("updated_documents",documentStatsMap.get("updated"));
		 statsMap.put("returned_documents",documentStatsMap.get("returned"));

		 return statsMap;
	 }
}
