package rishabh.com.docscanner;

import java.io.Serializable;

public class ContactColors implements Serializable {

	private String id;
	private int color;

	public ContactColors(String id, int color) {
		super();
		this.id = id;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
