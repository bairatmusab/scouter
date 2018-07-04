

package org.atos.scouter.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import org.atos.scouter.metrics.MetricsLogger;
import org.atos.scouter.models.Event;
import org.atos.scouter.util.PropertiesManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class writes data in mongodb database
 *
 * @version 1.0
 */
public class DatabaseWriter {
    /**
     * Properties of this class
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     * @see PropertiesManager#getInstance()
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();
    /**
     * Object to add metrics from this class
     *
     * @see DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)
     * @see MetricsLogger#getMetricsLogger()
     * @see MetricsLogger#log(String, long)
     */
    private static final MetricsLogger METRICS_LOGGER = MetricsLogger.getMetricsLogger();
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseWriter.class);
    /**
     * Single instance of {@link DatabaseWriter}
     *
     * @see DatabaseWriter#getInstance()
     */
    private static final DatabaseWriter DATABASE_WRITER = new DatabaseWriter();
    /**
     * Mongo collection containing {@link Event}
     *
     * @see DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)
     */
    private final MongoCollection<Document> mongoCollection;
    /**
     * Constant value {@value LOCATION_FIELD}
     *
     * @see DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)
     */
    private static final String LOCATION_FIELD = "location";
    /**
     * Mapper to read JSON
     *
     * @see DatabaseWriter#insertEvent(Event, DatabaseWriterCallback)
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * DataWriter constructor
     *
     * @throws IllegalStateException if database configuration is not set
     */
    private DatabaseWriter() {
        try {
            final MongoClient mongoClient = MongoClients.create(PROPERTIES_MANAGER.getProperty("database.host"));
            final MongoDatabase mongoDatabase = mongoClient.getDatabase(PROPERTIES_MANAGER.getProperty("database.events.db_name"));
            this.mongoCollection = mongoDatabase.getCollection(PROPERTIES_MANAGER.getProperty("database.events.collection_name"));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException(e.getMessage());
        }
        LOGGER.info("DatabaseWriter has been instantiate");
    }

    /**
     * Return an instance of {@link DatabaseWriter}
     *
     * @return Single instance of {@link DatabaseWriter}
     * @see DatabaseWriter#DATABASE_WRITER
     */
    public static DatabaseWriter getInstance() {
        return DATABASE_WRITER;
    }

    /**
     * This method inserts an {@link Event} in the database
     *
     * @param event    {@link Event} to insert into database
     * @param callback {@link DatabaseWriterCallback} called after inserting
     * @throws NullPointerException    if event or callback is null
     * @see DatabaseWriter#mongoCollection
     * @see DatabaseWriter#LOCATION_FIELD
     * @see DatabaseWriter#METRICS_LOGGER
     * @see DatabaseWriter#mapper
     */
    public void insertEvent(Event event, DatabaseWriterCallback callback) {
        Objects.requireNonNull(event);
        Objects.requireNonNull(callback);
        try {
            long start = System.currentTimeMillis();
            Document document = Document.parse(mapper.writeValueAsString(event));
            document.remove("start");
            document.remove("end");
            document.remove(LOCATION_FIELD);
            document.remove("entityName");
            document.append("start", event.getStart());
            document.append("end", event.getEnd());
            document.append("location_text", PROPERTIES_MANAGER.getProperty("datasource.location"));
            document.append("sourceName",event.getEntityName());
            document.append("sourceId",event.getSourceId());
            document.append("verified",event.getVerified());
            document.append("keyterms",event.getEventsKeyTerms());
            document.append("isMergedEvent",event.isMergedEvent());
            document.append("sentimentSense", event.getSentimentSense());

            if(event.isParentEventWhenMerging()){
                document.append("sourceEventsCausingMerging",event.getEventsMerged());
            }

            if (event.getLocation().length == 1) {
                document.append(LOCATION_FIELD, new Point(new Position(
                        event.getLocation()[0].getLongitude(), event.getLocation()[0].getLatitude())));
            } else {
                List<Position> positions = Arrays.stream(event.getLocation())
                        .map(p -> new Position(p.getLongitude(), p.getLatitude())).collect(Collectors.toList());
                document.append(LOCATION_FIELD, new Polygon(positions));
            }
            this.mongoCollection.insertOne(document, (result, t) -> callback.onResult(t));
            long time = System.currentTimeMillis() - start;
            //METRICS_LOGGER.log("time_dbwriter_" + event.getSource(), time);
        } catch (JsonProcessingException e) {
            LOGGER.error("Invalid event format: event not inserted in database.");
        }
    }
}
