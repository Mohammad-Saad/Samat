package com.example.locationupdate.shared_pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by mansoor.ahmed on 6/27/2016.
 */
public class SaveInSharedPreference {

    Context context;

    static SaveInSharedPreference saveInSharedPreference;

    public SaveInSharedPreference(Context context) {
        this.context = context;
    }

    public static SaveInSharedPreference getInSharedPreference(Context context) {
        if (saveInSharedPreference == null) {
            saveInSharedPreference = new SaveInSharedPreference(context);
        }
        return saveInSharedPreference;
    }

    //making session method start
    public void setLatLong(double lat,double lng) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("lat",lat+"");
        editor.putString("lng",lng+"");
        editor.apply();
    }

    public Double getLat() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString("lat","").length()>1) {
            return Double.parseDouble(prefs.getString("lat", ""));
        }
        return 0.0;
    }

    public Double getLng() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString("lng","").length()>1) {
            return Double.parseDouble(prefs.getString("lng", ""));
        }
        return 0.0;
    }


    //making session method start
    public void setCurrentLatLong(double lat,double lng) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("clat",lat+"");
        editor.putString("clng",lng+"");
        editor.apply();
    }

    public Double getCurrentLat() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString("clat","").length()>1) {
            return Double.parseDouble(prefs.getString("clat", ""));
        }
        return 0.0;
    }

    public Double getCurrentLng() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString("clng","").length()>1) {
            return Double.parseDouble(prefs.getString("clng", ""));
        }
        return 0.0;
    }


    public void setPrefDistance(String silentDistance) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("cSilentDistance",silentDistance+"");
        editor.apply();
    }

    public Double getPrefDistance() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(prefs.getString("cSilentDistance","").length()>1) {
            return Double.parseDouble(prefs.getString("cSilentDistance", ""));
        }
        return 20.0;
    }

}
