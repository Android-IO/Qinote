package com.example.qinote;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qinote.data.NoteContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Allows user to create a new note or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    /**
     * Identifier for the note data loader
     */
    private static final int EXISTING_NOTE_LOADER = 0;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_PICK_PICTURE_FROM_GALLERY = 2;
    String mCurrentPhotoPath;
    private Context context;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentNoteUri;

    private File image;

    private List<Integer> mSelectedTagItems;

    private ArrayList<String> mNotePhotoPath = new ArrayList<String>();

    private String sPhotoPathArrayString;
    private int sNoteImageCount;

    private int mColorPrimary = R.color.BlueGrey_500;
    private int mColorPrimaryDark = R.color.BlueGrey_700;

    private TextView mDayTextView;
    private TextView mMonthYearTextView;
    private ImageButton mFavoriteImageButton;
    private MultiAutoCompleteTextView mTagCompleteTextView;
    private ImageButton mAddTagImageButton;
    private ImageView mNotePictureImageView;
    private ImageButton mDeleteImageButton;
    private EditText mTypeNoteEditText;

    private RecyclerView mPictureRecyclerView;
    private RecyclerView.Adapter mPictureAdapter;
    private RecyclerView.LayoutManager mPictureLayoutManager;

//    private LinearLayout linearLayout = (LinearLayout) findViewById(R.id.activity_editor);

    private int mFavorite = NoteContract.NoteEntry.UNFAVORITED;
    private int mArchive = NoteContract.NoteEntry.UNARCHIVED;

    private int mNoteImageCount = 0;

    /**
     * Boolean flag that keeps track of whether the note has been edited (true) or not (false)
     */
    private boolean mNoteHasChanged = false;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNoteHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new note or editing an existing one.
        final Intent intent = getIntent();
        mCurrentNoteUri = intent.getData();

        // If the intent DOES NOT contain a note content URI, then we know that we are
        // creating a new note.
        mDayTextView = (TextView) findViewById(R.id.day_textview);
        mMonthYearTextView = (TextView) findViewById(R.id.month_and_year_textview);
        if (mCurrentNoteUri == null) {
            // This is a new pet, so initialize the day,month and year
            mDayTextView.setText(Utility.getFriendlyDayString());
            mMonthYearTextView.setText(Utility.getFriendlyMonthYearString());
        } else {
            // Otherwise this is an existing pet, so

            // Initialize a loader to read the note data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_NOTE_LOADER, null, this);
        }
        // Invalidate the options menu, so the corresponding menu option can be hidden.
        invalidateOptionsMenu();

        // Find all relevant views that we will need to read user input from
        ArrayAdapter<String> tagAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TAGS);
        mTagCompleteTextView = (MultiAutoCompleteTextView) findViewById(R.id.tag_multiAuto);
        mTagCompleteTextView.setAdapter(tagAdapter);
        mTagCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        mFavoriteImageButton = (ImageButton) findViewById(R.id.favorite_imagebutton);
        mFavoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mFavorite){
                    case NoteContract.NoteEntry.FAVORITED:
                        unfavorited();
                        mFavorite= NoteContract.NoteEntry.UNFAVORITED;
                        break;
                    case NoteContract.NoteEntry.UNFAVORITED:
                        favorited();
                        mFavorite= NoteContract.NoteEntry.FAVORITED;
                        break;
                }
                mNoteHasChanged=true;
                invalidateOptionsMenu();
            }
        });

        mAddTagImageButton = (ImageButton) findViewById(R.id.add_tag_imagebutton);
        mAddTagImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog();
            }
        });

        mNotePictureImageView = (ImageView) findViewById(R.id.note_photo_imageview);
        mDeleteImageButton = (ImageButton) findViewById(R.id.delete_picture_imagebutton);

        mTypeNoteEditText = (EditText) findViewById(R.id.type_note_edittext);

        mFavoriteImageButton.setOnTouchListener(mTouchListener);
        mTagCompleteTextView.setOnTouchListener(mTouchListener);
        mTypeNoteEditText.setOnTouchListener(mTouchListener);

        mPictureRecyclerView = (RecyclerView) findViewById(R.id.note_picture_recyclerview);
        mPictureRecyclerView.setHasFixedSize(true);
        mPictureLayoutManager = new GridLayoutManager(this, 3);
        mPictureRecyclerView.setLayoutManager(mPictureLayoutManager);
        mPictureAdapter = new NotePictureAdapter(this, mNotePhotoPath);
        mPictureRecyclerView.setAdapter(mPictureAdapter);

        // Setup the item click listener
        ItemClickSupport.addTo(mPictureRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // Create new intent to go to {@link PictureViewActivity}
                Intent intent1=new Intent(EditorActivity.this,PictureViewerActivity.class);
                intent1.putExtra("PhotoPath",sPhotoPathArrayString);
                intent1.putExtra("ImageCount",sNoteImageCount);
                intent1.putExtra("CurrentPosition",position);
                Log.d(LOG_TAG," final sPhotopathArrayString= "+sPhotoPathArrayString);
//                Log.d(LOG_TAG,"photoPathArrayString= "+photoPathArrayString);
//                intent1.setData(mCurrentNoteUri);
                startActivity(intent1);
            }
        });
    }

    private static final String[] TAGS = new String[]{
            "#购物", "#电影", "#收据", "#餐厅", "#待办", "#私人", "#食谱", "#旅行", "#Misc"
    };

    private void saveNote() {
        // Read from input fields
        String dayString = mDayTextView.getText().toString();
        String monthYearString = mMonthYearTextView.getText().toString();
        String tagString = mTagCompleteTextView.getText().toString();
        String typeNoteString = mTypeNoteEditText.getText().toString();
        mNoteImageCount = mNotePhotoPath.size();
        sNoteImageCount=mNoteImageCount;
        // Check if this is supposed to be a new pet
        // and check if all the fields in the editor are blank
        if (mCurrentNoteUri == null && TextUtils.isEmpty(typeNoteString) && mNoteImageCount == 0) {
            // Since no fields were modified, we can return early without creating a new note.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        if (mCurrentNoteUri != null && TextUtils.isEmpty(typeNoteString) && mNoteImageCount == 0) {
            // Since no fields were remained, we can return early without saving the note.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            deleteNote();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and note attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_DAY, dayString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_MONTH_YEAR, monthYearString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_FAVORITE, mFavorite);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_ARCHIVE, mArchive);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT, mNoteImageCount);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT, typeNoteString);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY, mColorPrimary);
        values.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY_DARK, mColorPrimaryDark);
        if (!TextUtils.isEmpty(tagString)) {
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_TAG, tagString);
        }
//        if (!TextUtils.isEmpty(typeNoteString)) {
//            values.put(NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT, typeNoteString);
//        }
        if (mNoteImageCount > 0) {
            Gson gson = new Gson();
            String picPathArrayString = gson.toJson(mNotePhotoPath);
            sPhotoPathArrayString=picPathArrayString;
            values.put(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_PATH, picPathArrayString);
        }

        // Determine if this is a new or existing note by checking if mCurrentNoteUri is null or not
        if (mCurrentNoteUri == null) {
            // This is a NEW note, so insert a new note into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_note_failed),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_insert_note_failed), Snackbar.LENGTH_LONG)
//                        .show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_note_successful),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_insert_note_successful), Snackbar.LENGTH_LONG)
//                        .show();
            }
        } else {
            // Otherwise this is an EXISTING note, so update the note with content URI: mCurrentNoteUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentNoteUri will already identify the correct row in the database that
            // we want to modify.


            int rowsAffected = getContentResolver().update(mCurrentNoteUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_note_failed),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_update_note_failed), Snackbar.LENGTH_LONG)
//                        .show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_note_successful),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_update_note_successful), Snackbar.LENGTH_LONG)
//                        .show();
            }
        }
    }

    /**
     * Perform the deletion of the note in the database.
     */
    private void deleteNote() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentNoteUri != null) {
            // Call the ContentResolver to delete the note at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentNoteUri
            // content URI already identifies the note that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentNoteUri, null, null);
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_note_failed),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_delete_note_failed), Snackbar.LENGTH_LONG)
//                        .show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_note_successful),
                        Toast.LENGTH_SHORT).show();
//                Snackbar.make(linearLayout, getString(R.string.editor_delete_note_successful), Snackbar.LENGTH_LONG)
//                        .show();
            }
        }
        // Close the activity
        finish();
    }

    private void favorited() {
        mFavoriteImageButton.setImageResource(R.drawable.ic_star_color_24dp);
    }

    private void unfavorited() {
        mFavoriteImageButton.setImageResource(R.drawable.ic_star_white_24dp);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = Uri.fromFile(image);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg",       /* suffix */
                storageDir     /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void pickPictureFromGalleryIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_PICTURE_FROM_GALLERY);
    }


    private String getPicturePathFromUri(Uri uri) {
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String saveBitmap(Bitmap bitmap) throws IOException {
        File pictureFile = createImageFile();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return pictureFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            mCurrentPhotoPath = image.getAbsolutePath();
//            Log.d(LOG_TAG, "mCurrentPhotoPath= " + mCurrentPhotoPath);
            mNotePhotoPath.add(mCurrentPhotoPath);
            sNoteImageCount+=1;
            Gson gson = new Gson();
            String picPathArrayString = gson.toJson(mNotePhotoPath);
            sPhotoPathArrayString=picPathArrayString;
            Log.d(LOG_TAG,"sPhotopathArrayString= "+sPhotoPathArrayString);
            mPictureAdapter.notifyDataSetChanged();
            mNoteHasChanged = true;
        } else if (requestCode == REQUEST_PICK_PICTURE_FROM_GALLERY && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            } else {
                Uri picUri = data.getData();
                mCurrentPhotoPath = getPicturePathFromUri(picUri);
                mNotePhotoPath.add(mCurrentPhotoPath);
                sNoteImageCount+=1;
                Gson gson = new Gson();
                String picPathArrayString = gson.toJson(mNotePhotoPath);
                sPhotoPathArrayString=picPathArrayString;
                mPictureAdapter.notifyDataSetChanged();
                mNoteHasChanged = true;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showAddPictureOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_picture)
                .setItems(R.array.add_picture_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                pickPictureFromGalleryIntent();
                                break;
                            case 1:
                                dispatchTakePictureIntent();
                                break;
                        }
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showPickNoteColorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_note_color, null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        setBlueGreyTheme(view, alertDialog);
        setBlueTheme(view, alertDialog);
        setDeepOrangeTheme(view, alertDialog);
        setAmberTheme(view, alertDialog);
        setGreenTheme(view, alertDialog);
        setTealTheme(view, alertDialog);
    }

    private void showAddTagDialog() {
        mSelectedTagItems = new ArrayList<Integer>();  // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        builder.setTitle(R.string.add_a_tag)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.tags, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedTagItems.add(which);
                                } else if (mSelectedTagItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedTagItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String resultString;
                        String mSelectedTagString = "";
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        for (int i = 0; i < mSelectedTagItems.size(); i++) {
                            switch (mSelectedTagItems.get(i)) {
                                case 0:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_grocery);
                                    break;
                                case 1:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_movies);
                                    break;
                                case 2:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_personal);
                                    break;
                                case 3:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_receipts);
                                    break;
                                case 4:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_recipes);
                                    break;
                                case 5:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_restaurant);
                                    break;
                                case 6:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_todo);
                                    break;
                                case 7:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_travel);
                                    break;
                                case 8:
                                    mSelectedTagString = mSelectedTagString + " " + getString(R.string.tag_misc);
                                    break;
                                default:
                                    mSelectedTagString = " ";
                            }
                        }
                        String noteTypeString = mTagCompleteTextView.getText().toString();
                        if (TextUtils.isEmpty(noteTypeString)) {
                            resultString = mSelectedTagString.trim();
                        } else {
                            resultString = noteTypeString + mSelectedTagString;
                        }
                        mTagCompleteTextView.setText(resultString);
                    }
                });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void setNoteColor(int colorPrimary, int colorPrimaryDark) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(EditorActivity.this, colorPrimary)));
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(EditorActivity.this, colorPrimaryDark));
        }
        mNoteHasChanged = true;
    }

    private void setBlueGreyTheme(View view, final AlertDialog alertDialog) {
        ImageButton blueGreyImageButton = (ImageButton) view.findViewById(R.id.blue_grey_imagebutton);
        blueGreyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.BlueGrey_500, R.color.BlueGrey_700);
                mColorPrimary = R.color.BlueGrey_500;
                mColorPrimaryDark = R.color.BlueGrey_700;
                alertDialog.dismiss();
            }
        });
    }

    private void setBlueTheme(View view, final AlertDialog alertDialog) {
        ImageButton blueImageButton = (ImageButton) view.findViewById(R.id.blue_imagebutton);
        blueImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.Blue_500, R.color.Blue_700);
                mColorPrimary = R.color.Blue_500;
                mColorPrimaryDark = R.color.Blue_700;
                alertDialog.dismiss();
            }
        });
    }

    private void setDeepOrangeTheme(View view, final AlertDialog alertDialog) {
        ImageButton deepOrangeImageButton = (ImageButton) view.findViewById(R.id.deep_orange_imagebutton);
        deepOrangeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.DeepOrange_500, R.color.DeepOrange_700);
                mColorPrimary = R.color.DeepOrange_500;
                mColorPrimaryDark = R.color.DeepOrange_700;
                alertDialog.dismiss();
            }
        });
    }

    private void setAmberTheme(View view, final AlertDialog alertDialog) {
        ImageButton amberImageButton = (ImageButton) view.findViewById(R.id.amber_imagebutton);
        amberImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.Amber_500, R.color.Amber_700);
                mColorPrimary = R.color.Amber_500;
                mColorPrimaryDark = R.color.Amber_700;
                alertDialog.dismiss();
            }
        });
    }

    private void setGreenTheme(View view, final AlertDialog alertDialog) {
        ImageButton greenImageButton = (ImageButton) view.findViewById(R.id.green_imagebutton);
        greenImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.Green_500, R.color.Green_700);
                mColorPrimary = R.color.Green_500;
                mColorPrimaryDark = R.color.Green_700;
                alertDialog.dismiss();
            }
        });
    }

    private void setTealTheme(View view, final AlertDialog alertDialog) {
        ImageButton tealImageButton = (ImageButton) view.findViewById(R.id.teal_imagebutton);
        tealImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoteColor(R.color.Teal_500, R.color.Teal_700);
                mColorPrimary = R.color.Teal_500;
                mColorPrimaryDark = R.color.Teal_700;
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        if the note is favorited, hide the "加入收藏夹" menu item.
        if (mFavorite== NoteContract.NoteEntry.FAVORITED){
            MenuItem menuItem=menu.findItem(R.id.action_mark_as_favorite);
            menuItem.setVisible(false);
        }
//        if the note is unfavorited, hide the "移出收藏夹" menu item.
        if (mFavorite== NoteContract.NoteEntry.UNFAVORITED){
            MenuItem menuItem=menu.findItem(R.id.action_remove_from_favorite);
            menuItem.setVisible(false);
        }
//        if the note is archived, hide the "归档" menu item.
        if (mArchive== NoteContract.NoteEntry.ARCHIVED){
            MenuItem menuItem=menu.findItem(R.id.action_archive);
            menuItem.setVisible(false);
        }
//        if the note is unarchived, hide the "取消归档" menu item.
        if (mArchive== NoteContract.NoteEntry.UNARCHIVED){
            MenuItem menuItem=menu.findItem(R.id.action_unarchive);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_image:
                showAddPictureOptionsDialog();
                return true;
            case R.id.action_color:
                showPickNoteColorDialog();
                return true;
            case R.id.action_todo:

                return true;
            case R.id.action_share:

                return true;
            case R.id.action_mark_as_favorite:
                favorited();
                mFavorite= NoteContract.NoteEntry.FAVORITED;
                mNoteHasChanged=true;
                invalidateOptionsMenu();
                return true;
            case R.id.action_remove_from_favorite:
                unfavorited();
                mFavorite= NoteContract.NoteEntry.UNFAVORITED;
                mNoteHasChanged=true;
                invalidateOptionsMenu();
                return true;
            case R.id.action_archive:
                mArchive= NoteContract.NoteEntry.ARCHIVED;
                mNoteHasChanged=true;
                saveNote();
                finish();
                return true;
            case R.id.action_unarchive:
                mArchive= NoteContract.NoteEntry.UNARCHIVED;
                mNoteHasChanged=true;
                return true;
            case R.id.action_delete:
                deleteNote();
                return true;
            case R.id.action_settings:

                return true;
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!mNoteHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes,save notes to database and exit activity
                saveNote();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mNoteHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, save notes and exit activity
        saveNote();
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the note table
        String[] projection = {
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

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentNoteUri,        // Query the content URI for the current pet
                projection,               // Columns to include in the resulting Cursor
                null,                    // No selection clause
                null,                    // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of note attributes that we're interested in
            int dayColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_DAY);
            int monthYearColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_MONTH_YEAR);
            int favoriteColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_FAVORITE);
            int archiveColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_ARCHIVE);
            int tagColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_TAG);
            int imageCountColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT);
            int imagePathColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_PATH);
            int noteTextColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT);
            int colorPrimaryColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY);
            int colorPrimaryDarkColumnIndex = cursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY_DARK);

            // Extract out the value from the Cursor for the given column index
            String day = cursor.getString(dayColumnIndex);
            String monthYear = cursor.getString(monthYearColumnIndex);
            int favorite = cursor.getInt(favoriteColumnIndex);
            int archive = cursor.getInt(archiveColumnIndex);
            String tag = cursor.getString(tagColumnIndex);
            int imageCount = cursor.getInt(imageCountColumnIndex);
            String picPathArrayString = cursor.getString(imagePathColumnIndex);
            String noteText = cursor.getString(noteTextColumnIndex);
            int colorPrimary = cursor.getInt(colorPrimaryColumnIndex);
            int colorPrimaryDark = cursor.getInt(colorPrimaryDarkColumnIndex);

            // Update the views on the screen with the values from the database
            mDayTextView.setText(day);
            mMonthYearTextView.setText(monthYear);
            if (!TextUtils.isEmpty(tag)) {
                mTagCompleteTextView.setText(tag);
            }
            if (!TextUtils.isEmpty(noteText)) {
                mTypeNoteEditText.setText(noteText);
            }

            switch (favorite) {
                case NoteContract.NoteEntry.FAVORITED:
                    favorited();
                    mFavorite= NoteContract.NoteEntry.FAVORITED;
                    break;
                case NoteContract.NoteEntry.UNFAVORITED:
                    unfavorited();
                    mFavorite= NoteContract.NoteEntry.UNFAVORITED;
                    break;
            }
            switch (archive){
                case NoteContract.NoteEntry.ARCHIVED:
                    mArchive= NoteContract.NoteEntry.ARCHIVED;

                    break;
                case NoteContract.NoteEntry.UNARCHIVED:
                    mArchive= NoteContract.NoteEntry.UNARCHIVED;

                    break;
            }
            Gson gson = new Gson();
            if (imageCount > 0) {
                sPhotoPathArrayString=picPathArrayString;
                sNoteImageCount=imageCount;
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> noteImagePath = gson.fromJson(picPathArrayString, type);
                mNotePhotoPath = noteImagePath;
                mPictureRecyclerView = (RecyclerView) findViewById(R.id.note_picture_recyclerview);
                mPictureRecyclerView.setHasFixedSize(true);
                mPictureLayoutManager = new GridLayoutManager(this, 3);
                mPictureRecyclerView.setLayoutManager(mPictureLayoutManager);
                mPictureAdapter = new NotePictureAdapter(this, noteImagePath);
                mPictureRecyclerView.setAdapter(mPictureAdapter);
            }
            mColorPrimary = colorPrimary;
            mColorPrimaryDark = colorPrimaryDark;
            setNoteColor(mColorPrimary, mColorPrimaryDark);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
//        mDayTextView.setText(Utility.getFriendlyDayString());
//        mMonthYearTextView.setText(Utility.getFriendlyMonthYearString());
        mTagCompleteTextView.setText("");
        mTypeNoteEditText.setText("");
    }
}
