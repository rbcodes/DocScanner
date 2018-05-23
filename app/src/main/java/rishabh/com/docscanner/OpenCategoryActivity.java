package rishabh.com.docscanner;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rishabh.com.docscanner.utils.SQLiteHandler;

public class OpenCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardsAdapter adapter;
    private List<ContactCard> albumList;
    private ArrayList<URI> images;
    ImageView back;
    String backgroundcolor;
    private SQLiteHandler db;
    ImageView changelayout;
    Boolean isGridLayout = true;
    ArrayList<HashMap<String ,String>> categorypages = new ArrayList<HashMap<String ,String>>();

    File path;


    static final String[] EXTENSIONS = new String[]{
            "jpg","gif", "png", "bmp" // and other formats you need
    };

    static final FilenameFilter IMAGE_FILTER = new FilenameFilter()
    {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_category);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        path = cw.getDir("imageDir", Context.MODE_PRIVATE);
        db = new SQLiteHandler(getApplicationContext());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        albumList = new ArrayList<>();
        adapter = new CardsAdapter(getApplicationContext(), albumList);


        back = (ImageView)findViewById(R.id.left_action);
        changelayout = (ImageView)findViewById(R.id.right_action);
        changelayout.setImageResource(R.drawable.list);

        changelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isGridLayout)
                {
                    changelayout.setImageResource(R.drawable.gridview_icon);
                    RecyclerView.LayoutManager LayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(LayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                    isGridLayout = false;
                }
                else
                {
                    changelayout.setImageResource(R.drawable.list);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(OpenCategoryActivity.this, 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(adapter);
                    isGridLayout = true;
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        Bundle bundle = getIntent().getExtras();
        String stuff = bundle.getString("category");
        String stuffid = bundle.getString("category_id");


        categorypages = db.getcategorypages(stuffid);
    for(int d = 0; d<categorypages.size();d++)
    {
        HashMap<String ,String> classname;
        classname = categorypages.get(d);

        ContactCard card = new ContactCard(classname.get("name"), stuffid, stuff, classname.get("id"), classname.get("angle"), classname.get("background_color"), new File(classname.get("uri")).toURI());
        albumList.add(card);
    }
        adapter.notifyDataSetChanged();
//        prepareAlbums(stuff);

    }


    private void prepareAlbums(String category) {

        File yourDir = new File(path + "/DocScanner/" + category);

        if (yourDir.isDirectory())
        { // make sure it's a directory
            for (final File f : yourDir.listFiles(IMAGE_FILTER))
            {
                URI uri = f.toURI();
                String namewithextension = f.getName();
                String[] name = namewithextension.trim().split("\\.");
                String namewithcolor = name[0];
                String[] namewithoutcolor = namewithcolor.split("");
//                ContactCard card = new ContactCard(namewithoutcolor[0], "", uri);
                backgroundcolor = namewithoutcolor[1];
//                albumList.add(card);
            }
            adapter.notifyDataSetChanged();
        }
        else
        {
            Toast.makeText(OpenCategoryActivity.this,"DIR NOT EXIST" + path + "/DocScanner", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
