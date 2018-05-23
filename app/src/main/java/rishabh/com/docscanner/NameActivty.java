package rishabh.com.docscanner;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import rishabh.com.docscanner.utils.SessionManager;

public class NameActivty extends AppCompatActivity {

    ImageView cropImageView;
    ImageButton buttonDone;
    EditText name_ed;
    Spinner categoryspinner;
    private SessionManager session;
    private List<String> categorylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_activty);

        cropImageView = (ImageView)findViewById(R.id.cropImageView);

        Bitmap abc = DocScanner.getInstance().bitmapprocessed;
        cropImageView.setImageBitmap(abc);
        buttonDone = (ImageButton)findViewById(R.id.buttonDone);
        name_ed = (EditText)findViewById(R.id.name_ed);
        categoryspinner = (Spinner)findViewById(R.id.categoriesspinner);
        session = new SessionManager(this);
        categorylist = session.getcategories();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, categorylist);

        categoryspinner.setAdapter(adapter);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(categoryspinner.getSelectedItem().equals(""))
                {
                    Toast.makeText(NameActivty.this,"Please select a category..",Toast.LENGTH_LONG).show();
                }
                if(name_ed.getText().toString().equals("") || name_ed.getText().toString().equals(null))
                {
                    Toast.makeText(NameActivty.this,"Please enter a name..",Toast.LENGTH_LONG).show();
                }
                else
                {
                    createDirectoryAndSaveFile( ((BitmapDrawable)cropImageView.getDrawable()).getBitmap() , name_ed.getText().toString(), categoryspinner.getSelectedItem()+"");
                }

            }
        });
    }


    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName, String category) {

        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File path = cw.getDir("imageDir", Context.MODE_PRIVATE);
/*
            String path = Environment.getExternalStorageDirectory().toString();*/
            new File(path + "/DocScanner").mkdirs();


            File filename = new File(path + "/DocScanner/"+ category + "/" + fileName + ".jpg");
            FileOutputStream out = new FileOutputStream(filename);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            //MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());
            Toast.makeText(getApplicationContext(), "File is Saved in  " + filename, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
