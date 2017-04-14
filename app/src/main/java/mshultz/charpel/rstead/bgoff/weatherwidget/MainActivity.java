package mshultz.charpel.rstead.bgoff.weatherwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    public class DownloadMaterial extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int in = inputStreamReader.read();
                while (in != -1) {
                    char c = (char)in;
                    result += c;
                    in = inputStreamReader.read();
                }
                //Log.i("result", result);
            }catch(IOException e){
                Log.e("Some Error", e.toString());
                result = "Web search failed";
            }
            return result;
        }

    }

    DownloadMaterial downloadMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadMaterial = new DownloadMaterial();

        Spinner postalCodeSpinner = (Spinner)findViewById(R.id.locationSpinner);
        String[] postalCodes = new String[] {"84102", "83340", "71730", "12078", "99223"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, postalCodes);
        postalCodeSpinner.setAdapter(adapter);

        postalCodeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onZipCodeClick(view);
            }
        });

        onZipCodeClick(null);
    }

    public void onZipCodeClick(View view){
        //String zipCode = (TextView)findViewById(R.id.whateverthehellryancallsthetextview);
        String zipCode = "84102";
        try{
            String content = downloadMaterial.execute("http://api.openweathermap.org/data/2.5/weather?zip=" + zipCode + "&units=imperial&appid=6048b5656c2d7f3ec3164e71540edca5").get();
            JSONObject jsonObject = new JSONObject(content);
            JSONArray weatherArray = new JSONArray(jsonObject.getString("weather"));
            JSONObject mainObject = new JSONObject(jsonObject.getString("main"));
            String cityName = jsonObject.getString("name");
            String temperature = mainObject.getString("temp");
            String temperatureMin = mainObject.getString("temp_min");
            String temperatureMax = mainObject.getString("temp_max");
            String weatherType = ((JSONObject)weatherArray.get(0)).getString("main");
            String weatherDescription = ((JSONObject)weatherArray.get(0)).getString("description");
            Log.i("NAME", cityName);
            Log.i("TEMP", temperature);
            Log.i("MIN", temperatureMin);
            Log.i("MAX", temperatureMax);
            Log.i("TYPE", weatherType);
            Log.i("DESCRIPTION", weatherDescription);
        }catch(ExecutionException | InterruptedException e){
            Log.e("Thread was interrupted!", e.toString());
        }catch(JSONException e){
            Log.e("Invalid data", e.toString());
        }
    }
}
