package rishabh.com.docscanner.utils;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class RotationGestureDetector {

    private static final int INVALID_POINTER_ID = -1;
    private PointF mFPoint = new PointF();
    private PointF mSPoint = new PointF();
    private int mPtrID1, mPtrID2;
    private float mAngle;
    private Matrix mZoom;
    private float oldAngle;
    private View mView;
    private PointF mid = new PointF();

    private OnRotationGestureListener mListener;

    float oldDist = 1f;

    public float getAngle() {
        return mAngle;
    }
    public Matrix getZoom() {
        return mZoom;
    }

    public RotationGestureDetector(OnRotationGestureListener listener, View v) {
        mListener = listener;
        mView = v;
        mPtrID1 = INVALID_POINTER_ID;
        mPtrID2 = INVALID_POINTER_ID;
    }

    public boolean onTouchEvent(Matrix matrix, MotionEvent event){


        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_OUTSIDE:
                break;
            case MotionEvent.ACTION_DOWN:
                mPtrID1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mZoom = matrix;
                }
                mPtrID2 = event.getPointerId(event.getActionIndex());
                oldAngle = mAngle;
                getRawPoint(event, mPtrID1, mSPoint);
                getRawPoint(event, mPtrID2, mFPoint);

                break;
            case MotionEvent.ACTION_MOVE:
                if (mPtrID1 != INVALID_POINTER_ID && mPtrID2 != INVALID_POINTER_ID){

                    float newDist = spacing(event);
                    PointF nfPoint = new PointF();
                    PointF nsPoint = new PointF();

                    getRawPoint(event, mPtrID1, nsPoint);
                    getRawPoint(event, mPtrID2, nfPoint);

                    mAngle = angleBetweenLines(mFPoint, mSPoint, nfPoint, nsPoint) + oldAngle;

                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        mZoom.postScale(scale, scale, mid.x, mid.y);
                    }

                    if (mListener != null) {
                        mListener.onRotation(this);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                mPtrID1 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPtrID2 = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_CANCEL:
                mPtrID1 = INVALID_POINTER_ID;
                mPtrID2 = INVALID_POINTER_ID;
                break;
            default:
                break;
        }
        return true;
    }

    void getRawPoint(MotionEvent ev, int index, PointF point){
        final int[] location = { 0, 0 };
        mView.getLocationOnScreen(location);

        float x = ev.getX(index);
        float y = ev.getY(index);

        double angle = Math.toDegrees(Math.atan2(y, x));
        angle += mView.getRotation();

        final float length = PointF.length(x, y);

        x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
        y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

        point.set(x, y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private float angleBetweenLines(PointF fPoint, PointF sPoint, PointF nFpoint, PointF nSpoint)
    {
        float angle1 = (float) Math.atan2((fPoint.y - sPoint.y), (fPoint.x - sPoint.x));
        float angle2 = (float) Math.atan2((nFpoint.y - nSpoint.y), (nFpoint.x - nSpoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
    public interface OnRotationGestureListener {
        void onRotation(RotationGestureDetector rotationDetector);
    }
}