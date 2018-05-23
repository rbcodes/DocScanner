package rishabh.com.docscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class ImageProcess extends Activity {

    ImageView imageView;
    Button orignal;
    Button graymode;
    Button bandw;
    Button magiccolor;
    ImageView rotate_left;
    ImageView rotate_right;
    ImageView done;
    Bitmap abcd;
    Bitmap bp;
    Mat newimage = new Mat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_image_process);
        imageView = (ImageView)findViewById(R.id.sourceImageView);
        Bitmap abc = DocScanner.getInstance().bitmap;
        abcd = DocScanner.getInstance().bitmapconverted;
        orignal = (Button)findViewById(R.id.orignal);
        graymode = (Button)findViewById(R.id.graymode);
        bandw = (Button)findViewById(R.id.bandw);
        magiccolor = (Button)findViewById(R.id.magiccolor);
        done = (ImageView)findViewById(R.id.done);
        rotate_left = (ImageView)findViewById(R.id.rotate_left);
        rotate_right = (ImageView)findViewById(R.id.rotate_right);

        imageView.setImageBitmap(abcd);

       rotate_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap rotatedbitmap =  RotateBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap(),-90);
                    imageView.setImageBitmap(rotatedbitmap);
                }
            });

        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap rotatedbitmap =  RotateBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap(),90);
                imageView.setImageBitmap(rotatedbitmap);
            }
        });

        graymode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap1 = abcd;
                Bitmap bitmap  = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
                org.opencv.android.Utils.bitmapToMat(bitmap,newimage);

                Mat imgSource = new Mat(newimage.size(), CvType.CV_8U);
                Imgproc.cvtColor(newimage, imgSource, Imgproc.COLOR_BGR2GRAY);

                bitmap = createBitmapfromMat(imgSource);
                imageView.setImageBitmap(bitmap);

            }
        });

        bandw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap1 = abcd;
                Bitmap bitmap  = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
                org.opencv.android.Utils.bitmapToMat(bitmap,newimage);

                Mat imgSource = new Mat(newimage.size(), CvType.CV_8U);
                Imgproc.cvtColor(newimage, imgSource, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.adaptiveThreshold(imgSource, imgSource, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 40);
                Imgproc.adaptiveThreshold(imgSource, imgSource, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 15, 40);
                bitmap = createBitmapfromMat(imgSource);
                imageView.setImageBitmap(bitmap);

            }
        });

        magiccolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(doHighlightImage(abcd));

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    DocScanner.getInstance().bitmapprocessed =  ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                Intent obj_intent = new Intent(ImageProcess.this, NameActivty.class);
                startActivity(obj_intent);
            }
        });

    }


    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap createBitmapfromMat(final Mat snap) {
        bp = null;
        bp = Bitmap.createBitmap(snap.cols(), snap.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(snap, bp);

        return bp;
    }

    public static Bitmap doHighlightImage(Bitmap src) {
        // create new bitmap, which will be painted and becomes result image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        // setup canvas for painting
        Canvas canvas = new Canvas(bmOut);
        // setup default color
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // create a blur paint for capturing alpha
        Paint ptBlur = new Paint();
        ptBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        // capture alpha into a bitmap
        Bitmap bmAlpha = src.extractAlpha(ptBlur, offsetXY);
        // create a color paint
        Paint ptAlphaColor = new Paint();
        ptAlphaColor.setColor(0xFFFFFFFF);
        // paint color for captured alpha region (bitmap)
        canvas.drawBitmap(bmAlpha, offsetXY[0], offsetXY[1], ptAlphaColor);
        // free memory
        bmAlpha.recycle();

        // paint the image source
        canvas.drawBitmap(src, 0, 0, null);

        // return out final image
        return bmOut;
    }

}
