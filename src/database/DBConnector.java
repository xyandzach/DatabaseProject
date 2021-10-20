package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * DBCONNECTOR CLASS:
 * 
 * class that creates a connection that is used 
 * in the User class
 * 
 * references mySQL connector jar file
 */

public class DBConnector {
	
	public static Connection getConnection() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/database","root","");
		
		return connection;
	}
}
