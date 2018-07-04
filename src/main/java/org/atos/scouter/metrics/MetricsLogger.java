

package org.atos.scouter.metrics;

import org.atos.scouter.models.Event;
import io.vertx.core.json.JsonObject;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Class used in whole project to log value into InfluxDB
 *
 * @version 1.0
 */
public class MetricsLogger {
    /**
     * Object used to log into InfluxDB
     *
     * @see MetricsLogger#close()
     * @see MetricsLogger#getMetricsLogger()
     * @see MetricsLogger#checkValidInfluxDBConnexion()
     * @see MetricsLogger#read(String)
     * @see MetricsLogger#log(Event)
     * @see MetricsLogger#log(Point...)
     * @see MetricsLogger#log(String, long)
     * @see MetricsLogger#log(String, String)
     * @see MetricsLogger#log(String, String, String)
     */
    private final MetricsConnector metricsConnector = MetricsConnector.getMetricsConnector();
    /**
     * Logger used to log all information in this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsLogger.class);
    /**
     * Single instance of {@link MetricsLogger}
     */
    private static final MetricsLogger METRICS_LOGGER = new MetricsLogger();
    /**
     * Constant value {@value AUTOGEN}
     *
     * @see MetricsLogger#log(Event)
     * @see MetricsLogger#log(String, long)
     * @see MetricsLogger#log(String, String)
     * @see MetricsLogger#log(String, String, String)
     */
    private static final String AUTOGEN = "autogen";
    /**
     * Constant value {@value ASYNC}
     *
     * @see MetricsLogger#log(Event)
     * @see MetricsLogger#log(String, long)
     * @see MetricsLogger#log(String, String)
     * @see MetricsLogger#log(String, String, String)
     */
    private static final String ASYNC = "async";
    /**
     * Name of the collection in InfluxDB
     *
     * @see MetricsLogger#log(Event)
     * @see MetricsLogger#log(String, long)
     * @see MetricsLogger#log(String, String)
     */
    private final String measurement;

    /**
     * Instantiate MetricsLogger object
     */
    private MetricsLogger() {
        this.measurement = metricsConnector.getProperties().getMeasurement();
        if (!this.metricsConnector.isConnexionEnabled() || this.metricsConnector.getInfluxDB() == null) {
            LOGGER.warn("influxdb connexion disabled");
        } else {
            LOGGER.info(MetricsLogger.class.getName() + " has been started ...");
        }
    }

    /**
     * Get instance of singleton MetricsLogger
     *
     * @return An unique instance of MetricsLogger
     */
    public static MetricsLogger getMetricsLogger() {
        return METRICS_LOGGER;
    }

    /**
     * Close the connexion with the influx database
     *
     * @see MetricsLogger#metricsConnector
     */
    public void close() {
        if (metricsConnector != null) {
            metricsConnector.close();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Log a long value into influx database with a specific field
     *
     * @param field name of the metric field
     * @param value value of the field, usually a timestamp in millis
     * @throws NullPointerException if field is null
     * @see MetricsLogger#metricsConnector
     * @see MetricsLogger#measurement
     * @see MetricsLogger#ASYNC
     * @see MetricsLogger#AUTOGEN
     */
    public void log(String field, long value) {
        Objects.requireNonNull(field);
        if (checkValidInfluxDBConnexion()) {
            final InfluxDB influxDB = metricsConnector.getInfluxDB();
            Point point = Point.measurement(measurement).tag(ASYNC, "true")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField(field, value).build();
            influxDB.write(metricsConnector.getProperties().getDbName(), AUTOGEN, point);
            LOGGER.info(MetricsLogger.class.getName() + " : indexed points " + point);
        }
    }

    /**
     * Log a data as value sent to the influx database into a specific measurement
     *
     * @param field specify the field in order to build a point
     * @param data  the value to stock
     * @throws NullPointerException if field or data is null
     * @see MetricsLogger#metricsConnector
     * @see MetricsLogger#measurement
     * @see MetricsLogger#ASYNC
     * @see MetricsLogger#AUTOGEN
     */
    public void log(String field, String data) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(data);

        if (checkValidInfluxDBConnexion()) {
            final InfluxDB influxDB = metricsConnector.getInfluxDB();
            Point point = Point.measurement(measurement)
                    .tag(ASYNC, "true")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField(field, data).build();
            influxDB.write(this.metricsConnector.getProperties().getDbName(), AUTOGEN, point);
            LOGGER.info("{} : indexed points {}", MetricsLogger.class.getName(), point.toString());
        }
    }

    /**
     * Create a new metric with a data set (field, value) loaded into influx database if it's possible
     *
     * @param measurement Name of new metric
     * @param field       specify the field in order to build a point
     * @param data        the value to stock
     * @throws NullPointerException if at least one of these arguments measurement, field, data are null
     * @see MetricsLogger#metricsConnector
     * @see MetricsLogger#ASYNC
     * @see MetricsLogger#AUTOGEN
     */
    public void log(String measurement, String field, String data) {
        Objects.requireNonNull(measurement);
        Objects.requireNonNull(field);
        Objects.requireNonNull(data);
        if (checkValidInfluxDBConnexion()) {
            final InfluxDB influxDB = metricsConnector.getInfluxDB();
            Point point = Point.measurement(measurement)
                    .tag(ASYNC, "true")
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .addField(field, data).build();
            influxDB.write(metricsConnector.getProperties().getDbName(), AUTOGEN, point);
            LOGGER.info(MetricsLogger.class.getName() + " : indexed points " + point.toString());
        }
    }

    /**
     * Log a not null event into the influx database if it's possible
     *
     * @param event {@link Event} to stock
     * @throws NullPointerException if event is null
     * @see MetricsLogger#metricsConnector
     * @see MetricsLogger#measurement
     * @see Event
     */
    public void log(Event event) {
        Objects.requireNonNull(event);
        if (checkValidInfluxDBConnexion()) {
            final InfluxDB influxDB = metricsConnector.getInfluxDB();
            Point point = Point.measurement(measurement).tag("event_source", event.getSource())
                    .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                    .build();
            BatchPoints.Builder builder = BatchPoints.database(metricsConnector.getProperties().getDbName());
            BatchPoints points = builder.point(point).build();
            influxDB.write(points);
            LOGGER.info(MetricsLogger.class.getName() + " : indexed " + points.getPoints());
        }
    }

    /**
     * Log a collections of point and send them into influx database
     *
     * @param points the points to check metrics
     * @throws NullPointerException if points is null
     * @see MetricsLogger#metricsConnector
     */
    public void log(Point... points) {
        Objects.requireNonNull(points);
        if (checkValidInfluxDBConnexion()) {
            final InfluxDB influxDB = metricsConnector.getInfluxDB();
            influxDB.setLogLevel(InfluxDB.LogLevel.BASIC);
            BatchPoints.Builder builder = BatchPoints.database(metricsConnector.getProperties().getDbName());
            builder.points(points);
            BatchPoints batchPoints = builder.build();
            influxDB.write(batchPoints);
            batchPoints.getPoints().stream().map(Point::lineProtocol).forEach(point ->
                    LOGGER.info(MetricsLogger.class.getName() + " : push metrics point " + point)
            );
        }
    }

    /**
     * Check if there is a valid instance of InfluxDB
     *
     * @return true if {@link MetricsLogger} is valid
     * @see MetricsLogger#metricsConnector
     */
    public boolean checkValidInfluxDBConnexion() {
        return metricsConnector != null && metricsConnector.getInfluxDB() != null && metricsConnector.isConnexionEnabled();
    }

    /**
     * Read the result of a request to read data loaded into influxdb
     *
     * @param request the specific request
     * @throws NullPointerException if request is null
     * @see MetricsLogger#metricsConnector
     */
    public void read(String request) {
        Objects.requireNonNull(request);
        if (checkValidInfluxDBConnexion()) {
            final Query query = new Query(request, metricsConnector.getProperties().getDbName());
            QueryResult fixes = this.metricsConnector.getInfluxDB().query(query);
            List<QueryResult.Result> resultsList = fixes.getResults();
            resultsList.stream().filter(results -> results.getSeries() != null).forEach(results -> results.getSeries().forEach(series -> System.out.println(series.toString())));
        }
    }

    public JsonObject getLastMetricsValues() {
        JsonObject processingMetrics = executeQuery("SELECT last(*) FROM metrics" , metricsConnector.getProperties().getDbName());
        JsonObject MongoMetrics = executeQuery("SELECT last(*) FROM mongodb" , metricsConnector.getProperties().getDbName());
        JsonObject result = new JsonObject();

        result.put("processing_metrics",processingMetrics);
        result.put("mongodb_metrics",MongoMetrics);

        JsonObject kafkaMetrics = new JsonObject();

        kafkaMetrics.put("topics_count", executeQuery("select last(count) from BrokerTopicMetrics","kafka").getValue("last"));
        kafkaMetrics.put("memory_heap_usage", executeQuery("select last(memory.heap_usage) from JVM","kafka").getValue("last"));
        kafkaMetrics.put("memory_non_heap_usage", executeQuery("select last(memory.non_heap_usage) from JVM","kafka").getValue("last"));
        kafkaMetrics.put("uptime", executeQuery("select last(uptime) from JVM","kafka").getValue("last"));

        result.put("kafka_metrics",kafkaMetrics);


        return result;
    }

    private JsonObject executeQuery(String queryStr, String databaseName){
        JsonObject result = new JsonObject();
        if (checkValidInfluxDBConnexion()) {
            final Query query = new Query(queryStr, databaseName );
            QueryResult fixes = this.metricsConnector.getInfluxDB().query(query);
            List<QueryResult.Result> resultsList = fixes.getResults();
            List<String> cols = resultsList.get(0).getSeries().get(0).getColumns();
            List<Object> vals = resultsList.get(0).getSeries().get(0).getValues().get(0);
            for(int i = 0 ; i < cols.size() ; i++){
                if (!cols.get(i).equalsIgnoreCase("time"))
                    result.put(cols.get(i),vals.get(i));
            }
        }
        return result;
    }
}





