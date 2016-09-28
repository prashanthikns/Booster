package Booster;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.IOException;

public class DeviceData {
	
	private String deviceId;
	private String deviceName;
	private int    TelemetryRecordCount;
	private long   until; 

	public DeviceData(String deviceId, String deviceName, long until) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.until = until;
	}
	
	public void getTelemetryData(Properties vinliProperties) throws IOException, ParseException, java.text.ParseException {
		
        long since = 0;
         
        if (this.until > 0 ) {
        	since = this.until;
        } 
        
        String url = "https://telemetry.vin.li/api/v1/devices/"+deviceId+"/messages?since="+since+"";
		System.out.println("url: " + url);
		InputStream telemetryStreamReader = UrlConnection.setConnection(url, vinliProperties);    
	    		
		if (telemetryStreamReader != null) {
	        JsonReader jsonReader1 = Json.createReader(telemetryStreamReader);
	        JsonObject dataObj = jsonReader1.readObject();
	        JsonArray telemetryArray = dataObj.getJsonArray("messages");
	        
	        System.out.printf("Device ID = %s, # of entries = %d\n", deviceId, telemetryArray.size()); 
	        if (telemetryArray.size() != 0) {
	        	//Grab time stamp only from first entry
				JsonObject featobj = telemetryArray.getJsonObject(0);
				String timestamp = featobj.getString("timestamp");
				SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date ts = fmt.parse(timestamp);
				long time = ts.getTime();
				System.out.println("Timestamp: " + timestamp + " Time: " + time); 	
				
				setUntil(time);
				System.out.println("Latest time stamp for device polling: " + this.getUntil()); 
				this.TelemetryRecordCount+=telemetryArray.size();
	        	System.out.printf("**** # of samples for this device: %d **** \n", this.TelemetryRecordCount);
		        //Build JSON Object, add deviceId, deviceName and attach the telemetry data object
		        JsonObject toKafka = Json.createObjectBuilder()
		        	     .add("deviceId", this.getDeviceId())
		        	     .add("deviceName", this.getDeviceName())
		        	     .add("payload", dataObj)
		        	     .build();
		        JsonObject toKafkaKey = Json.createObjectBuilder()
		        		.add("deviceId", this.getDeviceId())
		        		.build();
		        
		         System.out.println(toKafkaKey.toString() + toKafka.toString());
		         DeviceDataProducer newProducer = new DeviceDataProducer();
		         newProducer.sendToKafka(toKafkaKey.toString(), toKafka.toString());
		         telemetryStreamReader.close();
	        }
        }
	}
		
	public String getDeviceId() {
		return this.deviceId;
	}
	
	public String getDeviceName() {
		return this.deviceName;
	}

	public long getUntil() {
		return this.until;
	}
	
	public long setUntil(long time){
		if (this.until == 0 || this.until < time) {
			this.until = time;  
		}
		return this.until;
	}	
}

