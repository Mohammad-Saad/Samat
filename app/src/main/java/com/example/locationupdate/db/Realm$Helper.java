package com.example.locationupdate.db;

import android.content.Context;
import android.util.Log;

import com.example.locationupdate.FilterData;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ghulam.akber on 13-Jul-16.
 */
public class Realm$Helper {

    static Realm$Helper toOneChat$Module;
    Context context;
    Realm realm;

    public Realm$Helper(Context context) {
        this.context = context;
        realm = Realm.getInstance(context);
    }

    public static Realm$Helper getLocation$Module(Context context) {
        toOneChat$Module = new Realm$Helper(context);
        return toOneChat$Module;
    }


    // Save SubCatData
    public void save$AllData(List<FilterData> filterDataList) {

        for (int i = 0; i < filterDataList.size(); i++) {

            try {
                realm.beginTransaction();

                Location_db location_db = realm.createObject(Location_db.class);
                location_db.setID(filterDataList.get(i).getID());
                location_db.setName(filterDataList.get(i).getName());
                location_db.setLat(filterDataList.get(i).getLat());
                location_db.setLng(filterDataList.get(i).getlng());
                realm.commitTransaction();
            } catch (Exception e) {
                Log.e("save$data ", "error" + e);
                realm.cancelTransaction();
            }
        }
    }


    public List<FilterData> getAllData() {

        List<FilterData> filterDataList = new ArrayList<>();
        try {
            FilterData filterData;

            realm.beginTransaction();
            RealmResults<Location_db> location_db_results = realm.where(Location_db.class).findAll();
            for (int i = 0; i < location_db_results.size(); i++) {
                filterData = new FilterData(
                        location_db_results.get(i).getID(),
                        location_db_results.get(i).getName(),
                        location_db_results.get(i).getLat(),
                        location_db_results.get(i).getLng()
                );

                filterDataList.add(filterData);
            }

            realm.commitTransaction();

        } catch (Exception e) {
            Log.e("getDbData", "error" + e);
            realm.cancelTransaction();
        }
        return filterDataList;
    }
}


