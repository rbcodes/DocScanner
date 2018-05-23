package rishabh.com.docscanner.utils;

import java.util.ArrayList;

/**
 * Created by stpl on 11/11/2016.
 */
public class Categories {

    public String name;
    public Boolean islocked;
    public String password;
    public String created_at;
    public String updated_at;
    public int position;
    public int numberofposts;
    public ArrayList<String> postids;

    public Categories() {
    }

    public Categories(String name, Boolean islocked, String password, int position, String created_at, String updated_at, int numberofposts) {
        this.name = name;
        this.islocked = islocked;
        this.password = password;
        this.position = position;
        this.created_at = created_at;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.numberofposts = numberofposts;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIslocked() {
        return islocked;
    }

    public void setIslocked(Boolean islocked) {
        this.islocked = islocked;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getNumberofposts() {
        return numberofposts;
    }

    public void setNumberofposts(int numberofposts) {
        this.numberofposts = numberofposts;
    }

    public ArrayList<String> getPostids() {
        return postids;
    }

    public void setPostids(ArrayList<String> postids) {
        this.postids = postids;
    }

}
