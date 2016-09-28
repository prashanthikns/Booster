package Booster;


import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class FuelRequest {

	public void getFuelData() throws IOException, ParseException, java.text.ParseException {
		String fileName1 = "/home/ubuntu/sampleFuel.txt";	
			
		double lat;
		double lon;
				
		JsonReader jsonReader1 = Json.createReader(new FileReader(fileName1)); 
        JsonObject dataObj = jsonReader1.readObject();
        JsonArray fuelRequestArray = dataObj.getJsonArray("fuelRequest");
        
        if (fuelRequestArray.size() != 0) {
        	System.out.printf("Fuel array size is %d \n", fuelRequestArray.size());
        	for (int fuelIndex = 0; fuelIndex < fuelRequestArray.size(); fuelIndex++){
	        	JsonObject fuelObj = fuelRequestArray.getJsonObject(fuelIndex);
				String id = fuelObj.getString("_id");
				System.out.printf("Fuel id: %s \n", id);
	              
		        JsonObject locationObj = fuelObj.getJsonObject("location");
		    	if (locationObj != null) {
		    		JsonObject geomObj = locationObj.getJsonObject("geometry");
		    		JsonArray coordinatesArray = geomObj.getJsonArray("coordinates");
		    		lat  = coordinatesArray.getJsonNumber(0).doubleValue();
		    		lon  = coordinatesArray.getJsonNumber(1).doubleValue();	
		    		System.out.printf("id: %s  lat: %f lon: %f\n", id, lat, lon);
		    		
		    		 JsonObject toFuelKafka = Json.createObjectBuilder()
			        	     .add("fuelId", id)
			        	     .add("lat", lat)
			        	     .add("lon", lon)
			        	     .build();
			        JsonObject toFuelKafkaKey = Json.createObjectBuilder()
			        		.add("fuelId", id)
			        		.build();
			        
		         System.out.println(toFuelKafkaKey.toString() + toFuelKafka.toString());
		         FuelProducer fuelRequestProducer = new FuelProducer();
		         fuelRequestProducer.sendToFuelKafka(toFuelKafkaKey.toString(), toFuelKafka.toString());
		    	}
        	}
        }
        jsonReader1.close();
	}
}


	
	