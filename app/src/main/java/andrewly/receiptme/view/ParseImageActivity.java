package andrewly.receiptme.view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.woxthebox.draglistview.DragListView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import andrewly.receiptme.R;
import andrewly.receiptme.controller.ItemAdapter;
import andrewly.receiptme.model.DatabaseHelper;
import andrewly.receiptme.model.OcrGraphic;
import andrewly.receiptme.model.PurchasedItem;
import andrewly.receiptme.model.TextBlockHolder;
import andrewly.receiptme.model.dao.ItemDao;
import andrewly.receiptme.view.reader.TextBlockReader;

import static android.R.attr.category;
import static android.R.attr.value;

@SuppressWarnings("CheckStyle")
public class ParseImageActivity extends MenuIncludedActivity implements ItemAdapter.ItemClickCallback {

    private DatabaseHelper databaseHelper;

    private ImageView imgPicture;
    private Bitmap imageBitmap;
    private List<Double> parsedPriceList;
    private List<String> itemsList;
    private android.widget.TableRow.LayoutParams layoutParams;
    private List<PurchasedItem> listData;

    private RecyclerView itemRecyclerView;
    private ItemAdapter adapter;

    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main2_parse_image);

        //initialize database
        databaseHelper = new DatabaseHelper(this);

        //RecyclerView setting
        itemRecyclerView = (RecyclerView) findViewById(R.id.rec_list);

        //LayoutManager: Using LinearLayout to show a lit
        itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adding ItemTouchHelper

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
        itemTouchHelper.attachToRecyclerView(itemRecyclerView);

        boolean isPhotoCapture = getIntent().getBooleanExtra("photo_capture_bool", false);
        Log.d("value_of_parcelable", getIntent().getParcelableExtra("photo_capture_bool") + "" );

        if (isPhotoCapture) {
            parsedPriceList = TextBlockReader.parsedPriceList;
            itemsList = TextBlockReader.itemsList;

        }
        else {
            imgPicture = (ImageView) findViewById(R.id.imgPicture);
            parsedPriceList = new ArrayList<>();
            itemsList = new ArrayList<>();

            Uri imageURI = getIntent().getData();
            InputStream inputStream;

            try {
                inputStream = getContentResolver().openInputStream(imageURI);

                // get a bitmap from the stream
                imageBitmap = BitmapFactory.decodeStream(inputStream);

                findViewById(R.id.imgPicture);
                //show image to user
                //imgPicture.setImageBitmap(imageBitmap);
                readImage();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
            }
        }

        listData = (ArrayList) ItemDao.getCreatedListData(itemsList, parsedPriceList);

        //Setting the adapter here
        adapter = new ItemAdapter(listData, this);
        itemRecyclerView.setAdapter(adapter);

        //Setup the Save FAB

        FloatingActionButton takePictureFab = (FloatingActionButton) findViewById(R.id.fab_submit_data);
        takePictureFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //process data from another method
                listData = updateListData();
                databaseHelper.insertPurchasedItems(listData);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        return true;
    }

    public void readImage() {
        Context context = getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        SparseArray<TextBlock> retBlock = textRecognizer.detect((new Frame.Builder().setBitmap(imageBitmap)).build());

        TextBlockReader.iterateAllTextBlocks(retBlock);
        parsedPriceList = TextBlockReader.parsedPriceList;
        itemsList = TextBlockReader.itemsList;


    }

    public void createDataTable() {
        TableLayout itemPrices = (TableLayout) findViewById(R.id.item_price_table);

        itemPrices.setStretchAllColumns(true);
        itemPrices.bringToFront();

        addFirstRowToTable(itemPrices);

        for (int i = 0; i < maxSizeOfItems(); i++) {
            addRowToTable(getItem(itemsList, i), getItem(parsedPriceList, i), itemPrices);
        }
    }

    private void addFirstRowToTable(TableLayout table) {
        TableRow tr = new TableRow(this);

        TextView items = new TextView(this);
        TextView prices = new TextView(this);
        TextView category = new TextView(this);

        items.setText("ITEMS");
        prices.setText("PRICES");
        category.setText("CATEGORY");

        items.setTextSize(20);
        prices.setTextSize(20);
        category.setTextSize(20);

        items.setGravity(Gravity.CENTER_HORIZONTAL);
        prices.setGravity(Gravity.CENTER_HORIZONTAL);
        category.setGravity(Gravity.CENTER_HORIZONTAL);

        tr.addView(items);
        tr.addView(prices);
        tr.addView(category);

        table.addView(tr);
    }

    private void addRowToTable(String col1, String col2, TableLayout table) {
        TableRow tr = new TableRow(this);

        EditText c1 = new EditText(this);
        EditText c2 = new EditText(this);
        Spinner categoryDropdown = createSpinner();

        c1.setText(col1);
        c2.setText(col2);

        c1.setTextSize(16);
        c2.setTextSize(16);

        c1.setPadding(0, 10, 0, 10);
        c2.setPadding(0, 10, 0, 10);

        //setItemDragListener(c1);
        //setItemDragListener(c2);

        c1.setGravity(Gravity.CENTER_HORIZONTAL);
        c2.setGravity(Gravity.CENTER_HORIZONTAL);

        tr.addView(c1);
        tr.addView(c2);
        tr.addView(categoryDropdown);

        table.addView(tr);
    }

    private Spinner createSpinner() {
        Spinner retSpinner = new Spinner(this);
        List<String> list = new ArrayList<String>();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, R.layout.custom_text_view);

        retSpinner.setAdapter(adapter);

        return retSpinner;
    }

    private void setItemDragListener(final TextView text) {

        text.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(text);

                v.startDrag(dragData, myShadow, null, 0);
                return true;
            }
        });

        text.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (TableRow.LayoutParams) v.getLayoutParams();
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
                        v.setVisibility(View.VISIBLE);
                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d(msg, "ACTION_DROP event");
                        v.setVisibility(View.VISIBLE);
                        // Do nothing
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(text);

                    text.startDrag(data, shadowBuilder, text, 0);
                    text.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private String getItem(List<? extends Object> arr, int idx) {

        if (idx >= arr.size() || arr.get(idx) == null) {
            return "";
        } else {
            return arr.get(idx).toString();
        }
    }

    private int maxSizeOfItems() {
        if (parsedPriceList.size() >= itemsList.size()) {
            return parsedPriceList.size();
        } else {
            return itemsList.size();
        }
    }

    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        //moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        deleteItem(viewHolder.getAdapterPosition());
                    }
                };

        return simpleItemTouchCallback;
    }

    private void deleteItem(final int position) {
        listData.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onItemClick(int p) {
        //TO-DO
    }

    @Override
    public void onSecondaryIconClick(int p) {
        //TO-DO
    }

    public ArrayList<PurchasedItem> updateListData() {
        RecyclerView.LayoutManager layoutManager = itemRecyclerView.getLayoutManager();
        int lastItemPosition;
        ArrayList<PurchasedItem> retList = new ArrayList<>();

        lastItemPosition = layoutManager.getItemCount();

        for (int currPosition = 0; currPosition < lastItemPosition; currPosition++) {
            ItemAdapter.ItemHolder holder = (ItemAdapter.ItemHolder) itemRecyclerView.findViewHolderForAdapterPosition(currPosition);

            PurchasedItem item = new PurchasedItem();
            item.setItemName(holder.getTitle());
            item.setCost(holder.getCost());
            item.setCategory(holder.getCategory());

            retList.add(item);
        }

        return retList;

    }
}
