package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import application.IO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class User implements Serializable{
	private static final long serialVersionUID = 649264823;
	private int photoNum = 0;
	private String name;
	private transient ObservableList<Album> albums;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		List<Album> albumlist = new ArrayList<Album>(albums);
		out.writeObject(albumlist);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        @SuppressWarnings("unchecked")
		List<Album> albumlist = (List<Album>) in.readObject();
        albums = FXCollections.observableArrayList(albumlist);
	}

	public User(String name) {
		albums = FXCollections.observableArrayList();
		this.name = name;
		File datadir = new File("data");
		if (datadir.exists()&&!datadir.isDirectory()) IO.popup("Error", "IO Error", "File named 'data' in main directory.");
		if (!datadir.exists()) datadir.mkdir();
		File userdir = new File("data/" + this.name);
		if (userdir.exists()) IO.popup("Error", "IO Error", "User directory for this user already exists.");
		userdir.mkdir();
	}

	public int getPhotoNum() { return this.photoNum++; }

	public String getName() { return name; }

	public ObservableList<Album> getAlbums() { return albums; }

	public void setAlbums (ObservableList<Album> albums) { this.albums = albums; }

	public void addAlbum(Album al) { albums.add(al); }

	public void deleteAlbum(int albumnum) { albums.remove(albumnum); }

	public void savePhoto(Photo p) {
		File photoFile = new File("data/" + this.name + "/photo" + p.getNumber() + ".data");
		try {
			if (photoFile.exists()) photoFile.delete();
			photoFile.createNewFile();
			ObjectOutputStream photoOut = new ObjectOutputStream(new FileOutputStream(photoFile));
			photoOut.writeObject(p);
			photoOut.close();
		} catch (IOException e) { IO.popup("Error", "Error Saving Photo", "Photo #" + p.getNumber() + ", User: " + this.name); }
	}

	public void loadAlbum(Album a) {
		if (a.getNPhotos()!=0) a.setPhotos(FXCollections.observableArrayList());
		for (int photoNum : a.getPhotoNums()) a.addPhoto(loadPhoto(photoNum));
	}

	public Photo loadPhoto(int photoNum) {
		Photo p = null;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File ("data/" + this.name + "photo" + photoNum + ".data")));
			p = (Photo) in.readObject();
			in.close();
		} catch (Exception e) { IO.popup("Error", "Error Loading Photo", "Photo #" + p.getNumber() + ", User: " + this.name); }
		return p;
	}

}
