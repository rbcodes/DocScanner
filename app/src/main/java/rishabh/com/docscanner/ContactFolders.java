package rishabh.com.docscanner;

import java.io.Serializable;

public class ContactFolders implements Serializable {

	private String id;
	private String name;
	private String itemcount;
	
	public ContactFolders(String id, String name, String itemcount) {
		super();
		this.id = id;
		this.name = name;
		this.itemcount = itemcount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItemcount() {
		return itemcount;
	}

	public void setItemcount(String itemcount) {
		this.itemcount = itemcount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
