package rishabh.com.docscanner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rishabh.com.docscanner.utils.Zoomcameraview;

public class CameraScanActivity extends Activity {

    private final static String TAG = "BetSlipReader";
    FrameLayout camera;
    ImageView capture;

    static {
        if (!OpenCVLoader.initDebug())
            Log.e(TAG, "Failed to load OpenCV!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.show_camera);
        camera = (FrameLayout)findViewById(R.id.camera);
        capture = (ImageView)findViewById(R.id.capture);
        final PreviewNew preview = new PreviewNew(CameraScanActivity.this);
        camera.setVisibility(View.VISIBLE);
        camera.addView(preview);

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preview.isImageCaptured = true;
            }
        });
    }

}

