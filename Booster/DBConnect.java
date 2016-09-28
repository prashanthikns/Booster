package Booster;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	
	private Connection con;
	
    public void connect() {      
    	try {
	  		Class.forName("org.postgresql.Driver");
	  	  	System.out.println("PostgreSQL JDBC Driver Registered!");
	  		con = DriverManager
	              .getConnection("jdbc:postgresql://ec2-54-70-148-118.us-west-2.compute.amazonaws.com:5432/mydb1",
	              "test1", "test1");
	         con.setAutoCommit(true);
	         System.out.println("Opened database successfully");
	    } catch (Exception e) {
	         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         System.exit(0);
	    }
    }
   
    public void insertDB(String sql) throws SQLException{
    	Statement stmt = con.createStatement(); 
        try {
        	stmt.executeUpdate(sql);
            System.out.println("Records inserted successfully");
            stmt.close();	
        } catch(SQLException e) {
            System.out.println("DeviceDataTable Insert Failed: " + sql);
        	System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());	           
       }
       return;
    }
    
    public void updateDeviceTime(DeviceDataSinkTask.UpdateTime updateTimeRecord) throws SQLException { 	   
    	int updateRowCount = 0;
    	try {
        	Statement stmt = con.createStatement(); 
            System.out.println(updateTimeRecord.updateTimeSql);
        	updateRowCount = stmt.executeUpdate(updateTimeRecord.updateTimeSql);
	        stmt.close();
        } catch(SQLException e) {
        	System.out.println("Calling update in deviceTimeTable failed: " + updateTimeRecord.insertTimeSql);
        	System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());	                  	
        }
    	
        if (updateRowCount == 0) {
        	try {
        		// No rows matched, not error
        		Statement insertStmt = con.createStatement();
	  	  		insertStmt.executeUpdate(updateTimeRecord.insertTimeSql);
	  	  		insertStmt.close();
	  	        System.out.println("Records inserted successfully into deviceTimeTable");
        	} catch(SQLException e) {
        		System.out.println("Calling insert in deviceTimeTable failed: " + updateTimeRecord.insertTimeSql);
        		System.err.println("Error Code: " + e.getErrorCode());
                System.err.println("Message: " + e.getMessage());	           
        	}
        }
    }
    
    public void insertFuelDB(String fuelSql) throws SQLException{
    	Statement stmt = con.createStatement(); 
        try {
        	stmt.executeUpdate(fuelSql);
            System.out.println("Records inserted successfully into fuelTable");
            stmt.close();	
        } catch(SQLException e) {
            System.out.println("fuelTable Insert Failed: " + fuelSql);
        	System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());	           
       }
       return;
    }
   
}
    
   