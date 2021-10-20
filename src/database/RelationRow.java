package database;

import java.util.ArrayList;

/*
 * RELATIONROW CLASS:
 * 
 * used in the creation of TableViews in DatabaseUI for 
 * a selected relation
 * 
 * can be seen as a default/blueprint row in a given relation
 * of up to nine attributes/columns
 */

public class RelationRow {
	private String[] attributes = new String[10];
	private String[] attributeNames = new String[] {"attribute0",
			"attribute1",
			"attribute2",
			"attribute3",
			"attribute4",
			"attribute5",
			"attribute6",
			"attribute7",
			"attribute8",
			"attribute9"};
	private String attribute0;
	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String attribute5;
	private String attribute6;
	private String attribute7;
	private String attribute8;
	private String attribute9;
	
	
	
	//CONSTRUCTOR
	
	public RelationRow(ArrayList<String> attributes) {	
		for(int i = attributes.size(); attributes.size() <= 10; i++)
		{
			attributes.add(i, "");
		}
		
		for(int i = 0; i < 9; i++)
		{
			this.attributes[i] = attributes.get(i);
		}
		
		
		this.setAttributes();	
	}
	
	
	
	/*
	 * setAttributes method:
	 * 
	 * sets attribute values from constructor into
	 * actual stored variables for the class, this is needed
	 * in the creation of the tableview representing the selected
	 * relation
	 * 
	 * called in constructor
	 */
	private void setAttributes()
	{
		attribute0 = attributes[0];
		attribute1 = attributes[1];
		attribute2 = attributes[2];
		attribute3 = attributes[3];
		attribute4 = attributes[4];
		attribute5 = attributes[5];
		attribute6 = attributes[6];
		attribute7 = attributes[7];
		attribute8 = attributes[8];
		attribute9 = attributes[9];
	}
	
	
	
	//GETTERS AND SETTERS
	
	public String[] getAttributeNames() {
		return attributeNames;
	}


	public void setAttributeNames(String[] attributeNames) {
		this.attributeNames = attributeNames;
	}

	public String[] getAttributes() {
		return attributes;
	}

	public void setAttributes(String[] attributes) {
		this.attributes = attributes;
	}

	public String getAttribute0() {
		return attribute0;
	}

	public void setAttribute0(String attribute0) {
		this.attribute0 = attribute0;
	}

	public String getAttribute1() {
		return attribute1;
	}

	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}

	public String getAttribute2() {
		return attribute2;
	}

	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}

	public String getAttribute3() {
		return attribute3;
	}

	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}

	public String getAttribute4() {
		return attribute4;
	}

	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}

	public String getAttribute5() {
		return attribute5;
	}

	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}

	public String getAttribute6() {
		return attribute6;
	}

	public void setAttribute6(String attribute6) {
		this.attribute6 = attribute6;
	}

	public String getAttribute7() {
		return attribute7;
	}

	public void setAttribute7(String attribute7) {
		this.attribute7 = attribute7;
	}

	public String getAttribute8() {
		return attribute8;
	}

	public void setAttribute8(String attribute8) {
		this.attribute8 = attribute8;
	}

	public String getAttribute9() {
		return attribute9;
	}

	public void setAttribute9(String attribute9) {
		this.attribute9 = attribute9;
	}
	
}
