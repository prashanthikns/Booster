package Booster;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkTaskContext;


/**
 * Task for PostgreSQL sink connector for DeviceData
 */
public class DeviceDataSinkTask extends SinkTask {
  
	//Database host server property key
	public static final String HOST_CONFIG = "db.host";

	//Database name property key
	public static final String DATABASE_CONFIG = "db.database";

	//Database username property key
	public static final String USER_CONFIG = "db.username";

	//Database password property key
	public static final String PASSWORD_CONFIG = "db.password";

	//Table name property key
	public static final String TABLE_CONFIG = "db.table";
	
	public class UpdateTime {
		public String updateTimeSql;
	    public String insertTimeSql;	    
	}
	
	ArrayList<String> sqlList = new ArrayList<String>();
	ArrayList<String> fuelList = new ArrayList<String>();
	
	UpdateTime updateTimeObject = new UpdateTime();
	
	DBConnect newConnection = new DBConnect();
	
	@SuppressWarnings("unused")
	private SinkTaskContext TaskContext;

	//Return connector version
	@Override
	public String version() {
		return DeviceDataSinkConnect.VERSION;
	}
	
	//Initialise sink task
	@Override
	public void initialize(SinkTaskContext context) {
		TaskContext = context;//save task context
	}
  
	//Start the task
	@Override
	public void start(Map<String, String> props) throws ConnectException {
    
	    System.out.println("Starting");
	    
	    /* log connector configuration */
	    String configuration="\n";
	    configuration = configuration + '\t' + HOST_CONFIG + ':' + props.get(HOST_CONFIG)+'\n';
	    configuration = configuration + '\t' + DATABASE_CONFIG + ':' + props.get(DATABASE_CONFIG)+'\n';
	    configuration = configuration + '\t' + TABLE_CONFIG + ':' + props.get(TABLE_CONFIG)+'\n';
	    configuration = configuration + '\t' + USER_CONFIG + ':' + props.get(USER_CONFIG)+'\n';
	    configuration = configuration + '\t' + PASSWORD_CONFIG + ':' + props.get(PASSWORD_CONFIG)+'\n';
	    System.out.println("Device Data Sink connector configuration: " + configuration);
	    
	    try {
		    /* get configuration properties */
		    @SuppressWarnings("unused")
			String host=props.get(HOST_CONFIG);
		    String database=props.get(DATABASE_CONFIG);
		    String table=props.get(TABLE_CONFIG);
		    @SuppressWarnings("unused")
			String username=props.get(USER_CONFIG);
		    @SuppressWarnings("unused")
			String password=props.get(PASSWORD_CONFIG);
		     
		    /* validate configuration */
		    if (database==null) throw new ConnectException("Database not configured");
		    if (table==null) throw new ConnectException("Table not configured");
	   
		    newConnection.connect();
        } catch (NumberFormatException exception) {
        	  throw new ConnectException(exception);
        }
  }
	
  /**
   * Parses JSON value in each record and appends JSON elements to the table
   */
	@Override
	public void put(Collection<SinkRecord> sinkRecords) throws ConnectException {
    
		for (SinkRecord record : sinkRecords) {
			System.out.print("Put message: " + (String)record.value() + "\n");
			
			System.out.println("PARSING");;
			JsonReader jsonReader1 = Json.createReader(new StringReader((String) record.value())); 
			JsonObject jsonObj = jsonReader1.readObject();
			String deviceId = jsonObj.getString("deviceId");
			String deviceName = jsonObj.getString("deviceName");
			System.out.println("Device ID and Name" + deviceId + " " + deviceName);
			 
			JsonObject payloadObj = jsonObj.getJsonObject("payload");
			JsonArray telemetryArray = payloadObj.getJsonArray("messages");
			if (telemetryArray != null) { 
				System.out.println("Device ID and Name" + deviceId + " " + deviceName 
									+ " # of records " + telemetryArray.size());

				for (int j=0; j<telemetryArray.size(); j++){
					double lat = 0;
					double lon = 0;
					
					JsonObject featobj = telemetryArray.getJsonObject(j);
					String clientid = featobj.getString("id");
				    String timestamp = featobj.getString("timestamp");
				     
				    JsonObject data = featobj.getJsonObject("data");
				    if (data != null) {
				    	JsonObject location = data.getJsonObject("location");
				    	if (location != null) {
				    		String locationtype = location.getString("type");
				    		System.out.print("locationType: " + locationtype + "\n");
				     
				    		JsonArray coordinatesArray = location.getJsonArray("coordinates");
				    		lat  = coordinatesArray.getJsonNumber(0).doubleValue();
				    		lon  = coordinatesArray.getJsonNumber(1).doubleValue();
				    	}
				    }
				    
					double calculatedLoadValue = 0;
					double massAirFlow = 0;
					int    vehicleSpeed = 0;
					
					javax.json.JsonNumber numObj = data.getJsonNumber("calculatedLoadValue");
					if (numObj != null) {
						calculatedLoadValue = numObj.doubleValue();
					}
					numObj = data.getJsonNumber("vehicleSpeed");
					if (numObj != null) {
						vehicleSpeed = numObj.intValue();
					}
					numObj = data.getJsonNumber("massAirFlow");
					if (numObj != null) {
						massAirFlow = numObj.doubleValue();
					}
				    System.out.printf("Record number: %d clientid: %s timestamp: %s load: %f vehicleSpeed:%d mass:%f " +
				    				  "lat: %f lon: %f\n", j,
				    				   clientid, timestamp, calculatedLoadValue, vehicleSpeed, massAirFlow, lat, lon);			        
			        
			        String sql = "INSERT INTO deviceDataTable (deviceId, deviceName, sampleTimeStamp, latitude, longitude, geom, calculatedLoadValue, vehicleSpeed, massAirFlow) "
			        		 + "VALUES (" 
			        		 + "'" + deviceId + "'" + ","
			        		 + "'" + deviceName + "'" + "," 
			        		 + "'" + timestamp + "'" + ","
			        		 +  lat + "," 
			        		 +  lon + ","
			        		 +  "'POINT("+ lat + " " + lon + ")'" + "," 
			        		 +  calculatedLoadValue + ","
			        		 +  vehicleSpeed + ","
			        		 +  massAirFlow  + ");";
			        
			        System.out.println(sql);
			        sqlList.add(sql);
			        
			        if (j == 0) {
			        	String updateTimeSql = "UPDATE deviceTimeTable SET (latestTimeStamp) = "
			      		        + "('" + timestamp+ "')" 
			      		        + "," + "latitude = " +  lat 
			      		        + "," + "longitude = " + lon 
			      		        + "," + "geom = " + "'POINT("+ lat + " " + lon + ")'"
			      		        + " where" +
			      		        " deviceid = "
			      		        + "'" + deviceId + "'" + ";";
			        	System.out.println(updateTimeSql);
			        	
			        	String insertTimeSql = "INSERT INTO deviceTimeTable (deviceId, deviceName, latestTimeStamp, latitude, longitude, geom) "
			        			+ "VALUES (" 
			        			+ "'" + deviceId + "'" + ","
			        			+ "'" + deviceName + "'" + "," 
			        			+ "'" + timestamp + "'" + ","
				        		+ lat + "," 
				        		+ lon + ","
				        		+ "'POINT("+ lat + " " + lon + ")'" + ");";
			        	System.out.println(insertTimeSql);
			        	
			        	updateTimeObject.updateTimeSql = updateTimeSql;
			        	updateTimeObject.insertTimeSql = insertTimeSql;
			        }
				}
			}
		}
	}

	//Flushes content to the database
	@Override
	public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) throws ConnectException {
 
		System.out.print("Flush start at "+System.currentTimeMillis());
		if (sqlList != null) {
			System.out.print(sqlList.size());
			for(int j=0; j < sqlList.size(); j++){
				try {
					newConnection.insertDB(sqlList.get(j));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (updateTimeObject.updateTimeSql != null && !updateTimeObject.updateTimeSql.isEmpty() && 
				updateTimeObject.insertTimeSql != null && !updateTimeObject.insertTimeSql.isEmpty()) {
		 		try {
		 			System.out.println("UPDATE: " + updateTimeObject.updateTimeSql);
		 			System.out.println("INSERT: " + updateTimeObject.insertTimeSql);
		 			newConnection.updateDeviceTime(updateTimeObject);
		 		} catch (SQLException e) {
					e.printStackTrace();
		 		}
			}
			
			// Clear the Arraylist and the updateDeviceTimeObject
			sqlList.clear() ;
			updateTimeObject.updateTimeSql = "";
			updateTimeObject.insertTimeSql = "";
			System.out.print("Flush stop at " + System.currentTimeMillis());
		}   
	  }
		
	//Stop the sink task
	@Override
	public void stop() throws ConnectException {
		System.out.print("Stopping");
	}
}