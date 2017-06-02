package application;

import java.io.*;
import java.util.*;

import javafx.collections.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.*;

public class IO {
	public static void saveUserList(ObservableList<User> obsList) {
		File datadir = new File("data");
		if (!datadir.exists()) datadir.mkdir();
		File userlist = new File("data/userlist.data");
		userlist.delete();
		List<String> list = new ArrayList<String>();
		for (User u : obsList) list.add(u.getName());
		try {
			userlist.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data/userlist.data"));
			out.writeObject(list);
			out.close();
		} catch (IOException e) {
			popup("Error", "Error Saving User List", "");
			e.printStackTrace();
			return;
		}
	}

	public static void saveUser(User user) {
		File datadir = new File("data");
		if (!datadir.exists()) datadir.mkdir();
		File userfile = new File("data/" + user.getName() + ".data");
		userfile.delete();
		try {
			userfile.createNewFile();
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(userfile));
			out.writeObject(user);
			out.close();
		} catch (IOException e) {
			popup("Error", "Error Saving User List", "");
			e.printStackTrace();
			return;
		}
	}

	public static ObservableList<User> loadUserList() {
		File userlist = new File("data/userlist.data");
		if (!userlist.exists()) return FXCollections.observableArrayList(new User("admin"));
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("data/userlist.data"));
			@SuppressWarnings("unchecked")
			List<String> usernames = (List<String>) in.readObject();
			in.close();
			List<User> users = new ArrayList<User>();
			for (String s : usernames) users.add(loadUser(s));
			return FXCollections.observableArrayList(users);
		} catch (Exception e) {
			popup("Error", "Error loading user list.", "");
			e.printStackTrace();
			return FXCollections.observableArrayList();
		}
	}

	public static User loadUser(String name) {
		File userfile = new File("data/" + name + ".data");
		try {
			userfile.createNewFile();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(userfile));
			User u = (User) in.readObject();
			in.close();
			return u;
		} catch (Exception e) {
			popup("Error", "Error Loading User", "User: " + name);
			e.printStackTrace();
			return null;
		}
	}

	public static void popup(String title, String header, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
