package com.nekoromain.ma_bibliotheque;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_BOOKS = "books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PUBLISHER = "publisher";
    public static final String COLUMN_AUTHOR = "Author";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ISBN13 = "isbn_13";
    public static final String COLUMN_ISBN10 = "isbn_10";
    public static final String COLUMN_COVERPATH = "cover_path";

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    // Commande sql pour la création de la base de données
    private static final String DATABASE_CREATE = "create table "
            + TABLE_BOOKS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text,"
            + COLUMN_PUBLISHER + " text,"
            + COLUMN_AUTHOR + " text,"
            + COLUMN_DATE + " text,"
            + COLUMN_ISBN13 + " text,"
            + COLUMN_ISBN10 + " text,"
            + COLUMN_COVERPATH + " text"
            +");";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

}
