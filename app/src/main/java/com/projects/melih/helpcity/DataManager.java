package com.projects.melih.helpcity;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Created by melih on 3.12.2017.
 */

public class DataManager {
    private static final String PREF_NAME = "data_file";
    private static final String PREF_KEY_LAST_LOCATION = "last_location";
    private static DataManager instance;
    private final Context context;
    private final SharedPreferences sharedPreferences;
    private Gson gson;

    private DataManager(@NonNull Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

    }

    public static DataManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public void saveLastLocation(@NonNull Location location) {
        String locationJson = getGson().toJson(location);
        sharedPreferences.edit().putString(PREF_KEY_LAST_LOCATION, locationJson).apply();
    }

    @Nullable
    public Location getLastLocation() {
        Location location = null;
        String lastLocationJson = sharedPreferences.getString(PREF_KEY_LAST_LOCATION, "");
        if (!TextUtils.isEmpty(lastLocationJson)) {
            location = getGson().fromJson(lastLocationJson, Location.class);
        }
        return location;
    }
}
