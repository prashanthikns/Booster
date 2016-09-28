package Booster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

/**
 * Kafka device data sink connector for PostgreSQL
 * 
 */
public class DeviceDataSinkConnect extends SinkConnector {
	
	//Version of the connector
	public final static String VERSION="1.0a"; 
  
	//Configuration properties for the connector
	private Map<String, String> dbProperties;
  
	@Override
	public String version() {
		return VERSION;
	}
	 
	//Initialise the connector
	@Override
	public void initialize(ConnectorContext ctx) {
		//do nothing    
	}
  
	//Start the connector
	@Override
	public void start(Map<String, String> props) {
		dbProperties = props;
	}

	//Stop the connector
	@Override
	public void stop() {
		//do nothing
	}
  
	//Returns class of task
	@Override
	public Class<? extends Task> taskClass() {
		return DeviceDataSinkTask.class;
	}
 
	/**
	 * Returns task configurations
	 * @param maxTasks maximum tasks to execute
	 * @return task configurations
	 */
	@Override
	public List<Map<String, String>> taskConfigs(int maxTasks) {
    
		ArrayList<Map<String, String>> configurations = new ArrayList<>();
      
		for (int i = 0; i < maxTasks; i++) {
			configurations.add(dbProperties);
		}
		return configurations;
	}

	@Override
	public ConfigDef config() {
		// TODO Auto-generated method stub
		return null;
	}
}