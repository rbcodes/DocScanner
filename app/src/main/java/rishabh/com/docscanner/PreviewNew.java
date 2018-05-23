package rishabh.com.docscanner;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;


class PreviewNew extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "Preview";

    Boolean isbetslipready = false;
    private Activity context;
    Mat newimage = new Mat();
    CameraScanActivity activity;
    SurfaceHolder mHolder;
    public Camera camera;
    ImageView capture;

    PreviewNew(Activity context) {
        super(context);

        this.context = context;

        if (context instanceof CameraScanActivity)
            this.activity = (CameraScanActivity) context;

        autoFocusHandler = new Handler();
        mHolder = getHolder();
        // Instance barcode scanner
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setFormat(PixelFormat.TRANSLUCENT | WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

    }

    public void setisisImageCaptured(boolean b) {
        isImageCaptured = b;
        isbetslipready = b;
        previewing = !b;
    }

    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }


    public static boolean isImageCaptured = false;
    Camera.Parameters parameters;
    byte[] data;

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (Build.MODEL.equalsIgnoreCase("SM-T231")) {
            camera = openFrontFacingCameraGingerbread();
        } else {
            camera = Camera.open();
        }

        try {
            camera.setPreviewDisplay(mHolder);
            setCameraDisplayOrientation(context,0,camera);
            camera.setPreviewCallback(new PreviewCallback() {

                public void onPreviewFrame(byte[] data, Camera arg1) {
                    FileOutputStream outStream = null;
                    try {
                        Camera.Parameters parameters = camera.getParameters();
                        PreviewNew.this.parameters = parameters;
                        PreviewNew.this.data = data;
                        int width = PreviewNew.this.parameters.getPreviewSize().width;
                        int height = PreviewNew.this.parameters.getPreviewSize().height;
                        YuvImage yuv = new YuvImage(PreviewNew.this.data, PreviewNew.this.parameters.getPreviewFormat(), width, height, null);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
                        byte[] bytes = out.toByteArray();
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        org.opencv.android.Utils.bitmapToMat(bitmap1, newimage);
//                      findLargestRectangle(newimage);

                        if(isImageCaptured)
                        {
                            camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                        }

                        Log.e("", "");
                        Log.e("", "");
                        Log.d(TAG, "onPreviewFrame - wrote bytes: " + data.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                    }
                    PreviewNew.this.invalidate();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };


    /**
     * Handles data for raw picture
     */
    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /**
     * Handles data for jpeg picture
     */
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            FileOutputStream outStream = null;
            long time = 0;
            try {
                time = System.currentTimeMillis();
                PreviewNew.this.releaseCamera();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 6;
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                Bitmap bitmap1 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                org.opencv.android.Utils.bitmapToMat(bitmap1, newimage);

                try {
                    DocScanner.getInstance().bitmap = bitmap1;
                    String str = "";
                    Intent obj_intent = new Intent(context, ScanActivity.class);
                    context.startActivity(obj_intent);
                    context.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                PreviewNew.this.releaseCamera();
                isImageCaptured = false;
            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };


    public void releaseCamera() {
        if (camera != null) {
            previewing = false;
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public boolean previewing = true;

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = camera.getParameters();
        camera.setParameters(parameters);
        camera.startPreview();
    }

    private Handler autoFocusHandler;

     @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint p = new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW", canvas.getWidth() / 2, canvas.getHeight() / 2, p);
    }


    public Bitmap createBitmapfromMat(Mat snap) {
        Bitmap bp = Bitmap.createBitmap(snap.cols(), snap.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(snap, bp);
        return bp;
    }

    public float getAngle(Point start, Point target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - start.y, target.x - start.x));

       /* if(angle < 0){
            angle += 360;
        }*/

        return angle;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}