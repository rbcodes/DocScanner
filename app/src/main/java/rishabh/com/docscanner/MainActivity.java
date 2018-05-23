package rishabh.com.docscanner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.android.OpenCVLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rishabh.com.docscanner.loginandsession.LoginActivity;
import rishabh.com.docscanner.utils.Categories;
import rishabh.com.docscanner.utils.SQLiteHandler;
import rishabh.com.docscanner.utils.SessionManager;
import rishabh.com.docscanner.utils.User;

public class MainActivity extends AppCompatActivity implements FoldersAdapter.FoldersClickListener{

    Button camera;
    Button gallery;
    HashMap<String, Integer> categories;
    GridView gridview;
    private SQLiteHandler db;
    ArrayList<HashMap<String ,String>> categorydetails = new ArrayList<HashMap<String ,String>>();
    ArrayList<HashMap<String ,String>> categotycount = new ArrayList<HashMap<String ,String>>();
    List<Categories> result = new ArrayList<>();
    ArrayList<String> directorieslist = new ArrayList<String>();
    ArrayList<String> categoryidlist = new ArrayList<String>();
    private FoldersAdapter adpt;
    private ArrayList<Categories> categorieslist = new ArrayList<Categories>();
    String userID;
    static {
        if (!OpenCVLoader.initDebug())
            Log.e("", "Failed to load OpenCV!");
    }
    String TAG = "Categories";
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private SessionManager session;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = (Button) findViewById(R.id.camera);
        gallery = (Button) findViewById(R.id.gallery);
        gridview = (GridView) findViewById(R.id.gridview);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/bernerbasisschrift1.ttf");
        camera.setTypeface(myTypeface);
        gallery.setTypeface(myTypeface);
        db = new SQLiteHandler(getApplicationContext());
        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDatabase = mFirebaseInstance.getReference("users").child(userID).child("categories");


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent obj_intent = new Intent(MainActivity.this, CameraScanActivity.class);
                startActivity(obj_intent);

//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, REQUEST_CAMERA);
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
            }
        });

        adpt  = new FoldersAdapter(this,new ArrayList<Categories>(), MainActivity.this);
        gridview.setAdapter(adpt);
        categorydetails = db.getCategoryDetails();

        session = new SessionManager(this);
        session.setcategoriesid(categoryidlist);

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Categories categories = postSnapshot.getValue(Categories.class);
                    result.add(categories);
                    String name = categories.getName();
                }

                if (result == null) {
                    Log.e("ERROR", "User data is null!");
                    return;
                }
                adpt.setItemList(result);
                adpt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }

        });



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Uri selectedImageUri = data.getData();
        try {
            DocScanner.getInstance().bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent obj_intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(obj_intent);
    }

    Bitmap bitmap = null;

    private void onSelectFromGalleryResult(Intent data) {

        Uri selectedImageUri = data.getData();
        try {
            DocScanner.getInstance().bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            String str = "";
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent obj_intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivity(obj_intent);

    }

    @Override
    public void itemClicked(String name, String id) {
        if (name.equalsIgnoreCase("Add Categories"))
        {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Intent i = new Intent(this, OpenCategoryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("category", name);
            bundle.putString("category_id", id);
            i.putExtras(bundle);
            startActivity(i);
        }
    }

    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child("categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Categories categories = postSnapshot.getValue(Categories.class);
                    result.add(categories);

                    String name = categories.getName();
                    // here you can access to name property like university.name

                }

//                Categories categories = dataSnapshot.getValue(Categories.class);
                // Check for null
                if (categories == null) {
                    Log.e("ERROR", "User data is null!");
                    return;
                }

//                result.add(categories);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }
}
