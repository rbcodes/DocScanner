package rishabh.com.docscanner;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.widget.ImageView;
import org.opencv.core.Rect;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ImageCrop {

    Mat newimage = new Mat();
    Bitmap bitmap;
    Bitmap abc3;
    Boolean rectfound = false;
    Bitmap abc;
    ImageView greatestrectimg;
    private Point topleft , topright, bottomleft, bottomright;
    private final static String TAG = "BetSlipReader";
    List<Point> newpoints = new ArrayList<Point>();
    Point p1,p2,p3,p4;
    Bitmap bp;
    Mat boundingSquare;


    public float[] getPoints(Bitmap bitmap1)
    {
        bitmap = bitmap1.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(bitmap,newimage);
          List<Point> pointlist = findLargestRectangle(newimage);
        float[] arr = {(float) pointlist.get(0).x,(float) pointlist.get(1).x,(float) pointlist.get(2).x,(float) pointlist.get(3).x,(float) pointlist.get(0).y,(float) pointlist.get(1).y,(float) pointlist.get(2).y,(float) pointlist.get(3).y};
        return arr;
    }

    private List<Point> findLargestRectangle(Mat original_image) {

        Mat imgSource = new Mat(original_image.size(), CvType.CV_8U);
        Imgproc.cvtColor(original_image, imgSource, Imgproc.COLOR_BGR2GRAY);
        Imgproc.adaptiveThreshold(imgSource, imgSource, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 15, 40);
        Imgproc.GaussianBlur(imgSource, imgSource, new Size(9, 9), 2, 2);

/*      Imgproc.cvtColor(original_image, imgSource, Imgproc.COLOR_BGR2GRAY);
        //convert the image to black and white does (8 bit)
        Imgproc.Canny(imgSource, imgSource, 50, 50);
        //apply gaussian blur to smoothen lines of dots
        Imgproc.GaussianBlur(imgSource, imgSource, new Size(5, 5), 5);*/

        abc = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        abc = createBitmapfromMat(imgSource);

        final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //find the contours
        //Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        Imgproc.findContours(imgSource, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        double maxArea = -1;
        MatOfPoint temp_contour;
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f maxCurve = new MatOfPoint2f();
        Log.d("FFFFFFFFFFF", "size--" + contours.size());

        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourarea = Imgproc.contourArea(temp_contour);
            //compare this contour to the previous largest contour found
            if (contourarea > maxArea) {
                //check if this contour is a square
                MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
                int contourSize = (int) temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, contourSize * 0.05, true);
                if (approxCurve_temp.total() == 4 && contourarea > 5000) {
                    maxArea = contourarea;
                    maxCurve = approxCurve_temp;
                    approxCurve = approxCurve_temp;
                    rectfound = true;
                }
            }
        }

        if(rectfound) {
            double temp_double[] = maxCurve.get(0, 0);
            p1 = new Point(temp_double[0], temp_double[1]);
            temp_double = maxCurve.get(1, 0);
            p2 = new Point(temp_double[0], temp_double[1]);
            temp_double = maxCurve.get(2, 0);
            p3 = new Point(temp_double[0], temp_double[1]);
            temp_double = maxCurve.get(3, 0);
            p4 = new Point(temp_double[0], temp_double[1]);
            newpoints.add(p1);
            newpoints.add(p2);
            newpoints.add(p3);
            newpoints.add(p4);
        }
        else {
            p1 = new Point(0, 0);
            p2 = new Point(bitmap.getWidth(),0);
            p3 = new Point(0, bitmap.getHeight());
            p4 = new Point(bitmap.getWidth(), bitmap.getHeight());
            newpoints.add(p1);
            newpoints.add(p2);
            newpoints.add(p3);
            newpoints.add(p4);
        }

            Collections.sort(newpoints, new Comparator<Point>() {

                public int compare(Point o1, Point o2) {
                    return Double.compare(o1.x, o2.x);
                }
            });

            if (newpoints.get(0).y > newpoints.get(1).y) {
                bottomleft = newpoints.get(0);
                topleft = newpoints.get(1);
            }
            else
            {
                bottomleft = newpoints.get(1);
                topleft = newpoints.get(0);
            }
            if (newpoints.get(2).y > newpoints.get(3).y) {
                bottomright = newpoints.get(2);
                topright = newpoints.get(3);
            }
            else
            {
                bottomright = newpoints.get(3);
                topright = newpoints.get(2);
            }

            p1 = topright;
            p2 = topleft;
            p3 = bottomleft;
            p4 = bottomright;
            return newpoints;
        }

    public Bitmap createBitmapfromMat(final Mat snap) {
        bp = null;
        bp = Bitmap.createBitmap(snap.cols(), snap.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(snap, bp);
        return bp;
    }

    public float getAngle(Point start,Point target) {
        float angle = (float) Math.toDegrees(Math.atan2(target.y - start.y, target.x - start.x));
       /* if(angle < 0){
            angle += 360;
        }*/
        return angle;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Bitmap getScannedBitmap(Bitmap original,double x1,double y1,double x2,double y2,double x3,double y3,double x4,double y4)
    {
        Bitmap bmp32 = original.copy(Bitmap.Config.ARGB_8888, true);
        bitmap = bmp32;
        org.opencv.android.Utils.bitmapToMat(bmp32,newimage);
        Rect roi = new Rect(new Point(x1,y1), new Point(x2,y2));
        boundingSquare = new Mat(newimage, roi);
         bmp32 = createBitmapfromMat(boundingSquare);
        return bmp32;
    }


}
