package database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 * DATABASEUI CLASS:
 * 
 * JavaFX class that extends application to create Database's
 * UI
 * 
 * Three main windows, one for loggin into the database, one to display
 * a list of relations currently in the database, and one to view/edit a given
 * relation from the database and popups using alertText(), newTableDialog(),
 * and newUserDialog()
 * 
 * Contains all event handlers for UI that call specific User
 * methods that execute update and query statements to the database
 */

public class DatabaseUI extends Application{
	private final String adminPass = "1700";
	private ArrayList<Relation> currentRelations;
	private Relation currentRelation;
	private Stage primaryStage = new Stage();	
	private Button backButton = new Button("Back");
	private Button addButton = new Button("Add");
	private Button editButton = new Button("Edit");
	private Button removeButton = new Button("Remove");
	private User user;
	
	
	
	//START METHOD
	
	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		primaryStage.setTitle("Welcome");
		
		
		
		//LOGIN WINDOW GUI
		
		GridPane loginGridPane = new GridPane();
		loginGridPane.setAlignment(Pos.CENTER);
		Text loginText = new Text("LOGIN");
		Text userNameText = new Text("Username: ");
		TextField userNameField = new TextField();
		Text passWordText = new Text("Password: ");
		TextField passWordField = new TextField();
		loginGridPane.add(userNameText, 0, 1);
		loginGridPane.add(userNameField, 1, 1);
		loginGridPane.add(passWordText, 0, 2);
		loginGridPane.add(passWordField, 1, 2);
		HBox loginHBox = new HBox();
		loginHBox.setSpacing(50);
		loginHBox.setAlignment(Pos.CENTER);
		Button newUserButton = new Button("New User");
		Button loginButton = new Button("Login");
		loginHBox.getChildren().addAll(newUserButton, loginButton);	
		BorderPane loginBorderPane = new BorderPane();
		BorderPane.setAlignment(loginText, Pos.CENTER);
		loginBorderPane.setCenter(loginGridPane);
		loginBorderPane.setBottom(loginHBox);
		loginBorderPane.setTop(loginText);	
		Scene scene = new Scene(loginBorderPane, 250, 150);
		
		
		
		//RELATIONS WINDOW GUI
		
		BorderPane relationsBorderPane = new BorderPane();
		ListView<String> relationsListView = new ListView<String>();		
		relationsListView.setPadding(new Insets(10, 10, 10, 10));
		Text relationsWindowTitle = new Text("Relations Avaliable: ");
		BorderPane.setAlignment(relationsWindowTitle, Pos.CENTER);
		HBox bottomRelationListButtons = new HBox();
		Button logoutButton = new Button("Log Out");
		Button newRelationButton = new Button("New Relation");
		Button deleteRelationButton = new Button("Delete Relation");
		relationsBorderPane.setCenter(relationsListView);
		relationsBorderPane.setTop(relationsWindowTitle);
		relationsBorderPane.setBottom(bottomRelationListButtons);
		Scene relationListScene = new Scene(relationsBorderPane, 250, 250);
		
		
		
		//Setting initial scene
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
		
		//EVENT HANDLERS
		
		/*
		 * logout button event handler:
		 * 
		 * sends user back to login screen
		 */
		logoutButton.setOnAction(event->{
			primaryStage.setTitle("Welcome");
			primaryStage.setScene(scene);
		});
		
		
		
		
		/*
		 * back button event handler:
		 * 
		 * sends user back to relations list screen
		 */
		backButton.setOnAction(event->{
			primaryStage.setTitle("Relations");
			primaryStage.setScene(relationListScene);
		});
		
		
		
		/*
		 * new user button event handler:
		 * 
		 * creates new user object and enters new user
		 * entry into adminusers table in database using
		 * array of strings retrieved from newUserDialog()
		 */
		newUserButton.setOnAction(event->{
			String[] info = this.newUserDialog();
			if(!info[0].equals("badUser"))
			{
				user = new User(info[0], info[1], Boolean.parseBoolean(info[2]));
				user.addUserToDatabase(info);
			}
		});
		
		
		
		/*
		 * login button event handler:
		 * 
		 * username and password are checked in the database to 
		 * see if matched and relations listview is updated based on admin
		 * privledges of user
		 */
		loginButton.setOnAction(event->{
			user = new User(userNameField.getText(), passWordField.getText());
			user.setAdmin(user.checkAdmin(user.getUserName()));
			bottomRelationListButtons.getChildren().clear();
			
			if(user.checkLogin(user.getUserName(), user.getPassWord()))
			{
				if(user.checkAdmin(user.getUserName()))
				{
					bottomRelationListButtons.getChildren().addAll(logoutButton, newRelationButton, deleteRelationButton);
					relationsListView.getItems().clear();
					currentRelations = this.getRelationsFromDB();
					for(Relation s : currentRelations)
					{
						s.changeName();
						relationsListView.getItems().add(s.getDisplayName());
					}
					primaryStage.setScene(relationListScene);
				}
				else
				{
					bottomRelationListButtons.getChildren().add(logoutButton);
					relationsListView.getItems().clear();
					currentRelations = this.getRelationsFromDB();
					ArrayList<Relation> tempArrayList = new ArrayList<Relation>();
					for(Relation s : currentRelations)
					{
						if(s.getAdmin() == false)
						{
							s.changeName();
							tempArrayList.add(s);
							relationsListView.getItems().add(s.getDisplayName());
						}
						
					}
					currentRelations.clear();
					currentRelations.addAll(tempArrayList);
					primaryStage.setScene(relationListScene);
				}
			}
			else
			{
				alertText("ERROR", "Invalid Login\nPlease check your username and password");
			}
		});
		
		
		
		/*
		 * delete button event handler:
		 * 
		 * deletes relation from database and from currentRelations list
		 * by calling deleteRelation() from user class
		 * 
		 * relations listview is then updated
		 */
		deleteRelationButton.setOnAction(event->{
			String relationForDeletion = relationsListView.getSelectionModel().getSelectedItem();

			for(Relation r: currentRelations)
			{
				if(r.getDisplayName().equals(relationForDeletion))
				{
					relationForDeletion = r.getRelationName();						
				}
			}
			String relationForDeletion2 = relationForDeletion;
			user.deleteRelation(relationForDeletion);
			currentRelations.removeIf(i -> i.getDisplayName() ==  relationForDeletion2);
			if(user.getAdmin())
			{
				relationsListView.getItems().clear();
				currentRelations = this.getRelationsFromDB();
				for(Relation s : currentRelations)
				{
					s.changeName();
					relationsListView.getItems().add(s.getDisplayName());
				}
				primaryStage.setScene(relationListScene);
			}
			else
			{
				relationsListView.getItems().clear();
				currentRelations = this.getRelationsFromDB();
				ArrayList<Relation> tempArrayList = new ArrayList<Relation>();
				for(Relation s : currentRelations)
				{
					if(s.getAdmin() == false)
					{
						s.changeName();
						tempArrayList.add(s);
						relationsListView.getItems().add(s.getDisplayName());
					}
					
				}
				currentRelations.clear();
				currentRelations.addAll(tempArrayList);
				primaryStage.setScene(relationListScene);
			}
		});
		
		
		
		/*
		 * new relation button event handler:
		 * 
		 * creates new relation in database and currentRelations list
		 * by calling createTable() from user class
		 * 
		 * relations listview is then updated
		 */
		newRelationButton.setOnAction(event->{
			this.alertText("Warning", "When creating a table:\n -All columns should be unique\n -First column should be used as key. Meaning each value in first column should be unique.");
			ArrayList<String> newTableDialogInfo = newTableDialog();
			
			user.createTable(newTableDialogInfo);
			if(user.getAdmin())
			{
				relationsListView.getItems().clear();
				currentRelations = this.getRelationsFromDB();
				for(Relation s : currentRelations)
				{
					s.changeName();
					relationsListView.getItems().add(s.getDisplayName());
				}
				primaryStage.setScene(relationListScene);
			}
			else
			{
				relationsListView.getItems().clear();
				currentRelations = this.getRelationsFromDB();
				ArrayList<Relation> tempArrayList = new ArrayList<Relation>();
				for(Relation s : currentRelations)
				{
					if(s.getAdmin() == false)
					{
						s.changeName();
						tempArrayList.add(s);
						relationsListView.getItems().add(s.getDisplayName());
					}
					
				}
				currentRelations.clear();
				currentRelations.addAll(tempArrayList);
				primaryStage.setScene(relationListScene);
			}
			
			if(!newTableDialogInfo.get(1).equals("badTable"))
			{
				if(Boolean.parseBoolean(newTableDialogInfo.get(0)) == true)
				{
					Relation tempRelation = new Relation(newTableDialogInfo.get(1), true);
					tempRelation.changeName();
					currentRelations.add(tempRelation);

				}
				else
				{
					Relation tempRelation = new Relation(newTableDialogInfo.get(1), false);
					tempRelation.changeName();
					currentRelations.add(tempRelation);
				}
			}

			
		});
		

		
		/*
		 * relationsListView double click event handler:
		 * 
		 * displays new relation window depending on which relation
		 * user doubled clicked
		 * 
		 * uses createRelationScene to create scene, then
		 * sets primaryStage to newly created scene
		 */	
		relationsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent click) {
		    	
		    	if (click.getClickCount() == 2) {
		    		
		    		for(Relation r : currentRelations)
		    		{
		    			if (r.getDisplayName().equals(relationsListView.getSelectionModel().getSelectedItem()))
		    			{
		    				currentRelation = r;
		    			}
		    		}
		    		currentRelation.setAttributes(user.getAttributesFromRelation(currentRelation.getRelationName()));
		    		primaryStage.setScene(this.createRelationScene(currentRelation.getRelationName(), currentRelation.getAttributes()));
		    		primaryStage.setTitle(currentRelation.getDisplayName());
		    	}
		        
		    }

		    /*
		     * createRelationScene() method:
		     * 
		     * creates new scene displaying relation data and editing pane
		     * using the passed relationName and attriubutes
		     */
			@SuppressWarnings("unchecked")
			private Scene createRelationScene(String relation, ArrayList<String> attributes) {
				GridPane grid = new GridPane();
				VBox relationPromtpsVBox = new VBox();
				VBox relationInfoVBox = new VBox();
				relationPromtpsVBox.setSpacing(20);
				relationPromtpsVBox.setPadding(new Insets(10, 10, 10, 10));
        		relationInfoVBox.setSpacing(11);
        		relationInfoVBox.setPadding(new Insets(10, 10, 10, 10));
        		
        		Label[] relationLabels = new Label[attributes.size()];
        		TextField[] relationTextFields = new TextField[relationLabels.length];
        		for (int i = 0;i < relationLabels.length;i++)
        		{
        			relationLabels[i] = new Label(attributes.get(i));
        			relationTextFields[i] = new TextField();
        		}
        		
        		relationPromtpsVBox.getChildren().addAll(relationLabels);
        		relationInfoVBox.getChildren().addAll(relationTextFields);
				
        		String[] attributeNames = new String[] {"attribute0",
        				"attribute1",
        				"attribute2",
        				"attribute3",
        				"attribute4",
        				"attribute5",
        				"attribute6",
        				"attribute7",
        				"attribute8",
        				"attribute9"};
        		
        		TableView<RelationRow> tableView = user.createTableView(attributes, attributeNames);
        		tableView.setItems(user.getRelationSet(attributes, relation));
        		tableView.setPrefWidth(500);
        		
        		HBox trioButtonBox = new HBox(addButton, editButton, removeButton);
        		trioButtonBox.setSpacing(20);
        		trioButtonBox.setAlignment(Pos.CENTER);
        		
        		grid.add(tableView, 0, 0);
        		grid.add(backButton, 0, 1);
        		grid.add(trioButtonBox, 2, 1);
        		grid.add(relationPromtpsVBox, 1, 0);
        		grid.add(relationInfoVBox, 2, 0);
        		
				Scene scene = new Scene(grid, 780, 425);
				
				/*
				 * double click entry in tableview event handler:
				 * 
				 * fills fields left to tableview with data of double clicked
				 * entry
				 */
				tableView.setOnMouseClicked(event->{
					TablePosition<RelationRow, String> pos = tableView.getSelectionModel().getSelectedCells().get(0);
					int row2 = pos.getRow();
					RelationRow selectedRow = tableView.getItems().get(row2);
					String rowKey = selectedRow.getAttribute0();
					ArrayList<String> rowInfo = user.getEntryInfo(currentRelation, rowKey);
					for(int i = 0; i < relationTextFields.length;i++)
					{
						relationTextFields[i].setText(rowInfo.get(i));
					}
				});
				
				/*
				 * add button event handler:
				 * 
				 * calls user method addEntry() passing
				 * the info and relation name
				 */
				addButton.setOnAction(event->{
					ArrayList<String> newEntryInfo = new ArrayList<String>();
					for(TextField tf : relationTextFields)
					{
						newEntryInfo.add(tf.getText());
						tf.clear();
					}
					user.addEntry(currentRelation, newEntryInfo);
					tableView.setItems(user.getRelationSet(attributes, relation));
				});
				
				/*
				 * edit button event handler:
				 * 
				 * calls user method editEntry() passing
				 * the entry to be edited and data 
				 */
				editButton.setOnAction(event ->{
					ArrayList<String> newEntryInfo = new ArrayList<String>();
					for(TextField tf : relationTextFields)
					{
						newEntryInfo.add(tf.getText());
						tf.clear();
					}
					user.editEntry(currentRelation, newEntryInfo);
					tableView.setItems(user.getRelationSet(attributes, relation));
				});
				
				/*
				 * remove button event handler:
				 * 
				 * calls user method removeEntry() passing
				 * the entry to be delete 
				 */
				removeButton.setOnAction(event->{
					TablePosition<RelationRow, String> pos = tableView.getSelectionModel().getSelectedCells().get(0);
					int row2 = pos.getRow();
					RelationRow selectedRow = tableView.getItems().get(row2);
					String rowKey = selectedRow.getAttribute0();
					user.removeEntry(currentRelation, rowKey);
					for(TextField tf: relationTextFields)
					{
						tf.clear();
					}
					tableView.setItems(user.getRelationSet(attributes, relation));
				});
				
				return scene;
					
			}
			
		});	
	}
	
	
	
	//METHODS
	
	/*
	 * alertText method:
	 * 
	 * creates pop up window titled with passed title
	 * diplaying passed in message 
	 */
	public void alertText(String title, String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.setGraphic(null);
		ButtonType okay = new ButtonType("Okay");
		alert.getButtonTypes().setAll(okay);
		alert.showAndWait();		
	}
	
	
	
	/*
	 * createTextField method:
	 * 
	 * creates an arraylist of textfields with as many textfields 
	 * as passed amount
	 */
	private ArrayList<TextField> createTextField(int amount) {
		ArrayList<TextField> result = new ArrayList<TextField>();
		
		for(int i = 0; i < amount; i++)
		{
			result.add(new TextField());
		}
		
		return result;
	}
	
	
	
	/*
	 * getRelations method:
	 * 
	 * returns list of relation objects containing
	 * a object for each relation in the database
	 */
	public ArrayList<Relation> getRelationsFromDB() {
		ArrayList<String> relationNames = new ArrayList<String>();
		try {
			ResultSet rs = user.getConnection().createStatement().executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' AND TABLE_SCHEMA = 'database'");
			while(rs.next())
			{
				relationNames.add(rs.getString(1));
			}
		} catch (Exception e) {
			System.out.println("ERROR getting relations in database");
			e.printStackTrace();
		}
		
		ArrayList<Relation> relations = new ArrayList<Relation>();
		
		for(String x: relationNames)
		{
			if(x.contains("admin"))
			{
				relations.add(new Relation(x, true));
			}
			else
			{
				relations.add(new Relation(x, false));
			}
		}
				
		return relations;
	}
	
	
	
	/*
	 * newUserDialog method:
	 * 
	 * creates pop up window asking for username,
	 * password, and admin code if checked
	 * 
	 * a string[3] is returned with,
	 * string[0] = username
	 * string[1] = password
	 * string[3] = adminBoolean
	 * 
	 * if login is not finished/any field is empty,
	 * sting[0] = badUser, and a check is performed in
	 * newUserButton event handler before proceeding
	 */
	public String[] newUserDialog() {
		Alert dialog = new Alert(AlertType.CONFIRMATION);
		dialog.setTitle("New User");
		dialog.getDialogPane().setMinHeight(250);
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		GridPane grid = new GridPane();
		CheckBox admin = new CheckBox("Admin");
		
		grid.add(new Text("Enter Username: "), 0, 0);
		grid.add(new Text("Enter Password: "), 0, 1);
		grid.add(admin, 0, 2);
		TextField username = new TextField();
		TextField password = new TextField();
		TextField adminCode = new TextField();
		
		grid.add(username, 1, 0);
		grid.add(password, 1, 1);
		
		admin.setOnAction(event->{
			if(admin.isSelected())
			{
				grid.add(adminCode, 1, 2);
			}
			else
			{
				grid.getChildren().remove(adminCode);
			}
		});
		
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 35, 20, 35));
		dialog.getDialogPane().setContent(grid);
		
		ButtonType buttonTypeOk = new ButtonType("Okay", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
		
		Optional<ButtonType> result = dialog.showAndWait();
		
		String[] returnInfo = new String[3];
		if (result.isPresent() && result.get() == buttonTypeOk)
		{
			if(username.getText().trim().isEmpty() || password.getText().trim().isEmpty())
			{
				returnInfo[0] = "badUser";
				return returnInfo;
			}
			returnInfo[0] = username.getText();
			returnInfo[1] = password.getText();
			if (admin.isSelected())
			{
				if (adminCode.getText().equals(adminPass)) 
				{
					returnInfo[2] = "true";
				}
				else
				{
					this.alertText("ERROR", "Admin code incorrect.");
					returnInfo[0] = "badUser";
					return returnInfo;
				}
			}
			else
			{
				returnInfo[2] = "false";
			}
			return returnInfo;
		}
		else
		{
			returnInfo[0] = "badUser";
			return returnInfo;
		}
		
	}
	
	
	
	/*
	 * newTableDialog method:
	 * 
	 * creates popup window asking for table name,
	 * column names (up to 9), and whether table is
	 * admin only
	 * 
	 * an arraylist of strings is returned in following format:
	 * 
	 * arraylist.get(0) = true/false based on admin condition
	 * arraylist.get(1) = name of table, if admin table, table 
	 * name gets "admin" attached to front of string ex. "tablename" => "admintablename"
	 * arraylist.get(2-arraylist.size()) = column names
	 * 
	 * if popup window is closed or cancel button clicked
	 * the method returns arraylist.get(0) = "badtable" letting rest of 
	 * program know not to proceed with table creation
	 */
	public ArrayList<String> newTableDialog() {
		Alert dialog = new Alert(AlertType.CONFIRMATION);
		dialog.setTitle("New Table");
		dialog.getDialogPane().setMinHeight(475);
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		GridPane grid = new GridPane();
		
		grid.add(new Text("Enter Table Name: "), 0, 0);
		TextField tableNameTextField = new TextField();
		grid.add(tableNameTextField, 1, 0);
		grid.add(new Text("How many columns? "), 0, 2);
		grid.add(new Label("Admin Table?"), 0, 1);
		CheckBox adminTable = new CheckBox();
		grid.add(adminTable, 1, 1);
		ComboBox<String> comboBox = new ComboBox<String>();
		comboBox.getItems().addAll(
				"1",
				"2",
				"3",
				"4",
				"5",
				"6",
				"7",
				"8",
				"9"
				);
		grid.add(comboBox, 1, 2);

		
		VBox tableColumnsBox = new VBox();
		tableColumnsBox.setSpacing(10);
		ArrayList<TextField> textFields = new ArrayList<TextField>();
		
		comboBox.setOnAction(event->{
			String amountString = comboBox.getValue();
			int amount = Integer.parseInt(amountString);
			textFields.clear();
			textFields.addAll(this.createTextField(amount));
			tableColumnsBox.getChildren().clear();
			tableColumnsBox.getChildren().addAll(textFields);
		});
		
		VBox mainBox = new VBox();
		mainBox.getChildren().addAll(grid, tableColumnsBox);	
			
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 35, 20, 35));
		dialog.getDialogPane().setContent(mainBox);
		
		ButtonType buttonTypeOk = new ButtonType("Create", ButtonData.OK_DONE);
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
		
		Optional<ButtonType> result = dialog.showAndWait();
		
		ArrayList<String> returnInfo = new ArrayList<String>();
		if (result.isPresent() && result.get() == buttonTypeOk)
		{
			if(adminTable.isSelected())
			{
				returnInfo.add("true");
				returnInfo.add("admin" + tableNameTextField.getText());
			}
			else
			{
				returnInfo.add("false");
				returnInfo.add(tableNameTextField.getText());
			}
			
			for(TextField tf : textFields)
			{
				returnInfo.add(tf.getText());
			}
			return returnInfo;
		}
		else
		{
			returnInfo.add("badTable");
			returnInfo.add("badTable");
			return returnInfo;
		}
		
	}
	

	
	//MAIN METHOD
	
	public static void main(String[] args) {
		launch(args);
	}

}
