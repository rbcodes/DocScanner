package rishabh.com.docscanner.utils;

import java.util.ArrayList;

/**
 * Created by stpl on 11/11/2016.
 */
public class User {

    public String name;
    public String email;
    public int numberofposts;
    public ArrayList<Categories> categories;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, int numberofposts, ArrayList<Categories> categories) {
        this.name = name;
        this.email = email;
        this.numberofposts = numberofposts;
        this.categories = categories;
    }
}
