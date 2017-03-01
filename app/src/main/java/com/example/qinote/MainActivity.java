package com.example.qinote;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.qinote.data.NoteContract;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the note data loader */
    private static final int NOTE_LOADER=0;

    private RecyclerView mRecyclerView;
    private NoteAdapter mNoteAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // For the forecast view we're showing only a small subset of the stored data.
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
//    // These indices are tied to NOTE_COLUMNS.  If NOTE_COLUMNS changes, these
//    // must change.
//    static final int COL_NOTE_ID = 0;
//    static final int COL_NOTE_DAY = 1;
//    static final int COL_NOTE_MONTH_YEAR = 2;
//    static final int COL_NOTE_FAVORITE = 3;
//    static final int COL_NOTE_ARCHIVE = 4;
//    static final int COL_NOTE_TAG = 5;
//    static final int COL_NOTE_IMAGE_COUNT = 6;
//    static final int COL_NOTE_IMAGE_PATH = 7;
//    static final int COL_NOTE_TEXT = 8;
//    static final int COL_NOTE_COLOR_PRIMARY = 9;
//    static final int COL_NOTE_COLOR_PRIMARY_DARK = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.note_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mNoteAdapter = new NoteAdapter(this);
        mRecyclerView.setAdapter(mNoteAdapter);

        // Setup the item click listener
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Toast.makeText(MainActivity.this,"Current position is "+String.valueOf(position), Toast.LENGTH_SHORT).show();
                // Create new intent to go to {@link EditorActivity}
                Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                Cursor cursor=mNoteAdapter.getCursor();
                cursor.moveToPosition(position);
                int idColumnIndex=cursor.getColumnIndex(NoteContract.NoteEntry._ID);
                long id=cursor.getLong(idColumnIndex);
                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link NoteEntry#CONTENT_URI}.
                // For example, the URI would be "content://com.example.android.notes/notes/2"
                // if the note with ID 2 was clicked on.
//                int id=v.getId();
                Uri currentNoteUri= ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI,id);
                // Set the URI on the data field of the intent
                intent.setData(currentNoteUri);
                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Kick off the loader
        getLoaderManager().initLoader(NOTE_LOADER,null,this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            // Handle the camera action
        } else if (id == R.id.nav_archive) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_exit_to_app) {
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,               // Parent activity context
                NoteContract.NoteEntry.CONTENT_URI, // Provider content URI to query
                NOTE_COLUMNS,                       // Columns to include in the resulting Cursor
                null,                                // No selection clause
                null,                                // No selection arguments
                null);                               // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link NoteAdapter} with this new cursor containing updated pet data
        mNoteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mNoteAdapter.swapCursor(null);
    }
}
