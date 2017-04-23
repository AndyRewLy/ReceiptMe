package andrewly.receiptme.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import andrewly.receiptme.R;

import static android.R.attr.value;

public class ParseImageActivity extends MenuIncludedActivity {

    private ImageView imgPicture;
    private Bitmap imageBitmap;
    private List<Double> parsedPriceList;
    private List<String> itemsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_image);

        imgPicture = (ImageView)findViewById(R.id.imgPicture);
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
            imgPicture.setImageBitmap(imageBitmap);
            readImage();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
        }
    }

    public void readImage() {
        Context context = getApplicationContext();
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        SparseArray<TextBlock> retBlock = textRecognizer.detect((new Frame.Builder().setBitmap(imageBitmap)).build());

        iterateAllTextBlocks(retBlock);
    }

    public void iterateAllTextBlocks(SparseArray<TextBlock> retBlock) {
        if (retBlock != null) {
            for (int i = 0; i < retBlock.size(); i++) {
                Log.i("Recognize Text", "Iterating through current block" + i);
                iterateThroughTextBlock(retBlock.valueAt(i));
            }
        }
    }

    public void iterateThroughTextBlock(TextBlock textComponents) {
        if (textComponents != null) {
            List<? extends Text> textValues = textComponents.getComponents();
            iterateThroughTextValues(textValues);
        }
    }

    public void iterateThroughTextValues(List<? extends Text> textValues) {
        if (textValues != null) {
            for (int i = 0; i < textValues.size(); i++) {
                Log.i("Recognize Text", "Text Found: " + textValues.get(i).getValue());
                if (checkIfTextIsPrice(textValues.get(i).getValue())) {

                }
                else {
                    checkIfTextIsItem(textValues.get(i).getValue());
                }
            }
        }
    }

    public boolean checkIfTextIsPrice(String value) {
        double tempValue;

        if (value.contains(".")) {
            value = value.replaceAll("[^0-9.]","");
            Log.i("Recognize Text", "obatined value of " + value);

            String[] removeSpaces = value.split(" ");

            for (int i = 0; i < removeSpaces.length; i++) {
                tempValue = Double.parseDouble(removeSpaces[i]);
                parsedPriceList.add((Double)tempValue);
                return true;
            }
        }

        return false;
    }

    public void checkIfTextIsItem(String value) {
        value = value.replaceAll("[^A-Za-z,/]", "");
        Log.i("Recognize Text", "Item name of " + value);
        itemsList.add(value);
    }
}
