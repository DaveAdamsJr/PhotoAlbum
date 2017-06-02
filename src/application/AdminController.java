package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;


public class AdminController {
	@FXML private TableView<User> table;
	@FXML private TableColumn<User,String> tcol;
	@FXML private Button deleteuserbutton, createuserbutton, logoutbutton;
	@FXML private TextField newusernamebox;
	private ObservableList<User> userlist;

	public void start(Stage primaryStage) {
		table.setItems(userlist);
		tcol.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
		deleteuserbutton.setOnAction((event) -> deleteUser());
		createuserbutton.setOnAction((event) -> createUser());
		logoutbutton.setOnAction((event) -> logout(primaryStage));
	}

	public void setUserlist(ObservableList<User> ol) { this.userlist = ol; }

	private void logout(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/login.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			LoginController lv = loader.getController();
			lv.start(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Photo Album Login");
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void createUser() {
		String username = newusernamebox.getText();
		newusernamebox.setText("");
		for(User u: userlist) {
			if (username.compareTo(u.getName())==0) {
				IO.popup("Error", "Username Invalid", "This username is already in use.");
				return;
			}
		}
		User newUser = new User(username);
		userlist.add(newUser);
		IO.saveUserList(userlist);
	}

	private void deleteUser() {
		int index = table.getSelectionModel().getSelectedIndex();
		if (userlist.get(index).getName().compareTo("admin")==0) {
			IO.popup("Error", "Cannot delete admin.", "This user is the administrator and cannot be deleted.");
			return;
		}
		userlist.remove(index);
		if (userlist.size()!=0) table.getSelectionModel().select((index!=0) ? (index-1) : 0);
		IO.saveUserList(userlist);
	}
}
