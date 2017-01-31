package com.finder.gofrendi.finder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gofrendi on 1/29/17.
 * Saving local configurations
 */

class AppConfig extends SQLiteOpenHelper {
    public double radius;
    public String server;
    public String session;

    public AppConfig(Context context)
    {
        super(context, "finder-config.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM config", params);
        res.moveToFirst();
        this.session = res.getString(0);
        this.server = res.getString(1);
        this.radius = res.getDouble(2);
        res.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS config(session TEXT, server TEXT, radius DOUBLE);");
        String[] params = new String[0];
        Cursor res = db.rawQuery("SELECT * FROM config", params);
        if(res.getCount() == 0) {
            params = new String[]{"", "10.0.2.2:3000", "10"};
            db.execSQL("INSERT INTO config(session, server, radius) VALUES(?, ?, ?)", params);
        }
        res.close();
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.onCreate(db);
    }

    public void save()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE config SET session='"+ this.session + "', server='"+this.server +"', radius=" + this.radius);
    }
}
