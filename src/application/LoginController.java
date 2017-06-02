package application;

import javafx.collections.ObservableList;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

public class LoginController {
	@FXML private TextField usernamebox;
	@FXML private Button loginbutton;

	private ObservableList<User> userlist;

	public void start(Stage primaryStage) {
		userlist = IO.loadUserList();
		loginbutton.setOnAction((event) -> login(primaryStage));
	}

	private void login(Stage primaryStage) {
		User currUser = null;
		String username = usernamebox.getText();
		if (username.compareTo("admin")==0) {
			adminLogin(primaryStage);
			return;
		}
		for(User u : userlist) if (username.compareTo(u.getName())==0) currUser = u;
		if (currUser==null) {
			IO.popup("Error", "Username Not Found", "This username is invalid.");
		} else {
			userLogin(primaryStage, currUser);
		}
	}

	private void userLogin (Stage primaryStage, User user) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/user.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			UserController uv = loader.getController();
			uv.setUser(user);
			uv.start(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Photo Album -- " + user.getName() + "'s Albums");
		} catch(Exception e) {
			IO.popup("Error", "Failed to load user view", "");
			e.printStackTrace();
		}
	}

	private void adminLogin(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/admin.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			AdminController av = loader.getController();
			av.setUserlist(userlist);
			av.start(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Photo Album -- Admin Subsystem");
		} catch(Exception e) {
			IO.popup("Error", "Failed to load admin view", "");
			e.printStackTrace();
		}
	}
}
