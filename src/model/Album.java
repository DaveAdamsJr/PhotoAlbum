package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Album implements Serializable {
	private static final long serialVersionUID = 746272649;
	private transient ObservableList<Photo> photos;
	private List<Integer> photoNums;
	private String name;
	private Calendar first, last;

	public Album(String name) {
		photos = FXCollections.observableArrayList();
		photoNums = new ArrayList<Integer>();
		this.name = name;
	}

	public String getName() { return this.name; }

	public void setName(String name) { this.name = name; }

	public String getFirst() {
		if (first==null) return "-";
		return this.first.getTime().toString();
	}

	public String getLast() {
		if (last==null) return "-";
		return this.last.getTime().toString();
	}

	public Integer getNPhotos() { return photos.size(); }

	public List<Integer> getPhotoNums() { return photoNums; }

	public void addPhoto(Photo p) {
		if ((first==null)||(p.getDate().compareTo(first)<0)) first = p.getDate();
		if ((last==null)||(p.getDate().compareTo(last)>0)) last = p.getDate();
		photos.add(p);
		photoNums.add(p.getNumber());
	}

	public ObservableList<Photo> getPhotos() { return photos; }

	public void setPhotos(ObservableList<Photo> photos) { this.photos = photos; }

	public void deletePhoto(int index) {
		photos.remove(index);
	}
}
