package rishabh.com.docscanner;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import rishabh.com.docscanner.utils.Categories;

public class FoldersAdapter extends ArrayAdapter<Categories> {

    private List<Categories> itemList;
    private Context context;
    private int namecheckint = 1;
    private int rollnocheckint = 1;
    public final FoldersClickListener listener;
    private int itemSize = -1;

    public FoldersAdapter(FoldersClickListener listener, List<Categories> itemList, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_1, itemList);
        this.listener = listener;
        this.itemList = itemList;
        this.context = ctx;


    }

    public int getCount() {

        if (itemList != null) {
            int totalCount = itemList.size() / 2;
            if (itemList.size() % 2 == 1) {
                totalCount = totalCount + 1;
            }
            return totalCount;
        }
        return 0;
    }

    public Categories getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    static class ViewHolder {
        protected LinearLayout layout_root;
        protected TextView foldername1;
        protected TextView foldername2;
        protected TextView folderitem1;
        protected TextView folderitem2;
        protected RelativeLayout selection1;
        protected RelativeLayout selection2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder = new ViewHolder();
        View v = convertView;
        itemSize = itemList.size() / 2;
        if (itemList.size() % 2 == 1) {
            itemSize = itemSize + 1;
        }
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.folders_list_item, null);

            viewHolder.layout_root = (LinearLayout) v.findViewById(R.id.layout_root);
            viewHolder.selection1 = (RelativeLayout) v.findViewById(R.id.selection1);
            viewHolder.selection2 = (RelativeLayout) v.findViewById(R.id.selection2);
            viewHolder.folderitem1 = (TextView) v.findViewById(R.id.folderitem1);
            viewHolder.folderitem2 = (TextView) v.findViewById(R.id.folderitem2);
            viewHolder.foldername1 = (TextView) v.findViewById(R.id.foldername1);
            viewHolder.foldername2 = (TextView) v.findViewById(R.id.foldername2);

            v.setTag(viewHolder);

            if (itemSize % 2 == 1 && (position + 1) == itemSize) {
                viewHolder.selection1.setTag(itemList.get(position * 2));
            } else {
                viewHolder.selection1.setTag(itemList.get(position * 2));
                viewHolder.selection2.setTag(itemList.get(position * 2 + 1));
            }

            Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/bernerbasisschrift1.ttf");
            viewHolder.folderitem1.setTypeface(myTypeface);
            viewHolder.folderitem2.setTypeface(myTypeface);
            viewHolder.foldername1.setTypeface(myTypeface);
            viewHolder.foldername2.setTypeface(myTypeface);

            viewHolder.selection1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactFolders element = (ContactFolders) viewHolder.selection1.getTag();
                    String name = element.getName();
                    String id = element.getId();
                    listener.itemClicked(name, id);
                }
            });

            viewHolder.selection2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContactFolders element = (ContactFolders) viewHolder.selection2.getTag();
                    String name = element.getName();
                    String id = element.getId();
                    listener.itemClicked(name, id);
                }
            });

        } else {
            v = convertView;
            ((ViewHolder) v.getTag()).selection1.setTag(itemList.get(position * 2));
            ((ViewHolder) v.getTag()).selection2.setTag(itemList.get(position * 2 + 1));
        }

        ViewHolder holder = (ViewHolder) v.getTag();

        int currentPosition = position * 2;
        Categories c, c1;

        if (itemSize % 2 == 1 && (position + 1) == itemSize) {
            holder.selection1.setVisibility(View.VISIBLE);
            c = itemList.get(currentPosition);
            holder.foldername1.setText(c.getName());
            holder.foldername1.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            params.setMargins(10, 0, 10, 0);
            holder.selection1.setLayoutParams(params);
            holder.folderitem1.setVisibility(View.GONE);
        } else {
            holder.selection1.setVisibility(View.VISIBLE);
            holder.selection2.setVisibility(View.VISIBLE);
            c = itemList.get(currentPosition);
            c1 = itemList.get(currentPosition + 1);
            holder.foldername1.setText(c.getName());
            holder.folderitem1.setText(c.getNumberofposts()+"");
            holder.foldername2.setText(c1.getName());
            holder.folderitem2.setText(c1.getNumberofposts()+"");
        }

        return v;

    }

    public List<Categories> getItemList() {
        return itemList;
    }

    public void setItemList(List<Categories> itemList) {
        this.itemList = itemList;
    }

    public void setnamechekint(int a) {
        this.namecheckint = a;
    }

    public void setrollnochekint(int a) {
        this.rollnocheckint = a;
    }

    public interface FoldersClickListener {
        void itemClicked(String name, String id);
    }


}