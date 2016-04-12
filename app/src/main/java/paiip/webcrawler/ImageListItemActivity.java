package paiip.webcrawler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageListItemActivity extends AppCompatActivity {

    public String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list_item);

        Intent imageListItemIntent = getIntent();
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView imageDescription = (TextView) findViewById(R.id.imageDescription);
        TextView imageNotes = (TextView) findViewById(R.id.imageNotes);
        TextView imageBlogSource = (TextView) findViewById(R.id.imageBlogSource);

        imagePath = imageListItemIntent.getStringExtra("imagePath");

        imageView.setImageBitmap(loadImageFromStorage(imageListItemIntent.getStringExtra("imagePath")));
        imageDescription.setText(imageListItemIntent.getStringExtra("imageDescription"));
        imageNotes.setText(imageListItemIntent.getStringExtra("imageNotes"));
        imageBlogSource.setText(imageListItemIntent.getStringExtra("imageBlogSource"));
    }

    private Bitmap loadImageFromStorage(String filename)
    {
        try {
            File f = new File(filename);
            if (!f.exists()) { return null; }
            Bitmap tmp = BitmapFactory.decodeFile(filename);
            return tmp;

        } catch (Exception e) {
            return null;
        }
    }

    public void onClickSave(View view) {
        Toast toast = Toast.makeText(ImageListItemActivity.this, "The image was saved.",
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onClickShareContent(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        File media = new File(imagePath);
        Uri imageUri = Uri.fromFile(media);
        sharingIntent.setType("image/jpg");

        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(sharingIntent, "Share image to..."));

    }
}
