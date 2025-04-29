// DatabaseHelper.java
package com.example.praktikum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "builds.db";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom
    private static final String TABLE_BUILDS = "builds";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE_RES = "image_res";
    private static final String COLUMN_IS_AVAILABLE = "is_available";

    // Query pembuatan tabel
    private static final String CREATE_TABLE_BUILDS =
            "CREATE TABLE " + TABLE_BUILDS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_IMAGE_RES + " INTEGER,"
                    + COLUMN_IS_AVAILABLE + " INTEGER"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUILDS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDS);
        onCreate(db);
    }

    // CRUD Operations

    // Tambah build item
    public long addBuildItem(BuildItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_IMAGE_RES, item.getImageRes());
        values.put(COLUMN_IS_AVAILABLE, item.isAvailable() ? 1 : 0);

        long id = db.insert(TABLE_BUILDS, null, values);
        db.close();
        return id;
    }

    // Dapatkan semua build items
    public List<BuildItem> getAllBuildItems() {
        List<BuildItem> items = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BUILDS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BuildItem item = new BuildItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    // Dapatkan build items berdasarkan ketersediaan
    public List<BuildItem> getBuildItemsByAvailability(boolean isAvailable) {
        List<BuildItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BUILDS,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_IMAGE_RES, COLUMN_IS_AVAILABLE},
                COLUMN_IS_AVAILABLE + "=?",
                new String[]{isAvailable ? "1" : "0"},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                BuildItem item = new BuildItem(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_AVAILABLE)) == 1
                );
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    // Update status ketersediaan build item
    public int updateBuildItemAvailability(BuildItem item, boolean isAvailable) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_AVAILABLE, isAvailable ? 1 : 0);

        return db.update(TABLE_BUILDS, values,
                COLUMN_NAME + "=? AND " + COLUMN_IMAGE_RES + "=?",
                new String[]{item.getName(), String.valueOf(item.getImageRes())});
    }

    // Hapus build item
    public void deleteBuildItem(BuildItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUILDS,
                COLUMN_NAME + "=? AND " + COLUMN_IMAGE_RES + "=?",
                new String[]{item.getName(), String.valueOf(item.getImageRes())});
        db.close();
    }
}
