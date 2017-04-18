package andrewly.receiptme;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static andrewly.receiptme.R.id.imgPicture;
import static android.R.attr.data;

public class ParseImageActivity extends AppCompatActivity {

    private ImageView imgPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parse_image);

        imgPicture = (ImageView)findViewById(R.id.imgPicture);

        Uri imageURI = getIntent().getData();
        InputStream inputStream;

        try {
            inputStream = getContentResolver().openInputStream(imageURI);

            // get a bitmap from the stream
            Bitmap image = BitmapFactory.decodeStream(inputStream);

            findViewById(R.id.imgPicture);
            //show image to user
            imgPicture.setImageBitmap(image);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
        }
    }
}
