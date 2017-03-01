package com.example.qinote.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 王石旺 on 2016/12/19.
 */

public class NoteDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "qinote.db";
    private static int DATABASE_VERSION = 1;

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_NOTES_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " ("
                + NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_DAY + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_NOTE_MONTH_YEAR + " TEXT NOT NULL, "
                + NoteContract.NoteEntry.COLUMN_NOTE_FAVORITE + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_ARCHIVE + " INTEGER NOT NULL DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_TAG + " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT + " INTEGER DEFAULT 0, "
                + NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_PATH + " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT + " TEXT, "
                + NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY + " INTEGER, "
                + NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY_DARK + " INTEGER);";
        sqLiteDatabase.execSQL(SQL_CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
