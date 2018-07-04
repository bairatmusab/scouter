

package org.atos.scouter.communication;

import org.atos.scouter.dao.DatabaseReaderCallback;
import org.atos.scouter.dao.IDatabaseReader;
import org.atos.scouter.metrics.MetricsLogger;
import org.atos.scouter.models.BoundingBox;
import org.atos.scouter.models.Request;
import org.atos.scouter.Main.Main;
import org.atos.scouter.util.Geocoder;
import org.atos.scouter.util.PropertiesManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

/**
 * This server relies on Vertx, to handle the REST requests. It is instanciated by the web communication connector.
 *
 * @version 1.0
 * @see io.vertx.core.Verticle
 * @see io.vertx.core.AbstractVerticle
 */
public class VertxServer extends AbstractVerticle {


    /**
     * Logger used to log all information in this module
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);
    /**
     * {@link IDatabaseReader} object to read data from database
     *
     * @see VertxServer#getEvent(Request, DatabaseReaderCallback)
     */
    private final IDatabaseReader databaseReader = WebCommunication.databaseReader;

    PropertiesManager propertiesManager = PropertiesManager.getInstance();

    private static final MetricsLogger METRICS_LOGGER = MetricsLogger.getMetricsLogger();

    /**
     * Server starting behaviour
     *
     * @param fut Future that handles the start status
     * @throws NullPointerException if fut is null
     */
    @Override
    public void start(Future<Void> fut) {
        Objects.requireNonNull(fut);
        Router router = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("X-PINGARUNER")
                .allowedHeader("Content-Type")
        );

        router.route("/*").handler(BodyHandler.create()); // enable reading of request's body
        router.get("/anomaly").handler(this::getAnomalies);
        router.post("/anomaly").handler(this::getAnomalies);
        router.get("/metrics").handler(this::getMetrics);
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8081), // default value: 8081
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
        LOGGER.info("VertxServer started");
    }

    /**
     * Reads a request from a routing context, and attach the response to it. It requests the database
     * with DatabaseReader.
     *
     * @param rc {@link RoutingContext}, which contains the request, and the response
     * @throws NullPointerException if rc is null
     */
    private void getAnomalies(RoutingContext rc) {
        Request request;
        try {
            LOGGER.info("Received web request: {}", rc.getBodyAsJson());
            request = parseRequest(rc.getBodyAsJson());
            if (request == null) {
                rc.response()
                        .setStatusCode(400)
                        .putHeader("Content-type", "application/json;charset:utf-8")
                        .end("{\"error\": \"Invalid address\"}");
                return;
            }
        } catch (DecodeException | NullPointerException e) {
            LOGGER.info("Received an invalid format request : {} ", e.getMessage());
            LOGGER.debug("DecodeException: {}", e);
            rc.fail(400);
            return;
        }
        LOGGER.info("Request : {}", request);
        LOGGER.info("rc= {}", rc);
        rc.response().putHeader("content-type", "application/json");

        getEvent(request, (t, result) -> {
            if (t != null) {
                LOGGER.error("DatabaseReader error: " + t.getMessage());
                return;
            }
            LOGGER.info("Found events: {}", result);
            JsonObject response = new JsonObject("{\"events\":" + result + "}");
            rc.response().end(response.encode());
        });
    }

    /**
     * Convert a request from Json to Java object
     *
     * @param jsonRequest {@link JsonObject} json formatted request
     * @return {@link Request}
     * @throws NullPointerException if jsonRequest is null
     */
    private Request parseRequest(JsonObject jsonRequest) {
        Objects.requireNonNull(jsonRequest);
        Date start = new Date(jsonRequest.getLong("start"));
        Date end = new Date(jsonRequest.getLong("end"));
        String address = jsonRequest.getString("address");
        Geocoder geocoder = Geocoder.geocode(address);
        if (geocoder.getLatLong() == null) {
            LOGGER.warn("Can't geocode this address {}", address);
            return null;
        }
        return new Request(start, end, new BoundingBox(geocoder.getBbox()), Date.from(Instant.now()));
    }

    /**
     * Retrieve an event from database
     *
     * @param request {@link Request} the user web request
     * @param databaseReaderCallback {@link DatabaseReaderCallback} called when request finished
     * @see VertxServer#databaseReader
     */
    private void getEvent(Request request, DatabaseReaderCallback databaseReaderCallback) {
        databaseReader.getEvent(request, databaseReaderCallback);
    }


    private void startScouter(){
        String[] arguments = new String[] {};
        try {
            Main.main(arguments);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getMetrics(RoutingContext rx){
        rx.response().end(METRICS_LOGGER.getLastMetricsValues().encodePrettily());
    }

}