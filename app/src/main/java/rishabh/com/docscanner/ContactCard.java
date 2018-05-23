package rishabh.com.docscanner;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

public class ContactCard implements Serializable {

		private String name;
		private String category_id;
		private String category_name;
		private String page_id;
		private String background_color;
		private String angle;
		private URI thumbnail;

		public ContactCard() {
		}

		public ContactCard(String name, String category_id, String category_name, String page_id, String angle, String background_color, URI thumbnail) {
			this.name = name;
			this.category_name = category_name;
			this.thumbnail = thumbnail;
			this.category_id = category_id;
			this.page_id = page_id;
			this.angle = angle;
			this.background_color = background_color;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCategory() {
			return category_name;
		}

		public void setCategory (String numOfSongs) {
			this.category_name = numOfSongs;
		}

		public URI getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(URI thumbnail) {
			this.thumbnail = thumbnail;
		}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public String getPage_id() {
		return page_id;
	}

	public void setPage_id(String page_id) {
		this.page_id = page_id;
	}

	public String getBackground_color() {
		return background_color;
	}

	public void setBackground_color(String background_color) {
		this.background_color = background_color;
	}

	public String getAngle() {
		return angle;
	}

	public void setAngle(String angle) {
		this.angle = angle;
	}
}