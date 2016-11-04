package ru.oik_dev.weatherappexample;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by OsIpoFF on 04.11.16.
 */

public class WeatherData {
    private static final String OPEN_WEATHER_MAP_API_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    private static final String TAG = "WeatherData";

    public static JSONObject getJsonData(Context context, String city) {
        try {
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API_URL, city  ));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_api_key));

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp;

            while ((tmp = reader.readLine()) != null) {
                json.append(tmp).append("\n");
            }

            reader.close();

            JSONObject data = new JSONObject(json.toString());
            Log.d(TAG, "data: \n" + data);

            if (data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
