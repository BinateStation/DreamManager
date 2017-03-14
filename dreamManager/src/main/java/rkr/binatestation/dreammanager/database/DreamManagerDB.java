package rkr.binatestation.dreammanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by RKR on 06-10-2016.
 * DreamManagerDB.
 */

class DreamManagerDB extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DreamManager.db";
    private static final int DATABASE_VERSION = 1;

    DreamManagerDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DreamManagerContract.DreamListTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DreamManagerContract.DreamListTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do the same thing as upgrading...
        onUpgrade(db, oldVersion, newVersion);
    }
}
