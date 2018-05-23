package rishabh.com.docscanner.utils;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.Image;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "docscanner";

    // Login table name
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_PAGES = "page";


    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_NAME = "name";
    private static final String KEY_PAGE_URI = "uri";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ISLOCKED = "true";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_BACKGROUND_COLOR = "background_color";
    private static final String KEY_ANGLE = "angle";
    private static final String KEY_CREATED_AT = "created_at";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("HHHHHHHHHHHHHHHHHH", "in on create");

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NAME + " INTEGER ,"
                + KEY_IMAGE + " TEXT,"
                + KEY_ISLOCKED +" TEXT,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_PAGE_TABLE = "CREATE TABLE " + TABLE_PAGES + "("
                + KEY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CATEGORY_ID +" INTEGER,"
                + KEY_NAME + " TEXT ,"
                + KEY_PAGE_URI + " TEXT ,"
                + KEY_BACKGROUND_COLOR + " TEXT,"
                + KEY_ANGLE +" TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_PAGE_TABLE);


        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
       // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public boolean addCategroy(String name,String image, String islocked, String password, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                if(cursor.getString(cursor.getColumnIndex(KEY_NAME)).equals(name))
                {
                    return false;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_IMAGE, image); // category_image
        values.put(KEY_ISLOCKED, islocked); // Email
        values.put(KEY_PASSWORD, password); // password
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New category inserted into sqlite: " + id);
        return true;
    }

    public boolean addPage(String c_id,String name, String uri, String background_color, String angle, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_PAGES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                if(cursor.getString(cursor.getColumnIndex(KEY_NAME)).equals(name))
                {
                    return false;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, c_id);
        values.put(KEY_NAME, name);
        values.put(KEY_PAGE_URI, uri);
        values.put(KEY_BACKGROUND_COLOR, background_color);
        values.put(KEY_ANGLE, angle);
        values.put(KEY_CREATED_AT, created_at);

        // Inserting Row
        long id = db.insert(TABLE_PAGES, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New page inserted into sqlite: " + id);
        return true;
    }


    /**
     * Getting user data from database
     * */

    public ArrayList<HashMap<String, String>> getCategoryDetails() {
        ArrayList<HashMap<String, String>> classes = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();

                user.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
                user.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.put(KEY_IMAGE, cursor.getString(cursor.getColumnIndex(KEY_IMAGE)));
                user.put(KEY_ISLOCKED, cursor.getString(cursor.getColumnIndex(KEY_ISLOCKED)));
                user.put(KEY_PASSWORD, cursor.getString(cursor.getColumnIndex(KEY_PASSWORD)));
                user.put(KEY_CREATED_AT, cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));

                Log.d(TAG, "Fetching category detail from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public ArrayList<HashMap<String, String>> getPageDetails() {
        ArrayList<HashMap<String, String>> classes = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_PAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();

                user.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
                user.put(KEY_CATEGORY_ID, cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_ID)));
                user.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.put(KEY_PAGE_URI, cursor.getString(cursor.getColumnIndex(KEY_PAGE_URI)));
                user.put(KEY_BACKGROUND_COLOR, cursor.getString(cursor.getColumnIndex(KEY_BACKGROUND_COLOR)));
                user.put(KEY_ANGLE, cursor.getString(cursor.getColumnIndex(KEY_ANGLE)));
                user.put(KEY_CREATED_AT, cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));

                Log.d(TAG, "Fetching page detail from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

    public ArrayList<HashMap<String, String>> getcategorypages(String id) {
        ArrayList<HashMap<String, String>> classes = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_PAGES +" WHERE " + KEY_CATEGORY_ID + "= "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                HashMap<String, String> user = new HashMap<String, String>();

                user.put(KEY_ID, cursor.getString(cursor.getColumnIndex(KEY_ID)));
                user.put(KEY_CATEGORY_ID, cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_ID)));
                user.put(KEY_NAME, cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                user.put(KEY_PAGE_URI, cursor.getString(cursor.getColumnIndex(KEY_PAGE_URI)));
                user.put(KEY_BACKGROUND_COLOR, cursor.getString(cursor.getColumnIndex(KEY_BACKGROUND_COLOR)));
                user.put(KEY_ANGLE, cursor.getString(cursor.getColumnIndex(KEY_ANGLE)));
                user.put(KEY_CREATED_AT, cursor.getString(cursor.getColumnIndex(KEY_CREATED_AT)));

                Log.d(TAG, "Fetching page detail from Sqlite: " + user.toString() + " " + cursor.getCount());
                classes.add(user);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // return user
        return classes;
    }

}