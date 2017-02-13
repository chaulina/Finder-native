package com.finder.gofrendi.finder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by gofrendi on 1/29/17.
 * Saving local configurations
 */

class AppBackEnd extends SQLiteOpenHelper {
    double radius;
    String protocol;
    String server;
    String session;

    static String currentUserName;
    static String currentUserEmail;
    static String currentUserProfilePicture;

    private String errorMessage="";

    public AppBackEnd(Context context) {
        super(context, "finder-backEnd.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM backEnd", params);
        res.moveToFirst();
        this.session = res.getString(0);
        this.protocol = res.getString(1);
        this.server = res.getString(2);
        this.radius = res.getDouble(3);
        res.close();
    }

    String getCurrentUserName() {
        return AppBackEnd.currentUserName;
    }

    String getCurrentUserEmail() {
        return AppBackEnd.currentUserEmail;
    }

    String getCurrentUserProfilePicture() {
        return AppBackEnd.currentUserProfilePicture;
    }

    String getErrorMessage(){
        return this.errorMessage;
    }

    boolean register(String email, String name, String password) {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/register";
        rh.method = "GET";
        rh.requestParams.put("email", email);
        rh.requestParams.put("name", name);
        rh.requestParams.put("password", password);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginByEmail", result);
            if (!result.equals("")) {
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

    boolean loginBySession() {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/loginBySession";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginBySession", result);
            if (!result.equals("")) {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    // get current user into
                    JSONObject currentUser =json.getJSONObject("user");
                    AppBackEnd.currentUserName = currentUser.getString("name");
                    AppBackEnd.currentUserEmail = currentUser.getString("email");
                    AppBackEnd.currentUserProfilePicture = currentUser.getString("profile_picture");
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

    boolean loginByEmail(String email, String password) {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/loginByEmail";
        rh.method = "GET";
        rh.requestParams.put("email", email);
        rh.requestParams.put("password", password);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginByEmail", result);
            if (!result.equals("")) {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    // get current user info
                    JSONObject currentUser =json.getJSONObject("user");
                    AppBackEnd.currentUserName = currentUser.getString("name");
                    AppBackEnd.currentUserEmail = currentUser.getString("email");
                    AppBackEnd.currentUserProfilePicture = currentUser.getString("profile_picture");
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

    void logout() {
        this.session = "";
        this.save();
        AppBackEnd.currentUserName = "";
        AppBackEnd.currentUserEmail = "";
        AppBackEnd.currentUserProfilePicture = "";
    }

    boolean move(double lat, double lon) {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/move";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        rh.requestParams.put("newLat", String.valueOf(lat));
        rh.requestParams.put("newLon", String.valueOf(lon));
        try {
            String result = rh.execute().get();
            Log.d("my.ac.loginByEmail", result);
            if (!result.equals("")) {
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

    boolean changeProfilePicture(String profilePictureFileName) {
        Log.d("my.ac.changePP", "start");
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/changeProfilePicture";
        rh.method = "POST";
        rh.requestParams.put("session", this.session);
        rh.fileParams.put("profilePicture", profilePictureFileName);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.changePP", result);
            if (!result.equals("")) {
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

    boolean swipeRight(String targetEmail) {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/swipeRight";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        rh.requestParams.put("targetEmail", targetEmail);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.swipRight", result);
            if (!result.equals("")) {
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

    boolean chat(String targetEmail, String message) {
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/chat";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        rh.requestParams.put("targetEmail", targetEmail);
        rh.requestParams.put("message", message);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.chat", result);
            if (!result.equals("")) {
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

    JSONArray getAvailableUserList(double radius){
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/getAvailableUserList";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        rh.requestParams.put("radius", String.valueOf(radius));
        try {
            String result = rh.execute().get();
            Log.d("my.ac.getUserList", result);
            if (!result.equals("")) {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    return json.getJSONArray("userList");
                } else {
                    this.errorMessage = json.getString("errorMessage");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new JSONArray();
    }

    JSONArray getMatchList(){
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/getMatchList";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.getMatchList", result);
            if (!result.equals("")) {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    return json.getJSONArray("userList");
                } else {
                    this.errorMessage = json.getString("errorMessage");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new JSONArray();
    }

    JSONArray getChatList(String targetEmail){
        RequestHelper rh = new RequestHelper();
        rh.urlAddress = this.protocol + "://" + this.server + "/getChatList";
        rh.method = "GET";
        rh.requestParams.put("session", this.session);
        rh.requestParams.put("targetEmail", targetEmail);
        try {
            String result = rh.execute().get();
            Log.d("my.ac.getMatchList", result);
            if (!result.equals("")) {
                JSONObject json = new JSONObject(result);
                if (json.getBoolean("success")) {
                    // loginBySession succeed
                    this.session = json.getString("session");
                    this.save();
                    return json.getJSONArray("chatList");
                } else {
                    this.errorMessage = json.getString("errorMessage");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return new JSONArray();
    }

    void save() {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[]{this.session, this.protocol, this.server, String.valueOf(this.radius)};
        db.execSQL("UPDATE backEnd SET session=?, protocol=?, server=?, radius=?", params);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS backEnd(session TEXT, protocol TEXT, server TEXT, radius DOUBLE);");
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM backEnd", params);
        if(res.getCount() == 0) {
            params = new String[]{"test@test.com_2623", "http", "evening-wildwood-74519.herokuapp.com", "10"};
            //params = new String[]{"", "http", "10.0.2.2:3000", "10"};
            db.execSQL("INSERT INTO backEnd(session, protocol, server, radius) VALUES(?, ?, ?, ?)", params);
        }
        res.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        this.onCreate(db);
    }

   }
