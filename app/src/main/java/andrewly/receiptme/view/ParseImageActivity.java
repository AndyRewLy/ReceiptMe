package andrewly.receiptme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import andrewly.receiptme.R;
import andrewly.receiptme.view.adapter.ItemAdapter;
import andrewly.receiptme.model.PurchasedItem;
import andrewly.receiptme.model.dao.ItemDao;
import andrewly.receiptme.model.dao.SQLDatabaseConnector;
import andrewly.receiptme.model.ocr.TextBlockReader;

@SuppressWarnings("CheckStyle")
public class ParseImageActivity extends AppCompatActivity implements ItemAdapter.ItemClickCallback {

    private SQLDatabaseConnector databaseHelper;

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
        databaseHelper = SQLDatabaseConnector.getInstance(this);

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

            PurchasedItem item = new PurchasedItem(holder.getTitle(), holder.getCost(), holder.getCategory());

            retList.add(item);
        }

        return retList;
    }
}
