package Booster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BoosterMain {

	public static void main(String[] args) throws IOException, SQLException, java.text.ParseException, InterruptedException{
		
		System.setProperty("javax.net.ssl.trustStore", "/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/security/cacerts");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		
		if (args.length != 1) {
			System.out.println("Missing properties file in the arguments.");
			return;
		}
		
		Properties vinliProperties = new Properties();
		try {
			FileInputStream vinliConfigFile = new FileInputStream(args[0]);
			vinliProperties.load(vinliConfigFile);
			vinliConfigFile.close();
			String APP_ID = vinliProperties.getProperty("appId");
			String APP_SECRET = vinliProperties.getProperty("appSecret");
			
			if (APP_ID == null) { 
				System.out.println("Missing property: appId in properties file " + args[0]);
			}
			if (APP_SECRET == null) {
				System.out.println("Missing property: appSecret in properties file " + args[0]);
			}
		} catch(FileNotFoundException f) {
			System.out.println(f.getMessage());
			f.printStackTrace();	
		}
		
		Vinli vinliDevice = new Vinli();
		System.out.println("Calling fuel data");
		FuelRequest f = new FuelRequest();
		f.getFuelData();

		while (true) {
			vinliDevice.getDeviceList(vinliProperties);
			
			for (String key : vinliDevice.vinliDeviceMap.keySet()) {	
				vinliDevice.vinliDeviceMap.get(key).getTelemetryData(vinliProperties);
			}
			TimeUnit.SECONDS.sleep(1);
		}	
	}
}

		