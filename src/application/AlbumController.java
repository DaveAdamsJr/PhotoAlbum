package application;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.*;
import javafx.stage.Stage;

public class AlbumController {
	@FXML private ImageView imgView;
	@FXML private TableView<Photo> table;
	@FXML private TableColumn<Photo, ImageView> photocol;
	@FXML private TableColumn<Photo, String> captcol;
	@FXML private Button closealbumbutton, movephotobutton, copyphotobutton, addtagbutton, addphotobutton, deletephotobutton, fwdbutton, backbutton, captioneditbutton;
	@FXML private TextField datebox, tagbox, addtagbox1, addtagbox2;
	private Album album;
	private User user;


	public void start(Stage primaryStage) {
		initTable();
		addphotobutton.setOnAction((event) -> addPhoto(primaryStage));
		backbutton.setOnAction((event) -> slideshowBack());
		fwdbutton.setOnAction((event) -> slideshowFwd());
		addtagbutton.setOnAction((event) -> addTag());
		movephotobutton.setOnAction((event) -> movePhoto(false));
		copyphotobutton.setOnAction((event) -> movePhoto(true));
		closealbumbutton.setOnAction((event) -> closeAlbum(primaryStage));
		table.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> showPhoto());
		deletephotobutton.setOnAction((event) -> deletePhoto());
		if (album.getNPhotos()>0) table.getSelectionModel().select(0);
	}

	public void setUser(User user) { this.user = user; }

	public void setAlbum (Album album) { this.album = album; }

	private void initTable() {
		table.setItems(album.getPhotos());
		table.setEditable(true);
		captcol.setEditable(true);
		photocol.setCellValueFactory((new PropertyValueFactory<Photo,ImageView>("imageView")));
		captcol.setCellValueFactory((new PropertyValueFactory<Photo,String>("caption")));
		captcol.setCellFactory(TextFieldTableCell.forTableColumn());
		captcol.setOnEditCommit(new EventHandler<CellEditEvent<Photo,String>>() {
				@Override
				public void handle(CellEditEvent<Photo,String> e) {
					Photo p = album.getPhotos().get(e.getTablePosition().getRow());
					p.setCaption(e.getNewValue());
					user.savePhoto(p);
				}
			});
	}

	private void movePhoto(boolean copy) {
		if (table.getSelectionModel().getSelectedIndex()==-1) {
			IO.popup("Error", "No Photo Selected", "Select a photo and try again.");
			return;
		}
		TextInputDialog input = new TextInputDialog();
		input.setTitle("PhotoAlbum -- " + ((copy) ? "Copy Photo" : "Move Photo"));
		input.setHeaderText("Enter destination album name.");
		input.setContentText("Destination album:");
		input.showAndWait();
		String destalbumname = input.getEditor().getText();
		Album destAlbum = null;
		for(Album a : user.getAlbums()) {
			if (a.getName().compareTo(destalbumname)==0) destAlbum = a;
		}
		if ((destAlbum==null)||(destAlbum.getName().compareTo(album.getName())==0)) {
			IO.popup("Error", "Invalid Album Name", "That was not a valid album name. Try again.");
			return;
		}
		destAlbum.addPhoto(table.getSelectionModel().getSelectedItem());
		if (!copy) album.getPhotos().remove(table.getSelectionModel().getSelectedIndex());
		IO.saveUser(user);
		IO.popup("PhotoAlbum", "Success", (copy) ? "Copy successful" : "Move successful");
	}

	private void addTag() {
		String type = addtagbox1.getText();
		String value = addtagbox2.getText();
		if ((type.length()<1)||(value.length()<1)) {
			IO.popup("Error", "Invalid Tag", "Try again.");
			return;
		}
		Photo p = table.getSelectionModel().getSelectedItem();
		p.addTag(type, value);
		user.savePhoto(p);
		showPhoto();
		addtagbox1.setText("");
		addtagbox2.setText("");
	}

	private void showPhoto() {
		if (table.getSelectionModel().getSelectedIndex()==-1) return;
		Photo p = table.getSelectionModel().getSelectedItem();
		if (p==null) return;
		imgView.setImage(p.getImage());
		datebox.setText(p.getDate().getTime().toString());
		String taglist = "";
		for(Tag t: p.getTags()) taglist += t.getType() + ":" + t.getValue() + ", ";
		tagbox.setText(taglist);
	}

	private void closeAlbum(Stage primaryStage) {
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

	private void deletePhoto() {
		int index = table.getSelectionModel().getSelectedIndex();
		if (index==-1) return;
		album.deletePhoto(index);
		IO.saveUser(user);
		showPhoto();
	}

	private void slideshowBack() {
		int index = table.getSelectionModel().getSelectedIndex();
		if (index>0) table.getSelectionModel().select(index - 1);
	}

	private void slideshowFwd() {
		int index = table.getSelectionModel().getSelectedIndex();
		if ((index+1)<album.getNPhotos()) table.getSelectionModel().select(index + 1);
	}

	private void addPhoto(Stage primaryStage) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("Image Files (.png, .jpg, .bmp)", "*.png", "*.jpg", "*.bmp"));
		File imgfile = fc.showOpenDialog(primaryStage);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(imgfile);
		} catch (IOException e) {
			IO.popup("Error", "Error loading image file", "");
			e.printStackTrace();
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(imgfile.lastModified());
		Photo p = new Photo(bi, c, user.getPhotoNum());
		p.setCaption("");
		album.addPhoto(p);
		table.getSelectionModel().select(album.getNPhotos()-1);
		user.savePhoto(p);
		IO.saveUser(user);

	}
}
