package com.example.productab;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.clevertap.android.sdk.CTExperimentsListener;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements CTExperimentsListener {

    TextView txt;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CleverTapAPI.setUIEditorConnectionEnabled(true);//Set to false in production
        CleverTapAPI.setDebugLevel(3); //Set to OFF in production
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt = (TextView) findViewById(R.id.text);
        img = (ImageView) findViewById(R.id.imageView2);
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
    }


    @Override
    public void CTExperimentsUpdated() {
        CleverTapAPI clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());

        String txtCT = null;
        String txt2CT = null;
        if (clevertapDefaultInstance != null) {
            txtCT = clevertapDefaultInstance.getStringVariable("txt", "This is default text and below is default image!");
            txt2CT = clevertapDefaultInstance.getStringVariable("img", "https://increasify.com.au/wp-content/uploads/2016/08/default-image.png");
        }

        txt.setText(txtCT);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute(txt2CT);

    }
    private class AsyncTaskRunner extends AsyncTask<String, String, Bitmap> {

        Bitmap imagefromCT = null;

        @Override
        protected Bitmap doInBackground(String... params) {
            String srcUrl = params[0];
            srcUrl = srcUrl.replace("///", "/");
            srcUrl = srcUrl.replace("//", "/");
            srcUrl = srcUrl.replace("http:/", "http://");
            srcUrl = srcUrl.replace("https:/", "https://");
            HttpURLConnection connection = null;
            try {
                URL url = new URL(srcUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                imagefromCT = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                Logger.v("Couldn't download the Image. URL was: " + srcUrl);
            } finally {
                try {
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (Throwable t) {
                    Logger.v("Couldn't close connection!", t);
                }
            }
            return imagefromCT;
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            img.setImageBitmap(imagefromCT);
        }


        @Override
        protected void onPreExecute() {
        }


    }
}
