package database;

import java.util.ArrayList;

/*
 * RELATION CLASS:
 * 
 * used to manage and keep track of current relations
 * 
 * any time a new relation is created/read from the DB
 * a instance of this class is created to represent and
 * keep track of it
 */

public class Relation {
	private boolean admin;
	private String relationName;
	private String displayName;
	private ArrayList<String> attributes;
	
	
	
	//CONSTRUCTOR
	
	public Relation(String relationName, boolean admin)
	{
		this.relationName = relationName;
		this.admin = admin;
	}
	
	
	
	/*
	 * changeName method:
	 * 
	 * changes raw relation name from database into a presentable 
	 * form for DatabaseUI, particularly remove "admin" from the 
	 * front of admin relations
	 */
	public void changeName(){
		if(this.relationName.contains("admin")) {
			
			String[] temp = this.relationName.split("n", 2);
			char[] tempCharArray = temp[1].toCharArray();
			tempCharArray[0] = Character.toUpperCase(tempCharArray[0]);
			this.displayName = String.valueOf(tempCharArray);
		}
		else if(!this.relationName.equals("databaseaccounts"))
		{
			char[] tempCharArray = this.relationName.toCharArray();
			tempCharArray[0] = Character.toUpperCase(tempCharArray[0]);
			this.displayName = String.valueOf(tempCharArray);
		}
		else
		{
			this.displayName = "Accounts";
		}
		
	}
	
	
	
	
	//GETTERS AND SETTERS
	
	public boolean getAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public ArrayList<String> getAttributes() {
		return attributes;
	}


	public void setAttributes(ArrayList<String> attributes) {
		this.attributes = attributes;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getRelationName() {
		return relationName;
	}

	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
}
