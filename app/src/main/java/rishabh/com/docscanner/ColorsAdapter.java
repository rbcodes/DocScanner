package rishabh.com.docscanner;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.MyViewHolder>  {

    private List<ContactColors> colorList;
    ScanActivity context;
    public final ColorClickListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView selection;

        public MyViewHolder(View view) {
            super(view);
            selection = (ImageView) view.findViewById(R.id.colorselection);
        }
    }


    public ColorsAdapter(ColorClickListener listener,List<ContactColors> colorList) {
        this.colorList = colorList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_colors, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ContactColors color = colorList.get(position);
        ColorDrawable cd = new ColorDrawable(color.getColor());
        holder.selection.setImageDrawable(cd);

        holder.selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClicked(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public interface ColorClickListener
    {
        void itemClicked(int position);
    }


}