package ru.oik_dev.weatherappexample;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by OsIpoFF on 04.11.16.
 */

public class WeatherFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    Typeface mWeatherFont;

    TextView mCityField,
             mUpdatedFiled,
             mDetailsFiled,
             mCurTempetureFiled,
             mWeatherIcon;

    Handler mHandler;

    public WeatherFragment() {
        mHandler = new Handler();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeatherFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/weather.ttf");
        updateWeatherData(new CityPreference(getActivity()).getCity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);

        mCityField = (TextView) rootView.findViewById(R.id.city_field);
        mUpdatedFiled = (TextView) rootView.findViewById(R.id.updated_field);
        mDetailsFiled = (TextView) rootView.findViewById(R.id.details_field);
        mCurTempetureFiled = (TextView) rootView.findViewById(R.id.current_temperature_field);
        mWeatherIcon = (TextView) rootView.findViewById(R.id.weather_icon);

        mWeatherIcon.setTypeface(mWeatherFont);

        return rootView;
    }

    private void updateWeatherData(final String city) {
        Log.d(TAG, "updateWeatherData");
        new Thread() {
            public void run() {
                Log.d(TAG, "updateWeatherData run");
                final JSONObject jsonObject = WeatherData.getJsonData(getActivity(), city);

                if (jsonObject == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    getActivity().getString(R.string.place_not_found),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(jsonObject);
                        }
                    });
                }
            }
        }.start();
    }

    public void changeCity(String city) {
        updateWeatherData(city);
    }

    public void renderWeather(JSONObject jsonObject) {
        try {
            mCityField.setText(jsonObject.getString("name").toUpperCase() + ", " +
                    jsonObject.getJSONObject("sys").getString("country"));

            JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
            JSONObject main = jsonObject.getJSONObject("main");

            mDetailsFiled.setText(details.getString("description").toUpperCase() + "\n" +
                    "Humidity: " + main.getString("humidity") + "%"+ "\n" +
                    "Pressure: " + main.getString("pressure") + " hPa");

            mCurTempetureFiled.setText(String.format("%.2f", main.getDouble("temp")) + "C");

            DateFormat dateFormat = DateFormat.getDateInstance();
            String updateOn = dateFormat.format(new Date(jsonObject.getLong("dt") * 1000));
            mUpdatedFiled.setText(updateOn);

            setWeatherIcon(details.getInt("id"),
                    jsonObject.getJSONObject("sys").getLong("sunrise") * 1000,
                    jsonObject.getJSONObject("sys").getLong("sunset") * 1000);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId/100;
        String icon = "";

        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            Log.d("SimpleWeather", "id " + id);

            switch (id) {
                case 2:
                    icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = getActivity().getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getActivity().getString(R.string.weather_cloudy);
                    break;
            }
        }

        mWeatherIcon.setText(icon);
    }
}
