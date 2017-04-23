package andrewly.receiptme.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;

import andrewly.receiptme.R;

import static andrewly.receiptme.view.MainActivity.IMAGE_GALLERY_REQUEST;

/**
 * Created by Andrew Ly on 4/22/2017.
 */

public class MenuIncludedActivity extends AppCompatActivity implements View.OnClickListener{

    protected Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                Intent uploadPhotoIntent = new Intent(Intent.ACTION_PICK);

                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                Uri data = Uri.parse(pictureDirectoryPath);

                uploadPhotoIntent.setDataAndType(data, "image/*");

                startActivityForResult(uploadPhotoIntent, IMAGE_GALLERY_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
