package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/*
 * USER CLASS:
 * 
 * used to manage a current session of
 * DatabaseUI, a instance of user is created for ever login
 * 
 * contains methods that are used by DatabaseUI to execute update
 * and query statements to database
 * 
 * stores:
 * boolean admin that sets whether user is admin or not
 * username and password String
 * Connection connection that connects program to DB
 * 
 */

public class User {
	private boolean admin;
	private String userName, passWord;
	private Connection connection;
		
	
	
	//CONSTRUCTORS
	
	public User(String user, String pass)
	{
		this.userName = user;
		this.passWord = pass;
		try {
			setConnection(DBConnector.getConnection());
		} catch (SQLException e) {
			System.out.println("Could not establish connection with database.\n");
			e.printStackTrace();
		}
	}
	
	
	public User(String user, String pass, boolean admin) 
	{
		this.userName = user;
		this.passWord = pass;
		this.admin = admin;
		try {
			setConnection(DBConnector.getConnection());
		} catch (SQLException e) {
			System.out.println("Could not establish connection with database.\n");
			e.printStackTrace();
		}
	}
	
	
	
	//METHODS
	
	/*
	 * addUserToDatabase method:
	 * 
	 * takes array of user info and sends statement
	 * to database to add a new entry to users table
	 */
	public void addUserToDatabase(String[] info) 
	{
		try {
			connection.createStatement().execute("INSERT INTO `adminusers` (`Username`, `Password`, `Admin`) VALUES ('"+info[0]+"', '"+info[1]+"', '"+info[2]+"')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * deleteRelation() method:
	 * 
	 * deletes a relation from database based on passed
	 * information gotten from DatabaseUI
	 */
	public void deleteRelation(String relationForDeletion) {
		try {
			connection.createStatement().executeUpdate("DROP TABLE " +relationForDeletion);
		} catch (Exception e) {
			System.out.println("ERROR could not delete relation from database");
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * removeEntry() method:
	 * 
	 * removes a row in a database relation based on passed
	 * information gotten from DatabaseUI
	 */
	public void removeEntry(Relation currentRelation, String rowKey){
		try {
			connection.createStatement().executeUpdate("DELETE FROM `" +currentRelation.getRelationName()+ "` WHERE `" +currentRelation.getAttributes().get(0)+ "` = '" +rowKey+ "'");
		} catch (Exception e) {
			System.out.println("ERROR deleting entry from database");
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * editEntry() method:
	 * 
	 * updates a row in a database relation based on passed
	 * information gotten from DatabaseUI
	 */
	public void editEntry(Relation currentRelation, ArrayList<String> newEntryInfo) {
		
		try {
			for(int i = 0; i < newEntryInfo.size();i++) 
			{
				connection.createStatement().executeUpdate("UPDATE " +currentRelation.getRelationName()+ "\nSET `" +currentRelation.getAttributes().get(i)+ "` = '" +newEntryInfo.get(i)+ "'\nWHERE `" +currentRelation.getAttributes().get(0)+ "` = \"" +newEntryInfo.get(0)+ "\"");
			}
		} catch (Exception e) {
			System.out.println("ERROR adding entry");
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * addEntry() method:
	 * 
	 * inserts a row in database relation based on passed
	 * information gotten from DatabaseUI
	 */
	public void addEntry(Relation currentRelation, ArrayList<String> newEntryInfo) {
		
		try {
			connection.createStatement().executeUpdate("INSERT INTO " + currentRelation.getRelationName() + "(`" + currentRelation.getAttributes().get(0) + "`) VALUES(\"" + newEntryInfo.get(0) + "\")");
			for(int i = 1; i < newEntryInfo.size();i++) 
			{
				connection.createStatement().executeUpdate("UPDATE " +currentRelation.getRelationName()+ "\nSET `" +currentRelation.getAttributes().get(i)+ "` = '" +newEntryInfo.get(i)+ "'\nWHERE `" +currentRelation.getAttributes().get(0)+ "` = \"" +newEntryInfo.get(0)+ "\"");
			}
		} catch (Exception e) {
			System.out.println("ERROR adding entry");
			e.printStackTrace();
		}
	}
	
	
	
	/*
	 * getAttributesFromRelation() method:
	 * 
	 * return list of atrributes/column names in 
	 * String array for passed relation
	 */
	public ArrayList<String> getAttributesFromRelation(String relationName) {
		ArrayList<String> attributes = new ArrayList<String>();
		try
		{
			ResultSet attributeRS = connection.createStatement().executeQuery("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '"+relationName+"'");
			while(attributeRS.next())
			{
				attributes.add(attributeRS.getString(1));
			}
			return attributes;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
			
		}				
	}
	
	
	
	/*
	 * createTable() method:
	 * 
	 * creates a new table/relation in database
	 * 
	 * done using an update statement constructed from passed
	 * variables
	 */
	public void createTable(ArrayList<String> arrayList) {
		if(!arrayList.get(1).equals("badTable"))
		{
			String sql = "CREATE TABLE " +arrayList.get(1)+ "\n(";	
			for(int i = 2; i < arrayList.size();i++)
			{
				sql = sql + arrayList.get(i) + " VARCHAR(15) NULL,\n";
			}
			sql = sql + "PRIMARY KEY(" +arrayList.get(2)+ "));";
			try {
				connection.createStatement().executeUpdate(sql);
			} catch (Exception e) {
				System.out.println("ERROR adding new table to database");
				e.printStackTrace();
			}
		}
		return;
	}
	
	
	
	/*
	 * createTableView method():
	 * 
	 * creates tableView foundation in DatabaseUI
	 * 
	 * returns tableview with custom columns constructed
	 * from passed information
	 */
	@SuppressWarnings("unchecked")
	public TableView<RelationRow> createTableView(ArrayList<String> info, String[] variables) {
		TableView<RelationRow> result = new TableView<RelationRow>();
		TableColumn<RelationRow, String>[] columns = new TableColumn[10];
		
		for(int i = info.size(); info.size() <= 10; i++)
		{
			info.add(i, "");
		}
		
		for(int i = 0;i < 10;i++) {
			columns[i] = new TableColumn<RelationRow, String>(info.get(i));
			columns[i].setCellValueFactory(new PropertyValueFactory<RelationRow, String>(variables[i]));	
		}
		
		result.getColumns().addAll(columns);
		
		return result;
	}
	
	
	
	/*
	 * getEntryInfo method():
	 * 
	 * returns an arraylist of type string filled with information
	 * pertaining to a particular row in a relation
	 * 
	 * the arraylist is populated using an query statement constructed from
	 * the passed variables
	 */
	public ArrayList<String> getEntryInfo(Relation currentRelation, String rowKey) {
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM `" +currentRelation.getRelationName()+ "` WHERE `" +currentRelation.getAttributes().get(0)+ "` = \"" +rowKey+ "\"");
			
			while(rs.next())
			{
				for(String x : currentRelation.getAttributes())
				{
					if(!x.equals(""))
					{
						result.add(rs.getString(x));
					}				
				}
			}
		} catch (SQLException e) {
			System.out.println("ERROR in getting info from database");
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	/*
	 * checkAdmin method:
	 * 
	 * returns boolean representing whether a user
	 * is or is not an admin
	 */
	public boolean checkAdmin(String user) 
	{
		boolean result = false;
		try 
		{
			ResultSet adminResult = connection.createStatement().executeQuery("SELECT Admin FROM adminusers WHERE Username = " + "\"" + user + "\"");
			while(adminResult.next())
			{
				result = Boolean.parseBoolean(adminResult.getString(1));
			}
			if(result)
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (Exception e) 
		{
			System.out.println("Admin check failed");
			e.printStackTrace();
			return false;				
		}
	}

	
	
	/*
	 * checkLogin() method:
	 * 
	 * checks if matching username and password are in
	 * database returns boolean indicating result
	 */
	public boolean checkLogin(String userName, String passWord) 
	{
		try 
		{
			ResultSet usernameRS = connection.createStatement().executeQuery("SELECT Username FROM adminusers");
			while(usernameRS.next())
			{
				if (userName.equals(usernameRS.getString(1))) 
				{
					ResultSet passwordRS = connection.createStatement().executeQuery("SELECT Password FROM adminusers WHERE Username = " + "\"" + userName + "\"");
					while(passwordRS.next())
					{
						if (passWord.equals(passwordRS.getString(1)))
						{
							return true;
						}
					}
				}
			}
			return false;
			
		} 
		catch (SQLException e)
		{
			System.out.println("Error getting usernames and passwords from database");
			return false;			
		}
	
	}
	

	
	/*
	 * getRelationSet() method:
	 * 
	 * returns ObservableList of relationRow objects
	 * filled with respective information from datatbase
	 */
	public ObservableList<RelationRow> getRelationSet(ArrayList<String> attributes, String relation){
		ObservableList<RelationRow> oblist = FXCollections.observableArrayList();

		try {
			ResultSet rs = connection.createStatement().executeQuery("select * from " + relation);
			while(rs.next()){
				ArrayList<String> relationRowInfo = new ArrayList<String>();
	    		
				for(int i = 0; i < attributes.size();i++)
				{
					if(!attributes.get(i).equals(""))
					{
						relationRowInfo.add(rs.getString(attributes.get(i)));
					}				
				}
				RelationRow relationRow = new RelationRow(relationRowInfo);
				
				oblist.add(relationRow);
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
    	
    	return oblist;
	}
	

	
	//GETTERS AND SETTERS

	public String getUserName() {
		return this.userName;
	}
	
	public void setUserName(String user) {
		this.userName = user;
	}
	
	public String getPassWord() {
		return this.passWord;
	}
	public void setPassWord(String pass) {
		this.passWord = pass;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public boolean getAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
}
