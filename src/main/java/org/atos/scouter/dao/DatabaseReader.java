

package org.atos.scouter.dao;

import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.geojson.Position;
import org.atos.scouter.metrics.MetricsLogger;
import org.atos.scouter.models.Request;
import org.atos.scouter.util.MongoDBConnectionManager;
import org.atos.scouter.util.PropertiesManager;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

/**
 * DatabaseReader class reads data (Events) from mongodb database
 *
 * @version 1.0
 * @see IDatabaseReader
 */
public class DatabaseReader implements IDatabaseReader {
    /**
     * Properties of this class
     *
     * @see PropertiesManager
     * @see PropertiesManager#getProperty(String)
     */
    private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();
    /**
     * Object to add metrics from this class
     *
     * @see MetricsLogger#getMetricsLogger()
     * @see MetricsLogger#log(String, long)
     */
    private static final MetricsLogger METRICS_LOGGER = MetricsLogger.getMetricsLogger();
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseReader.class);
    /**
     * Single instance of {@link DatabaseReader}
     *
     * @see DatabaseReader#getEvent(Request, DatabaseReaderCallback)
     * @see DatabaseReader#getInstance()
     */
    private static final DatabaseReader DATABASE_READER = new DatabaseReader();
    /**
     * Object use to read Document from MongoDb
     *
     * @see DatabaseReader#getEvent(Request, DatabaseReaderCallback)
     */
    private final MongoCollection<Document> mongoCollection;
    /**
     * Result limit of request
     *
     * @see DatabaseReader#getEvent(Request, DatabaseReaderCallback)
     */
    private final int limit;

    /**
     * The constructor of {@link DatabaseReader}
     * This class is a singleton
     *
     * @see DatabaseReader#PROPERTIES_MANAGER
     * @see DatabaseReader#mongoCollection
     * @see DatabaseReader#limit
     */
    private DatabaseReader() {
        try {
            //checkConfiguration();
            final MongoClient mongoClient = MongoDBConnectionManager.getInstance().getMongoClient(); //MongoClients.create(PROPERTIES_MANAGER.getProperty("database.host"));
            final MongoDatabase mongoDatabase = mongoClient.getDatabase(PROPERTIES_MANAGER.getProperty("database.events.db_name"));
            this.mongoCollection = mongoDatabase.getCollection(PROPERTIES_MANAGER.getProperty("database.events.collection_name"));
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
            throw new IllegalStateException(e);
        }
        int tmp = 50;
        try {
            tmp = Integer.parseInt(PROPERTIES_MANAGER.getProperty("database.limit"));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Use default database.limit");
        }
        this.limit = tmp;
        LOGGER.info("DatabaseReader has been instantiate");
    }

    /**
     * Get instance of Singleton DatabaseReader
     *
     * @return an instance of {@link DatabaseReader}
     * @see DatabaseReader#DATABASE_READER
     */
    public static DatabaseReader getInstance() {
        return DATABASE_READER;
    }

    /**
     * This method requests events from mongodb database and filters from data coming to the request object in parameter
     *
     * @param request  Request to apply to Mongo
     * @param callback Callback method call after select operation
     * @see DatabaseReader#limit
     * @see DatabaseReader#mongoCollection
     */
    @Override
    public void getEvent(Request request, DatabaseReaderCallback callback) {
        List<Position> polygon = Arrays.stream(request.getBoundingBox().getLatLongs())
                .map(l -> new Position(l.getLongitude(), l.getLatitude()))
                .collect(Collectors.toList());
        final long start = System.currentTimeMillis();
        this.mongoCollection
                .find(
                        and(
                        //geoIntersects("location", new Polygon(polygon)),
                        lte("end", request.getEnd()),
                        gte("start", request.getStart())
                ))
                .limit(limit)
                .into(new ArrayList<Document>(),
                        (result, t) -> {
                            long time = System.currentTimeMillis() - start;
                            //METRICS_LOGGER.log("time_dbreader", time);
                            callback.onResult(
                                    t,
                                    "[" + result.stream().map(Document::toJson).collect(Collectors.joining(", ")) + "]"
                            );
                        });
    }
}