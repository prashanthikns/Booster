package Booster;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkTaskContext;


// Task for PostgreSQL sink connector for fuel requests.
public class FuelSinkTask extends SinkTask {
	
	ArrayList<String> fuelList = new ArrayList<String>();
	
	DBConnect newConnection = new DBConnect();
	
	@SuppressWarnings("unused")
	private SinkTaskContext FuelContext;

	//Return connector version
	@Override
	public String version() {
		return FuelSinkConnect.VERSION;
	}
	
	//Initialise sink task
	@Override
	public void initialize(SinkTaskContext context) {
		FuelContext = context;
	}

	//Start the task
	@Override
	public void start(Map<String, String> props) throws ConnectException {
  
	    System.out.println("Starting");	    
		newConnection.connect();
	}
	
	//Parses JSON value in each record and create an SQL query string and add to arrayList
	@Override
	public void put(Collection<SinkRecord> sinkRecords) throws ConnectException {
  
		for (SinkRecord fuelrecord : sinkRecords) {
			System.out.print("Put message: " + (String)fuelrecord.value() + "\n"); 
			
			System.out.println("FUEL PARSING");;
			JsonReader jsonReader1 = Json.createReader(new StringReader((String) fuelrecord.value())); 
			JsonObject jsonObj = jsonReader1.readObject();
			String fuelId = jsonObj.getString("fuelId");
			System.out.println("Fuel ID and Name" + fuelId);
			 	     
			double lat  = jsonObj.getJsonNumber("lat").doubleValue();
			double lon  = jsonObj.getJsonNumber("lon").doubleValue();
				    	
			System.out.printf("fuel id: %s  lat: %f lon: %f\n", fuelId, lat, lon);	
			        
			String fuelSql = "INSERT INTO fuelTable (fuelId, lat, lon, geom) "
			        		 + "VALUES (" 
			        		 + "'" + fuelId + "'" + ","
			        		 +  lat + "," 
			        		 +  lon + ","
			        		 +  "'POINT("+ lat + " " + lon + ")'" + ");";
			        
			System.out.println(fuelSql);
			fuelList.add(fuelSql);
		}
	}

	//Flushes content to the database
	@Override
	public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) throws ConnectException {

		System.out.print("Flush start at "+System.currentTimeMillis());
		if (fuelList != null) {
			System.out.print(fuelList.size());
			for(int fuelListIndex=0; fuelListIndex < fuelList.size(); fuelListIndex++){
				try {
					newConnection.insertFuelDB(fuelList.get(fuelListIndex));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}		
			// Clear the Arraylist and the updateDeviceTimeObject
			fuelList.clear() ;
			System.out.print("Flush stop at " + System.currentTimeMillis());
		}   
	  }
	
	//Stop the sink task
	@Override
	public void stop() throws ConnectException {
		System.out.print("Stopping");
	}
}