package paiip.webcrawler;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import TumblrParser.TumblrBlogParser;
import TumblrParser.TumblrEntitiesWrapper;
import TumblrParser.TumblrEntity;


public class ImageListActivity extends Activity {

    ProgressDialog mProgressDialog;
    ArrayList<TumblrEntity> tumblrEntities;
    File xmlTumblrData;
    ListView listView;
    ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        xmlTumblrData = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TumblrEntities.xml");

        if (isNetworkAvailable()) {
            new TumblrDataFromInternet().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            new TumblrDataToStorage().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }
        else {
            new TumblrDataFromStorage().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class TumblrDataFromInternet extends AsyncTask<Void, Void, ArrayList<TumblrEntity>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(ImageListActivity.this);
            mProgressDialog.setTitle("Foolishthings Tumblr");
            mProgressDialog.setMessage("Downloading ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected ArrayList<TumblrEntity> doInBackground(Void... params) {
            TumblrBlogParser tumblrBlogParser = new TumblrBlogParser("http://these-foolishthings.tumblr.com/");
            tumblrEntities = tumblrBlogParser.getTumblrData();

            for (TumblrEntity tumblrEntity : tumblrEntities) {
                tumblrEntity.Image = getBitmapFromURL(tumblrEntity.UrlImage);
            }

            return tumblrEntities;
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                java.net.URL url = new java.net.URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ImageListActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<TumblrEntity> result) {
            tumblrEntities = result;
            listView = (ListView) findViewById(R.id.listView);
            adapter = new ImageListAdapter(ImageListActivity.this, tumblrEntities);
            listView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    private class TumblrDataFromStorage extends AsyncTask<Void, Void, ArrayList<TumblrEntity>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog = new ProgressDialog(ImageListActivity.this);
            mProgressDialog.setTitle("Foolishthings Tumblr");
            mProgressDialog.setMessage("Loading from local directory ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }


        @Override
        protected ArrayList<TumblrEntity> doInBackground(Void... params) {
            tumblrEntities = deserializeTumblrEntites().getTumblrEntities();

            for (TumblrEntity tumblrEntity : tumblrEntities) {
                tumblrEntity.Image = loadImageFromStorage(tumblrEntity.ImagePath);
            }

            return tumblrEntities;
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

        private TumblrEntitiesWrapper deserializeTumblrEntites() {
            TumblrEntitiesWrapper wrapper = null;

            if (xmlTumblrData.exists()) {
                try
                {
                    Serializer serializer = new Persister();
                    wrapper = serializer.read(TumblrEntitiesWrapper.class, xmlTumblrData);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
            }
            return wrapper;
        }

        @Override
        protected void onPostExecute(ArrayList<TumblrEntity> result) {
            listView = (ListView) findViewById(R.id.listView);
            adapter = new ImageListAdapter(ImageListActivity.this, tumblrEntities);
            listView.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }

    private class TumblrDataToStorage extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            File directory = getDirectoryPath();
            int imageNumber = 0;

            for (TumblrEntity tumblrEntity : tumblrEntities) {
                File imagePath = new File(directory, String.format("image%s.jpg", imageNumber++));
                tumblrEntity.ImagePath = imagePath.getPath();
                saveToFile(tumblrEntity.ImagePath, tumblrEntity.Image);
            }

            serializeTumblrEntites(new TumblrEntitiesWrapper(tumblrEntities));

            return null;
        }

        private void serializeTumblrEntites(TumblrEntitiesWrapper tumblrEntitiesWrapper) {
            try
            {
                Serializer serializer = new Persister();
                serializer.write(tumblrEntitiesWrapper, xmlTumblrData);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        private void saveToFile(String filename, Bitmap bmp) {
            try {
                FileOutputStream out = new FileOutputStream(filename);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch(Exception e) {

            }
        }

        public File getDirectoryPath() {
            File path;
            if (hasSDCard()) {
                path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TumblrImages/");
                path.mkdir();
            } else {
                path = Environment.getDataDirectory();
            }
            return path;
        }

        public boolean hasSDCard() {
            String status = Environment.getExternalStorageState();
            return status.equals(Environment.MEDIA_MOUNTED);
        }

        @Override
        protected void onPostExecute(Void result) {

        }
    }
}

