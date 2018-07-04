package org.atos.scouter.util;

import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import org.atos.scouter.metrics.MongoMetricsReporter;

import java.util.ArrayList;
import java.util.List;

public class MongoDBConnectionManager {
	
	private static final PropertiesManager PROPERTIES_MANAGER = PropertiesManager.getInstance();

	private static final MongoDBConnectionManager mongoConnectonManager = new MongoDBConnectionManager();
	
	private MongoClient mongoClient; 
	
	public static MongoDBConnectionManager getInstance(){
		return mongoConnectonManager;
	}
	
	private MongoDBConnectionManager(){
		List<ServerAddress> hosts = new ArrayList<>();
		String hostsConfigLine = PROPERTIES_MANAGER.getProperty("database.hosts");
		String [] hostsStrings = hostsConfigLine.split(",");
		for(String h : hostsStrings){
			ServerAddress localhost = new ServerAddress(h);
			hosts.add(localhost);
		}
		ClusterSettings clusterSettings = ClusterSettings.builder().hosts(hosts).build();
		MongoClientSettings settings = MongoClientSettings.builder().clusterSettings(clusterSettings).build();
		mongoClient = MongoClients.create(settings);
		startMongoDBToInfluxReporter();
	}
	
	public MongoClient getMongoClient(){
		return mongoClient;
	}

	private static void startMongoDBToInfluxReporter(){
		Thread thread = new Thread(() -> reportMongoDB());
		thread.setName("ScouterMongoDBReporterThread");
		thread.start();
	}

	private static void reportMongoDB(){
		Long freq = Long.parseLong(PROPERTIES_MANAGER.getProperty("database.mongo.reporting.frequency"));
		String dbname = PROPERTIES_MANAGER.getProperty("database.events.db_name");
		while (!Thread.currentThread().isInterrupted()){
			MongoMetricsReporter.getInstance().reportMongoDatabaseToInflux(dbname);
			try {
				Thread.sleep(freq);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
