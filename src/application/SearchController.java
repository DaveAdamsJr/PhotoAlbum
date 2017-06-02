package application;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.*;

public class SearchController {
	@FXML private ImageView imgView;
	@FXML private TableView<Photo> table;
	@FXML private TableColumn<Photo, ImageView> photocol;
	@FXML private TableColumn<Photo, String> captcol;
	@FXML private Button makealbumbutton, closealbumbutton, movephotobutton, copyphotobutton, addtagbutton, addphotobutton,
		deletephotobutton, fwdbutton, backbutton, captioneditbutton;
	@FXML private TextField datebox, tagbox, addtagbox1, addtagbox2;
	private Album album;
	private User user;

	public void start(Stage primaryStage) {
		initTable();
		backbutton.setOnAction((event) -> slideshowBack());
		fwdbutton.setOnAction((event) -> slideshowFwd());
		closealbumbutton.setOnAction((event) -> closeAlbum(primaryStage));
		makealbumbutton.setOnAction((event) -> makeAlbum());
		table.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> showPhoto());
		if (album.getNPhotos()>0) table.getSelectionModel().select(0);
	}

	public void setUser(User user) { this.user = user; }

	public void setAlbum (Album album) { this.album = album; }

	private void makeAlbum() {
		TextInputDialog input = new TextInputDialog();
		input.setTitle("PhotoAlbum -- Save As Album");
		input.setHeaderText("Enter new album name.");
		input.setContentText("New album:");
		input.showAndWait();
		String newalbumname = input.getEditor().getText();
		for(Album a : user.getAlbums()) {
			if (a.getName().compareTo(newalbumname)==0)  {
				IO.popup("Error", "Invalid Album Name", "There is already an album with this name. Try again.");
				return;
			}
		}
		album.setName(newalbumname);
		user.addAlbum(album);
		IO.saveUser(user);;
	}

	private void initTable() {
		table.setItems(album.getPhotos());
		table.setEditable(true);
		captcol.setEditable(true);
		photocol.setCellValueFactory((new PropertyValueFactory<Photo,ImageView>("imageView")));
		captcol.setCellValueFactory((new PropertyValueFactory<Photo,String>("caption")));
		captcol.setCellFactory(TextFieldTableCell.forTableColumn());
		captcol.setOnEditCommit(
			new EventHandler<CellEditEvent<Photo,String>>() {
				@Override
				public void handle(CellEditEvent<Photo,String> e) {
					album.getPhotos().get(e.getTablePosition().getRow()).setCaption(e.getNewValue());
				}
			}
		);
	}

	private void showPhoto() {
		if (table.getSelectionModel().getSelectedIndex()==-1) return;
		Photo p = table.getSelectionModel().getSelectedItem();
		if (p==null) return;
		imgView.setImage(p.getImage());
		datebox.setText(p.getDate().getTime().toString());
		String taglist = "";
		for(Tag t: p.getTags()) {
			taglist += t.getType() + ":" + t.getValue() + ", ";
		}
		tagbox.setText(taglist);
	}

	private void closeAlbum(Stage primaryStage) {
		if (album.getName().compareTo("Search Results")==0) for (int i = 0; i<album.getNPhotos(); i++) album.deletePhoto(i);
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
		}
	}

	private void slideshowBack() {
		int index = table.getSelectionModel().getSelectedIndex();
		if (index>0) table.getSelectionModel().select(index - 1);
	}

	private void slideshowFwd() {
		int index = table.getSelectionModel().getSelectedIndex();
		if ((index+1)<album.getNPhotos()) table.getSelectionModel().select(index + 1);
	}
}
