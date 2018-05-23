package rishabh.com.docscanner.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Sharedpref file name
    private static final String PREF_NAME = "Docscanner";

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setcategories(ArrayList<String> arrayList){
        Set<String> set = new HashSet<String>();
        set.addAll(arrayList);
        editor.putStringSet("categories", set);
        editor.commit();
    }

    public void setcategoriesid(ArrayList<String> arrayList){
        Set<String> set = new HashSet<String>();
        set.addAll(arrayList);
        editor.putStringSet("categoriesid", set);
        editor.commit();
    }

    public List<String> getcategories()
    {
        Set<String> set = pref.getStringSet("categories", null);
        List<String> sample = new ArrayList<String>(set);
        return sample;
    }

    public List<String> getcategoriesid()
    {
        Set<String> set = pref.getStringSet("categoriesid", null);
        List<String> sample = new ArrayList<String>(set);
        return sample;
    }
  }