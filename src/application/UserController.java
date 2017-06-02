package application;

import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

public class UserController {
	@FXML private TableView<Album> table;
	@FXML private TableColumn<Album, String> namecol, datecol1, datecol2;
	@FXML private TableColumn<Album, Integer> numcol;
	@FXML private Button openalbumbutton, deletealbumbutton, createalbumbutton, logoutbutton, tagsearchbutton, datesearchbutton;
	@FXML private TextField newalbumbox, tagsearchbox1, tagsearchbox2;
	@FXML private DatePicker datepicker1, datepicker2;
	private User user;

	public void start (Stage primaryStage) {
		initTable();
		openalbumbutton.setOnAction((event) -> openAlbum(primaryStage));
		deletealbumbutton.setOnAction((event) -> deleteAlbum());
		createalbumbutton.setOnAction((event) -> createAlbum());
		tagsearchbutton.setOnAction((event) -> search(primaryStage, false));
		datesearchbutton.setOnAction((event) -> search(primaryStage, true));
		logoutbutton.setOnAction((event) -> logout(primaryStage));
	}

	public void setUser(User user) { this.user = user; }

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

	private void initTable() {
		table.setItems(user.getAlbums());
		table.setEditable(true);
		namecol.setCellValueFactory(new PropertyValueFactory<Album, String>("name"));
		namecol.setCellFactory(TextFieldTableCell.forTableColumn());
		namecol.setOnEditCommit(
			new EventHandler<CellEditEvent<Album,String>>() {
				@Override
				public void handle(CellEditEvent<Album,String> e) {
					user.getAlbums().get(e.getTablePosition().getRow()).setName(e.getNewValue());
					IO.saveUser(user);
				}
			}
		);
		namecol.setEditable(true);
		numcol.setCellValueFactory(new PropertyValueFactory<Album, Integer>("nPhotos"));
		datecol1.setCellValueFactory(new PropertyValueFactory<Album, String>("first"));
		datecol2.setCellValueFactory(new PropertyValueFactory<Album, String>("last"));
	}

	private void openAlbum(Stage primaryStage) {
		int albumnum = table.getSelectionModel().getSelectedIndex();
		if (albumnum<0) return;
		Album a = user.getAlbums().get(albumnum);
		user.loadAlbum(a);
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/album.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			AlbumController av = loader.getController();
			av.setAlbum(a);
			av.setUser(user);
			av.start(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Photo Album -- " + user.getName() + " -- " + a.getName());
		} catch(Exception e) {
			IO.popup("Error", "Error opening album", "");
		}
	}

	private void search(Stage primaryStage, boolean byDate) {
		Album results = new Album("Search Results");
		if (byDate) {
			Date newdate = Date.from(datepicker1.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			Calendar begin = Calendar.getInstance();
			begin.setTime(newdate);
			Date newdate2 = Date.from(datepicker2.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
			Calendar end = Calendar.getInstance();
			end.setTime(newdate2);
			for(Album a : user.getAlbums()) {
				user.loadAlbum(a);
				for(Photo p : a.getPhotos()) {
					if (results.getPhotos().contains(p)) continue;
					if ((p.getDate().compareTo(begin)>0)&&(p.getDate().compareTo(end)<0)) results.addPhoto(p);
				}
			}
		} else {
			String type = tagsearchbox1.getText();
			String value = tagsearchbox2.getText();
			for(Album a : user.getAlbums()) {
				user.loadAlbum(a);
				for(Photo p : a.getPhotos()) {
					if (results.getPhotos().contains(p)) continue;
					for (Tag t : p.getTags()) {
						if ((t.getType().contains(type))&&(t.getValue().contains(value))) {
							results.addPhoto(p);
							break;
						}
					}
				}
			}
		}
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/application/search.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			SearchController sv = loader.getController();
			sv.setAlbum(results);
			sv.setUser(user);
			sv.start(primaryStage);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setTitle("Photo Album -- " + user.getName() + " -- " + results.getName());
		} catch(Exception e) {
			IO.popup("Error", "Error opening search results", "");
		}
	}

	private void deleteAlbum () {
		int albumnum = table.getSelectionModel().getSelectedIndex();
		user.deleteAlbum(albumnum);
		if (user.getAlbums().size()!=0) table.getSelectionModel().select((albumnum!=0) ? (albumnum - 1) : albumnum);
		IO.saveUser(user);
	}

	private void createAlbum() {
		String newalbumname = newalbumbox.getText();
		newalbumbox.setText("");
		for(Album a : user.getAlbums()) {
			if (a.getName().compareTo(newalbumname)==0)  {
				IO.popup("Error", "Invalid Album Name", "There is already an album with this name. Try again.");
				return;
			}
		}
		Album al = new Album(newalbumname);
		user.addAlbum(al);
		IO.saveUser(user);
	}
}
