package Booster;

public class TelemetryRecord {

	String deviceId;
	String deviceName;
	String  sampleTimeStamp;
	double  latitude;
	double  longitude;
	
	public TelemetryRecord(String deviceId, String deviceName, String timestamp,
								double latitude, double longitude){
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.sampleTimeStamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void printTelemetryRecord() {
		System.out.println(this.deviceId+this.deviceName+this.sampleTimeStamp);
		System.out.print(longitude);
		System.out.print(latitude);
		
	}
}
