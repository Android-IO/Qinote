package com.example.qinote;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;

import com.example.qinote.data.NoteContract;

public class NoteFilterActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private NoteFilterAdapter mNoteFilterAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    // For the note view we're showing only a small subset of the stored data.
// Specify the columns we need.
    private static final String[] NOTE_COLUMNS = {
            NoteContract.NoteEntry._ID,
            NoteContract.NoteEntry.COLUMN_NOTE_DAY,
            NoteContract.NoteEntry.COLUMN_NOTE_MONTH_YEAR,
            NoteContract.NoteEntry.COLUMN_NOTE_FAVORITE,
            NoteContract.NoteEntry.COLUMN_NOTE_ARCHIVE,
            NoteContract.NoteEntry.COLUMN_NOTE_TAG,
            NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT,
            NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_PATH,
            NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT,
            NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY,
            NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY_DARK
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_filter);

        mRecyclerView = (RecyclerView) findViewById(R.id.note_filter_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mNoteFilterAdapter = new NoteFilterAdapter(this);
        mRecyclerView.setAdapter(mNoteFilterAdapter);

        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note_filter, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);    // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Defines a string to contain the selection clause
        String mSelectionClause = null;
        /*
        * This defines a one-element String array to contain the selection argument.
         */
        String[] mSelectionArgs = null;
        // Constructs a selection clause that matches the word that the user entered.
//        mSelectionClause = NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT + " LIKE ?";

        // Moves the user's input string to the selection arguments.
//        mSelectionArgs[0] = "%" + mCurFilter + "%";

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,               // Parent activity context
                NoteContract.NoteEntry.CONTENT_URI, // Provider content URI to query
                NOTE_COLUMNS,                       // Columns to include in the resulting Cursor
                mSelectionClause,                                // No selection clause
                mSelectionArgs,                                // No selection arguments
                null);                               // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        mNoteFilterAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        mNoteFilterAdapter.swapCursor(null);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Don't care about this.
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
}
