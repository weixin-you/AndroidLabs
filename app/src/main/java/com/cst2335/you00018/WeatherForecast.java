package com.cst2335.you00018;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    protected static final String ACTIVITY_NAME = "Weather Forecast";
    private TextView currentTemperatureV, minTemperatureV, maxTemperatureV, uvRatingV;
    private ImageView weatherImageV;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        ForecastQuery networkThread = new ForecastQuery();
        //this starts doInBackground on the other thread
        networkThread.execute("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric");

        currentTemperatureV = (TextView) findViewById(R.id.text_curr_temp);
        minTemperatureV = (TextView) findViewById(R.id.text_min_temp);
        maxTemperatureV = (TextView) findViewById(R.id.text_max_temp);
        uvRatingV = (TextView) findViewById(R.id.text_uv_rating);
        weatherImageV = (ImageView) findViewById(R.id.image_weather);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //show the progress bar
        progressBar.setVisibility(View.VISIBLE);
    }

    //a subclass of AsyncTask                      Type1    Type2    Type3
    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String minTemp, maxTemp, currentTemp, uvRating, iconName;
        private Bitmap currentWeatherIcon;

        @Override
        protected String doInBackground(String... params) {
            try {
                //create the network connection
                URL temperatureUrl = new URL(params[0]); //create an URL object
                HttpURLConnection urlConnection = (HttpURLConnection) temperatureUrl.openConnection(); //open the connection
                InputStream inStream = urlConnection.getInputStream(); //wait for data

                //create a pull parser
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parseXpp = factory.newPullParser();
                parseXpp.setInput(inStream, "UTF-8");

                //now loop over the XML
                int eventType = parseXpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        //get the name of the starting tag: <tagName>
                        String tagName = parseXpp.getName();
                        if (tagName.equals("city")) {
                            String cityName = parseXpp.getAttributeValue(null, "name");
                            Log.i(ACTIVITY_NAME, "Found the city of " + cityName);
                            //tell android to call onProgressUpdate with 1 as parameter
                            //publishProgress(1);
                        } else if (tagName.equals("temperature")) {
                            currentTemp = parseXpp.getAttributeValue(null, "value");
                            Log.i(ACTIVITY_NAME, "Found current temperature: " + currentTemp);
                            publishProgress(25);

                            minTemp = parseXpp.getAttributeValue(null, "min");
                            Log.i(ACTIVITY_NAME, "Found min temperature: " + minTemp);
                            publishProgress(50);

                            maxTemp = parseXpp.getAttributeValue(null, "max");
                            Log.i(ACTIVITY_NAME, "Found max temperature: " + maxTemp);
                            //tell android to call onProgressUpdate with 75 as parameter
                            publishProgress(75);
                        } else if (tagName.equals("weather")) {
                            iconName = parseXpp.getAttributeValue(null, "icon");
                            Log.i(ACTIVITY_NAME, "Found iconName: " + iconName);
                            //tell android to call onProgressUpdate with 1 as parameter
                        }
                    }
                    //advance to next XML event
                    eventType = parseXpp.next();
                }
                //End of XML reading

                //Start of JSON reading of UV
                //create the network connection
                URL uvUrl = new URL("http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");
                HttpURLConnection UVConnection = (HttpURLConnection) uvUrl.openConnection();
                inStream = UVConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                //now a JSON table
                JSONObject jObject = new JSONObject(result);
                double aDouble = jObject.getDouble("value");
                uvRating = aDouble + "";
                //Log.i(ACTIVITY_NAME, "UV is: " + aDouble);
                Log.i(ACTIVITY_NAME, "UV is: " + uvRating);
                //END of UV rating

                //connecting or searching through file to get weather image

                if (fileExistence(iconName + ".png")) {
                    Log.i(ACTIVITY_NAME, "Looking for file" + iconName + ".png");
                    Log.i(ACTIVITY_NAME, "Weather image exists, found locally");
                    FileInputStream fileInputStream = null;
                    try {
                        fileInputStream = openFileInput(iconName + ".png");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    currentWeatherIcon = BitmapFactory.decodeStream(fileInputStream);
                } else {
                    Log.i(ACTIVITY_NAME, "Looking for file" + iconName + ".png");
                    Log.i(ACTIVITY_NAME, "Weather image does not exist, need to download");

                    URL imageUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        currentWeatherIcon = BitmapFactory.decodeStream(connection.getInputStream());
                    }
                    FileOutputStream imageOutput = openFileOutput(iconName + ".png", Context.MODE_PRIVATE);
                    currentWeatherIcon.compress(Bitmap.CompressFormat.PNG, 80, imageOutput);
                    imageOutput.flush();
                    imageOutput.close();
                    connection.disconnect();
                }
                publishProgress(100);
                //pause for 2000 milliseconds to watch the progress bar spin
                Thread.sleep(2000);
            } catch (Exception e) {
                Log.e("Crash!!", e.getMessage());
            }
            //return type 3, which is String
            return "Task finished";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i(ACTIVITY_NAME, "Update: " + values[0]);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            //the parameter String s will be "Task finished" from line 214
            currentTemperatureV.setText("  Current Temperature: " + currentTemp + " ℃");
            minTemperatureV.setText("  Min Temperature: " + minTemp + " ℃");
            maxTemperatureV.setText("  Max Temperature: " + maxTemp + " ℃");
            uvRatingV.setText("  UV Rating: " + uvRating);
            weatherImageV.setImageBitmap(currentWeatherIcon);
            progressBar.setVisibility(View.INVISIBLE);
        }

        public boolean fileExistence(String fName) {
            File file = getBaseContext().getFileStreamPath(fName);
            return file.exists();
        }
    }
}