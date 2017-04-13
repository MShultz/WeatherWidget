package mshultz.charpel.rstead.bgoff.weatherwidget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.ImageView;
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

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                Log.i("name: ", jsonObject.getString("name"));
                Log.i("weather: ", jsonObject.getString("weather"));
                JSONArray jsonArray = new JSONArray(jsonObject.getString("weather"));
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    Log.i("main: ", jsonObject1.getString("main"));
                    Log.i("description: ", jsonObject1.getString("description"));
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    DownloadMaterial downloadMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadMaterial = new DownloadMaterial();

    }

    public void onZipCodeClick(View view){
        //String zipCode = (TextView)findViewById(R.id.whateverthehellryancallsthetextview);
        String zipCode = "84102";
        try{
            String content = downloadMaterial.execute("http://api.openweathermap.org/data/2.5/weather?p=" + zipCode + "&units=imperial&appid=6048b5656c2d7f3ec3164e71540edca5").get();
            JSONObject jsonObject = new JSONObject(content);
            JSONArray weatherArray = new JSONArray(jsonObject.getString("weather"));
        }catch(ExecutionException | InterruptedException e){
            Log.e("Thread was interrupted!", e.toString());
        }catch(JSONException e){
            Log.e("Invalid data", e.toString());
        }
    }
}
