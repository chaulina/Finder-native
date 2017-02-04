package com.finder.gofrendi.finder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Created by gofrendi on 1/29/17.
 * Saving local configurations
 */

class AppBackEnd extends SQLiteOpenHelper {
    public double radius;
    public String protocol;
    public String server;
    public String session;

    private String errorMessage="";

    public AppBackEnd(Context context) {
        super(context, "finder-config.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM config", params);
        res.moveToFirst();
        this.session = res.getString(0);
        this.protocol = res.getString(1);
        this.server = res.getString(2);
        this.radius = res.getDouble(3);
        res.close();
    }

    public String getErrorMessage(){
        return this.errorMessage;
    }

    public boolean loginBySession(){
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/loginBySession";
        rh.method = "GET";
        rh.requestParams = new HashMap<String, String>();
        rh.requestParams.put("session", this.session);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginBySession", result);
            if (result != "") {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    return true;
                } else {
                    this.errorMessage = json.getString("errorMessage");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean loginByEmail(String email, String password){
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/loginByEmail";
        rh.method = "GET";
        rh.requestParams = new HashMap<String, String>();
        rh.requestParams.put("email", email);
        rh.requestParams.put("password", password);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginByEmail", result);
            if (result != "") {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    return true;
                } else {
                    this.errorMessage = json.getString("errorMessage");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void logout() {
        this.session = "";
        this.save();
    }

    public void register(String email, String name, String password) {

    }

    public void save() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[]{this.session, this.protocol, this.server, String.valueOf(this.radius)};
        db.execSQL("UPDATE config SET session=?, protocol=?, server=?, radius=?", params);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS config(session TEXT, protocol TEXT, server TEXT, radius DOUBLE);");
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM config", params);
        if(res.getCount() == 0) {
            params = new String[]{"", "http", "evening-wildwood-74519.herokuapp.com", "10"};
            //params = new String[]{"", "http", "10.0.2.2:3000", "10"};
            db.execSQL("INSERT INTO config(session, protocol, server, radius) VALUES(?, ?, ?, ?)", params);
        }
        res.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }

   }
