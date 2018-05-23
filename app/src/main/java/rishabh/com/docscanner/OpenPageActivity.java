package rishabh.com.docscanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class OpenPageActivity extends AppCompatActivity {

    LinearLayout completeframe;
    LinearLayout papercolorllyout;
    HorizontalScrollView imageprocessingllyout;
    FrameLayout mainframe;
    String angle;
    String background_color;
    LinearLayout layout_root;
    ImageView sourceImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_page);
        completeframe = (LinearLayout) findViewById(R.id.completeframe);
        mainframe = (FrameLayout) findViewById(R.id.mainframe);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        sourceImageView = (ImageView) findViewById(R.id.sourceImageView);
        Bundle bundle = getIntent().getExtras();
        String stuff = bundle.getString("URI");
        angle = bundle.getString("angle");
        background_color = bundle.getString("background_color");
        papercolorllyout = (LinearLayout) findViewById(R.id.papercolorllyout);
        imageprocessingllyout = (HorizontalScrollView) findViewById(R.id.imageprocessingllyout);
        papercolorllyout.setVisibility(View.INVISIBLE);
        imageprocessingllyout.setVisibility(View.INVISIBLE);
        papercolorllyout.setVisibility(View.VISIBLE);
        imageprocessingllyout.setVisibility(View.VISIBLE);


        Glide.with(this)
                .load(new File(stuff))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(sourceImageView);

        mainframe.setRotation(Float.parseFloat(angle));
//        layout_root.setBackgroundColor(Color.parseColor(background_color));
    }
}
