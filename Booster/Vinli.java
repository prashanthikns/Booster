package Booster;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import javax.json.*;

public class Vinli {
	
	HashMap<String, DeviceData> vinliDeviceMap = new HashMap<String, DeviceData>();;
	
	public void getDeviceList(Properties vinliProperties) {
		
		String stringUrl = "https://platform.vin.li/api/v1/devices";	
		InputStream inputStreamReader = UrlConnection.setConnection(stringUrl, vinliProperties);    
        
		if (inputStreamReader != null) {
		    JsonReader jsonReader = Json.createReader(inputStreamReader); 
			JsonObject deviceListObj = jsonReader.readObject(); 
			JsonArray array = deviceListObj.getJsonArray("devices");
			for(int i=0; i<array.size();i++) {
				JsonObject deviceObj = array.getJsonObject(i);
				String deviceId = deviceObj.getString("id");
				String deviceName = deviceObj.getString("name");
				System.out.println(deviceId + " " + deviceName);	
				if (vinliDeviceMap.get(deviceId) == null) {
					DeviceData newDevice = new DeviceData(deviceId, deviceName, 0);
					vinliDeviceMap.put(deviceId, newDevice);
				}
			}
			try {
				inputStreamReader.close();   
			} catch(IOException io) {
				System.out.println(io.getMessage());
				io.printStackTrace();
			}
		}
	}		
}
