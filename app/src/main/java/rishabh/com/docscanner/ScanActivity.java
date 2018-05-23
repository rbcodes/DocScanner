package rishabh.com.docscanner;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rishabh.com.docscanner.utils.RotationGestureDetector;
import rishabh.com.docscanner.utils.SQLiteHandler;
import rishabh.com.docscanner.utils.SessionManager;

public class ScanActivity extends AppCompatActivity implements RotationGestureDetector.OnRotationGestureListener, ColorsAdapter.ColorClickListener {

    private ImageView scanButton, back;
    private ImageView sourceImageView;
    private FrameLayout sourceFrame;
    public PolygonView polygonView;
    Bitmap bp;
    String currentangle = "0";
    String currentcolor = "FFFFFF";
    Boolean imagecropped = false;
    private Bitmap bitmap;
    private FrameLayout mainframe;
    LinearLayout layout_root;
    private ProgressDialogFragment progressDialogFragment;
    private Bitmap original;
    Mat newimage = new Mat();
    ArrayList<String> colorcodes = new ArrayList<String>();
    private RotationGestureDetector mRotationDetector;
    ImageView orignal_imageview, magic_imageview, gray_imageview, bandw_imageview;
    LinearLayout orignal_selection, magic_selection, gray_selection, bandw_selection;
    TextView orignal_textview, magic_textview, gray_textview, bandw_textview;
    private SessionManager session;
    LinearLayout papercolorllyout;
    HorizontalScrollView imageprocessingllyout;
    LinearLayout completeframe;
    private List<ContactColors> colorsresult = new ArrayList<>();
    private RecyclerView recyclerView;
    private ColorsAdapter mAdapter;
    private List<String> categorylist;
    private List<String> categoryidlist;
    private SQLiteHandler db;
    private Effect mEffect;
    private EffectContext mEffectContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        sourceImageView = (ImageView) findViewById(R.id.sourceImageView);
        scanButton = (ImageView) findViewById(R.id.right_action);
        back = (ImageView) findViewById(R.id.left_action);
        layout_root = (LinearLayout) findViewById(R.id.layout_root);
        scanButton.setOnClickListener(new ScanButtonClickListener());
        sourceFrame = (FrameLayout) findViewById(R.id.sourceFrame);
        polygonView = (PolygonView) findViewById(R.id.polygonView);
        bitmap = DocScanner.getInstance().bitmap;
        mainframe = (FrameLayout) findViewById(R.id.mainframe);
        mRotationDetector = new RotationGestureDetector(this, sourceImageView);
        papercolorllyout = (LinearLayout) findViewById(R.id.papercolorllyout);
        imageprocessingllyout = (HorizontalScrollView) findViewById(R.id.imageprocessingllyout);
        papercolorllyout.setVisibility(View.GONE);
        imageprocessingllyout.setVisibility(View.GONE);
        completeframe = (LinearLayout) findViewById(R.id.completeframe);
        orignal_imageview = (ImageView) findViewById(R.id.orignal_imageview);
        magic_imageview = (ImageView) findViewById(R.id.magic_imageview);
        bandw_imageview = (ImageView) findViewById(R.id.bandw_imageview);
        gray_imageview = (ImageView) findViewById(R.id.gray_imageview);
        db = new SQLiteHandler(getApplicationContext());


        orignal_selection = (LinearLayout) findViewById(R.id.orignal_selection);
        magic_selection = (LinearLayout) findViewById(R.id.magic_selection);
        bandw_selection = (LinearLayout) findViewById(R.id.bandw_selection);
        gray_selection = (LinearLayout) findViewById(R.id.gray_selection);

        orignal_textview = (TextView) findViewById(R.id.orignal_textview);
        magic_textview = (TextView) findViewById(R.id.magic_textview);
        bandw_textview = (TextView) findViewById(R.id.bandw_textview);
        gray_textview = (TextView) findViewById(R.id.gray_textview);

        sourceImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onTouchEvent(event);
                return true;
            }
        });
        sourceFrame.post(new Runnable() {
            @Override
            public void run() {
                original = bitmap;
                if (original != null) {
                    setBitmap(original);
                }
            }
        });

        gray_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(v);
                Bitmap bitmap = original.copy(Bitmap.Config.ARGB_8888, true);
                org.opencv.android.Utils.bitmapToMat(bitmap, newimage);

                Mat imgSource = new Mat(newimage.size(), CvType.CV_8U);
                Imgproc.cvtColor(newimage, imgSource, Imgproc.COLOR_BGR2GRAY);

                bitmap = createBitmapfromMat(imgSource);
                sourceImageView.setImageBitmap(bitmap);
            }
        });

        orignal_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(v);
                sourceImageView.setImageBitmap(original);
            }
        });

        bandw_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(v);
              /*  Bitmap bitmap = original.copy(Bitmap.Config.ARGB_8888, true);
                org.opencv.android.Utils.bitmapToMat(bitmap, newimage);
                Mat imgSource = new Mat(newimage.size(), CvType.CV_8U);
                Imgproc.cvtColor(newimage, imgSource, Imgproc.COLOR_BGR2GRAY);

                Imgproc.adaptiveThreshold(imgSource, imgSource, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 2, 15);
                bitmap = createBitmapfromMat(imgSource);*/
//                sourceImageView.setImageBitmap(changeBitmapContrastBrightness(bitmap, 2, -20));
                sourceImageView.setImageBitmap(changeBitmapContrastBrightness(ConvertToBlackAndWhite(original),2, -200));
            }
        });

        magic_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(v);
                sourceImageView.setImageBitmap(changeBitmapContrastBrightness(original, 2, -120));
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ColorsAdapter(this, colorsresult);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareColorData();


    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mRotationDetector.onTouchEvent(sourceImageView.getImageMatrix(), event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        Matrix m = rotationDetector.getZoom();
        sourceImageView.setImageMatrix(m);
        mainframe.setRotation(angle);
        currentangle = angle + "";
        Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
    }


    public void changeColor(View view) {
        if (view.getId() == R.id.orignal_selection) {
            orignal_textview.setTextColor(getResources().getColor(R.color.design_text_selected));
            orignal_imageview.setImageResource(R.color.design_text_selected);
            bandw_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            bandw_imageview.setImageResource(R.color.design_text_normal);
            gray_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            gray_imageview.setImageResource(R.color.design_text_normal);
            magic_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            magic_imageview.setImageResource(R.color.design_text_normal);
        }
        if (view.getId() == R.id.magic_selection) {
            orignal_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            orignal_imageview.setImageResource((R.color.design_text_normal));
            bandw_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            bandw_imageview.setImageResource((R.color.design_text_normal));
            gray_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            gray_imageview.setImageResource((R.color.design_text_normal));
            magic_textview.setTextColor(getResources().getColor(R.color.design_text_selected));
            magic_imageview.setImageResource((R.color.design_text_selected));
        }
        if (view.getId() == R.id.gray_selection) {
            orignal_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            orignal_imageview.setImageResource((R.color.design_text_normal));
            bandw_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            bandw_imageview.setImageResource((R.color.design_text_normal));
            gray_textview.setTextColor(getResources().getColor(R.color.design_text_selected));
            gray_imageview.setImageResource((R.color.design_text_selected));
            magic_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            magic_imageview.setImageResource((R.color.design_text_normal));
        }
        if (view.getId() == R.id.bandw_selection) {
            orignal_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            orignal_imageview.setImageResource((R.color.design_text_normal));
            bandw_textview.setTextColor(getResources().getColor(R.color.design_text_selected));
            bandw_imageview.setImageResource((R.color.design_text_selected));
            gray_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            gray_imageview.setImageResource((R.color.design_text_normal));
            magic_textview.setTextColor(getResources().getColor(R.color.design_text_normal));
            magic_imageview.setImageResource((R.color.design_text_normal));
        }
    }


    private void setBitmap(Bitmap original) {
        Bitmap scaledBitmap = scaledBitmap(original, sourceFrame.getWidth(), sourceFrame.getHeight());
        this.original = scaledBitmap;
        sourceImageView.setImageBitmap(scaledBitmap);
        Bitmap tempBitmap = ((BitmapDrawable) sourceImageView.getDrawable()).getBitmap();
        Map<Integer, PointF> pointFs = getEdgePoints(tempBitmap);
        polygonView.setPoints(pointFs);
        polygonView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.scanPadding);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(tempBitmap.getWidth() + 2 * padding, tempBitmap.getHeight() + 2 * padding);
        layoutParams.gravity = Gravity.CENTER;
        polygonView.setLayoutParams(layoutParams);
    }

    private Map<Integer, PointF> getEdgePoints(Bitmap tempBitmap) {
        List<PointF> pointFs = getContourEdgePoints(tempBitmap);
        Map<Integer, PointF> orderedPoints = orderedValidEdgePoints(tempBitmap, pointFs);
        return orderedPoints;
    }

    private List<PointF> getContourEdgePoints(Bitmap tempBitmap) {
        ImageCrop ic = new ImageCrop();
        float[] points = ic.getPoints(tempBitmap);
        float x1 = points[0];
        float x2 = points[1];
        float x3 = points[2];
        float x4 = points[3];

        float y1 = points[4];
        float y2 = points[5];
        float y3 = points[6];
        float y4 = points[7];

        List<PointF> pointFs = new ArrayList<>();
        pointFs.add(new PointF(x1, y1));
        pointFs.add(new PointF(x2, y2));
        pointFs.add(new PointF(x3, y3));
        pointFs.add(new PointF(x4, y4));
        return pointFs;
    }

    private Map<Integer, PointF> getOutlinePoints(Bitmap tempBitmap) {
        Map<Integer, PointF> outlinePoints = new HashMap<>();
        outlinePoints.put(0, new PointF(0, 0));
        outlinePoints.put(1, new PointF(tempBitmap.getWidth(), 0));
        outlinePoints.put(2, new PointF(0, tempBitmap.getHeight()));
        outlinePoints.put(3, new PointF(tempBitmap.getWidth(), tempBitmap.getHeight()));
        return outlinePoints;
    }

    private Map<Integer, PointF> orderedValidEdgePoints(Bitmap tempBitmap, List<PointF> pointFs) {
        Map<Integer, PointF> orderedPoints = polygonView.getOrderedPoints(pointFs);
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap);
        }
        return orderedPoints;
    }

    @Override
    public void itemClicked(int position) {
        currentcolor = colorcodes.get(position) + "";
        layout_root.setBackgroundColor(Color.parseColor(currentcolor));
    }


    private class ScanButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Map<Integer, PointF> points = polygonView.getPoints();
            if (isScanPointsValid(points)) {
                new ScanAsyncTask(points).execute();
            } else {
                showErrorDialog();
            }
        }
    }

    private void showErrorDialog() {
        Toast.makeText(this, "cannot crop", Toast.LENGTH_LONG).show();
/*        SingleButtonDialogFragment fragment = new SingleButtonDialogFragment(R.string.ok, getString(R.string.cantCrop), "Error", true);
        FragmentManager fm = getActivity().getFragmentManager();
        fragment.show(fm, SingleButtonDialogFragment.class.toString());*/
    }

    private boolean isScanPointsValid(Map<Integer, PointF> points) {
        return points.size() == 4;
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
    }

    private Bitmap getScannedBitmap(Bitmap original, Map<Integer, PointF> points) {
        double x1 = (points.get(0).x);
        double x2 = (points.get(1).x);
        double x3 = (points.get(2).x);
        double x4 = (points.get(3).x);
        double y1 = (points.get(0).y);
        double y2 = (points.get(1).y);
        double y3 = (points.get(2).y);
        double y4 = (points.get(3).y);

        List<Double> doublesx = new ArrayList<>();
        doublesx.add(x1);
        doublesx.add(x2);
        doublesx.add(x3);
        doublesx.add(x4);

        List<Double> doublesy = new ArrayList<>();
        doublesy.add(y1);
        doublesy.add(y2);
        doublesy.add(y3);
        doublesy.add(y4);


        Collections.sort(doublesx);
        Collections.sort(doublesy);


        Log.d("", "POints(" + x1 + "," + y1 + ")(" + x2 + "," + y2 + ")(" + x3 + "," + y3 + ")(" + x4 + "," + y4 + ")");
        ImageCrop ic = new ImageCrop();
        Bitmap _bitmap = ic.getScannedBitmap(original, doublesx.get(0), doublesy.get(0), doublesx.get(3), doublesy.get(3), x2, y2, x4, y4);
        return _bitmap;
    }


    public void CreateDialog() {
        View dialog = LayoutInflater.from(this).inflate(R.layout.dialog_screen, null);
        Dialog dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(dialog);
        dialog1.show();
    }


    private class ScanAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private Map<Integer, PointF> points;

        public ScanAsyncTask(Map<Integer, PointF> points) {
            this.points = points;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("Scanning");
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return getScannedBitmap(original, points);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            dismissDialog();
            DocScanner.getInstance().bitmapconverted = bitmap;

            if (!imagecropped) {
//
// .setVisibility(View.VISIBLE);
                imageprocessingllyout.setVisibility(View.VISIBLE);
                polygonView.setVisibility(View.GONE);
                sourceImageView.setImageBitmap(bitmap);
                imagecropped = true;
            } else {
                final Dialog dialog = new Dialog(ScanActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_screen);
                final Spinner category = (Spinner) dialog.findViewById(R.id.spinner);
                final EditText filename = (EditText) dialog.findViewById(R.id.filename);
                ImageView left_action = (ImageView) dialog.findViewById(R.id.left_action);
                ImageView right_action = (ImageView) dialog.findViewById(R.id.right_action);
                dialog.show();
                session = new SessionManager(ScanActivity.this);
                categorylist = session.getcategories();
                categoryidlist = session.getcategoriesid();

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ScanActivity.this, android.R.layout.simple_spinner_item, categorylist);
                category.setAdapter(adapter);

                right_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (category.getSelectedItem().equals("")) {
                            Toast.makeText(ScanActivity.this, "Please select a category..", Toast.LENGTH_LONG).show();
                        }
                        if (filename.getText().toString().equals("") || filename.getText().toString().equals(null)) {
                            Toast.makeText(ScanActivity.this, "Please enter a name..", Toast.LENGTH_LONG).show();

                        } else {
                         /*   mainframe.setDrawingCacheEnabled(true);
                            mainframe.buildDrawingCache();
                            Bitmap map = mainframe.getDrawingCache();*/
                            int selected_id = categorylist.indexOf(category.getSelectedItem());
                            createDirectoryAndSaveFile(((BitmapDrawable) sourceImageView.getDrawable()).getBitmap(), filename.getText().toString(), category.getSelectedItem() + "", categoryidlist.get(selected_id));
                            dialog.dismiss();
                        }

                    }
                });

                left_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        }
    }

    protected void showProgressDialog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        FragmentManager fm = getFragmentManager();
        progressDialogFragment.show(fm, ProgressDialogFragment.class.toString());
    }

    protected void dismissDialog() {
        progressDialogFragment.dismissAllowingStateLoss();
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
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


    private void prepareColorData() {

        colorcodes.add("#FFFFFF");
        colorcodes.add("#DFE2DB");
        colorcodes.add("#FFF056");
        colorcodes.add("#FFF056");
        colorcodes.add("#C63D0F");
        colorcodes.add("#FDF3E7");
        colorcodes.add("#D9853B");
        colorcodes.add("#ECECEA");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");
        colorcodes.add("#191919");

        for (int x = 0; x < colorcodes.size(); x++) {
            colorsresult.add(new ContactColors(x + "", Color.parseColor(colorcodes.get(x))));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName, String category, String c_id) {

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File path = cw.getDir("imageDir", Context.MODE_PRIVATE);

        if (db.addPage(c_id, fileName, path + "/DocScanner/" + category + "/" + fileName + ".jpg", currentcolor, currentangle, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))) {
            try {
              /*
            String path = Environment.getExternalStorageDirectory().toString();*/
                new File(path + "/DocScanner").mkdirs();


                File filename = new File(path + "/DocScanner/" + category + "/" + fileName + ".jpg");
                FileOutputStream out = new FileOutputStream(filename);
                imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                //MediaStore.Images.Media.insertImage(getContentResolver(), filename.getAbsolutePath(), filename.getName(), filename.getName());
                Toast.makeText(getApplicationContext(), "File is Saved in  " + filename, Toast.LENGTH_LONG).show();

                Intent obj_intent = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(obj_intent);
                finish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Page with " + fileName + "already exist, please try a different name ", Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    public Bitmap ConvertToBlackAndWhite(Bitmap sampleBitmap){
        ColorMatrix bwMatrix =new ColorMatrix();
        bwMatrix.setSaturation(0);
        final ColorMatrixColorFilter colorFilter= new ColorMatrixColorFilter(bwMatrix);
        Bitmap rBitmap = sampleBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint=new Paint();
        paint.setColorFilter(colorFilter);
        Canvas myCanvas =new Canvas(rBitmap);
        myCanvas.drawBitmap(rBitmap, 0, 0, paint);
        return rBitmap;
    }

}
