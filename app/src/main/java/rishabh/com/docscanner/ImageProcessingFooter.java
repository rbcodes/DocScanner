package rishabh.com.docscanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Button;

/**
 * Created by stpl on 10/5/2016.
 */
public class ImageProcessingFooter
{
    Bitmap bitmap;
    String selection;
    Context context;

    public void footerselection(Context context, Bitmap bitmap, String selection)
    {
        this.bitmap = bitmap;
        this.selection = selection;
        this.context = context;
    }

    public void setSelection()
    {

    }

}
