package ru.oik_dev.weatherappexample;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by OsIpoFF on 04.11.16.
 */
public class CityPreference {
    private SharedPreferences mPrefs;

    private static final String PREF_CITY_KEY = "city";

    public CityPreference(Activity activity) {
        mPrefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity() {
        return mPrefs.getString(PREF_CITY_KEY, "Moscow");
    }

    public void setCity(String city) {
        mPrefs.edit().putString(PREF_CITY_KEY, city);
    }
}
