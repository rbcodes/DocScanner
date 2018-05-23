package rishabh.com.docscanner;

import android.graphics.Bitmap;

/**
 * Created by stpl on 8/26/2016.
 */
public class DocScanner {
    private static DocScanner docScanner;
    public Bitmap bitmap;
    public Bitmap bitmapconverted;
    public Bitmap bitmapprocessed;
    public Bitmap bitmapcard;
    private DocScanner(){}

    public static DocScanner getInstance(){
        if(docScanner == null){
            docScanner = new DocScanner();
        }
        return docScanner;
    }

}
