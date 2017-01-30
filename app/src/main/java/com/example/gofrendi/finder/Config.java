package com.example.gofrendi.finder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gofrendi on 1/29/17.
 */

public class Config extends SQLiteOpenHelper {
    public double radius;
    public String color;
    public String session;

    public Config(Context context)
    {
        super(context, "finder-config.db", null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM config");
        res.moveToFirst();
        this.session = res.getString(0);
        this.color = res.getString(1);
        this.radius = res.getDouble(2);
    }

    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS config(session TEXT, color TEXT, radius DOUBLE);");
        Cursor res = db.rawQuery("SELECT * FROM config");
        if(res.getCount() == 0) {
            db.execSQL("INSERT INTO config('','#FF00FF', 10);");
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        this.onCreate(db);
    }

    public void save()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE config SET session='"+ this.session + "', color='"+this.color+"', radius=" + this.radius);
    }
}
