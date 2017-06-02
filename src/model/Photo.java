package model;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.embed.swing.SwingFXUtils;

public class Photo implements Serializable{
	private static final long serialVersionUID = 926463823;
	private int number;
	private List<Tag> tags;
	private String caption;
	private Calendar date;
	private transient Image image;

	public Photo(BufferedImage bi, Calendar date, int number) {
		this.number = number;
		tags = new ArrayList<Tag>();
		this.image = SwingFXUtils.toFXImage(bi, null);
		this.date = date;
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = SwingFXUtils.toFXImage(ImageIO.read(in), null);
	}

	public int getNumber() { return this.number; }

	public void setNumber(int number) { this.number = number; }

	public Image getImage() { return this.image; }

	public void setImage(Image image) { this.image = image; }

	public Calendar getDate() { return date; }

	public void setDate(Calendar date) { this.date = date; }

	public String getCaption() { return caption; }

	public void setCaption(String caption) { this.caption = caption; }

	public List<Tag> getTags() { return tags; }

	public void addTag(String type, String value) { tags.add(new Tag(type, value)); }

	public ImageView getImageView() {
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(30);
		imageView.setFitWidth(30);
		return imageView;
	}
}
