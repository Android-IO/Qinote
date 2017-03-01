package com.example.qinote.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by 王石旺 on 2016/12/18.
 */

public class NoteContract {
    private NoteContract() {
    }

    public static final String CONTENT_AUTHORITY="com.example.qinote";
    public static final Uri BASE_CONTENT_URI=Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_NOTES="notes";

    public static final class NoteEntry implements BaseColumns{
        public static final Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NOTES);

        public static final String CONTENT_LIST_TYPE=
                ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_NOTES;
        public static final String CONTENT_ITEM_TYPE=
                ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_NOTES;

        public final static String TABLE_NAME="notes";

        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_NOTE_DAY="day";
        public final static String COLUMN_NOTE_MONTH_YEAR="monthYear";
        public final static String COLUMN_NOTE_FAVORITE="favorite";
        public final static String COLUMN_NOTE_ARCHIVE="archive";
        public final static String COLUMN_NOTE_TAG="tag";
        public final static String COLUMN_NOTE_IMAGE_COUNT="imageCount";
        public final static String COLUMN_NOTE_IMAGE_PATH="imagePath";
        public final static String COLUMN_NOTE_CONTENT_TEXT="text";
        public final static String COLUMN_NOTE_COLOR_PRIMARY="colorPrimary";
        public final static String COLUMN_NOTE_COLOR_PRIMARY_DARK="colorPrimaryDark";

        public static final int UNFAVORITED=0;
        public static final int FAVORITED=1;

        public static final int UNARCHIVED=0;
        public static final int ARCHIVED=1;

        public static final int IMAGE_COUNT=0;
    }
}
