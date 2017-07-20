package com.example.qinote;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qinote.data.NoteContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 王石旺 on 2016/12/20.
 */

public class NoteFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String LOG_TAG = NoteFilterAdapter.class.getSimpleName();


    private Cursor mCursor;
    private Context mContext;

    private static final int VIEW_TYPE_NO_PICTURE = 0;
    private static final int VIEW_TYPE_TEXT_PICTURE = 1;
    private static final int VIEW_TYPE_NO_TEXT_ONLY_ONE_PICTURE = 2;
    private static final int VIEW_TYPE_NO_TEXT_MORE_THAN_ONE_PICTURE = 3;

//    public class NoteAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        public final Button mNoteOptionButton;
//        public final TextView mNoteContentTextView;
//        public final ImageView mNotePictureImageView1;
//        public final ImageView mNotePictureImageView2;
//        public final ImageView mNotePictureImageView;
//        public final ImageView mNotePictureNoTextImageView;
//
//        public NoteAdapterViewHolder(View view) {
//            super(view);
//            mNoteOptionButton = (Button) view.findViewById(R.id.option_button);
//            mNotePictureImageView = (ImageView) view.findViewById(R.id.note_picture_imageview);
//            mNotePictureNoTextImageView=(ImageView)view.findViewById(R.id.note_picture_no_text_imageview);
//            mNoteContentTextView = (TextView) view.findViewById(R.id.content_textview);
//            mNotePictureImageView1 = (ImageView) view.findViewById(R.id.note_picture_imageview1);
//            mNotePictureImageView2 = (ImageView) view.findViewById(R.id.note_picture_imageview2);
//            view.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View view) {
//            int adapterPosition = getAdapterPosition();
//            mCursor.moveToPosition(adapterPosition);
//
//        }
//    }

    public class NoPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button mNoteOptionButton;
        public final TextView mNoteContentTextView;

        public NoPictureViewHolder(View view) {
            super(view);
            mNoteOptionButton = (Button) view.findViewById(R.id.filter_no_picture_option_button);
            mNoteContentTextView = (TextView) view.findViewById(R.id.filter_no_picture_content_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

        }
    }

    public class NoTextMoreThanOnePictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button mNoteOptionButton;
        public final ImageView mNotePictureImageView1;
        public final ImageView mNotePictureImageView2;

        public NoTextMoreThanOnePictureViewHolder(final View view) {
            super(view);
            mNoteOptionButton = (Button) view.findViewById(R.id.filter_no_text_more_than_one_picture_option_button);
            mNotePictureImageView1 = (ImageView) view.findViewById(R.id.filter_no_text_more_than_one_picture_note_picture_imageview1);
            mNotePictureImageView2 = (ImageView) view.findViewById(R.id.filter_no_text_more_than_one_picture_note_picture_imageview2);
//            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
//            Toast.makeText(mContext,"Current position is "+String.valueOf(adapterPosition),Toast.LENGTH_SHORT).show();

        }
    }

    public class NoTextOnlyOnePictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button mNoteOptionButton;
        public final ImageView mNotePictureImageView;

        public NoTextOnlyOnePictureViewHolder(View view) {
            super(view);
            mNoteOptionButton = (Button) view.findViewById(R.id.filter_no_text_only_one_picture_option_button);
            mNotePictureImageView = (ImageView) view.findViewById(R.id.filter_no_text_only_one_picture_note_picture_imageview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

        }
    }

    public class TextPictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final Button mNoteOptionButton;
        public final TextView mNoteContentTextView;
        public final ImageView mNotePictureImageView;

        public TextPictureViewHolder(View view) {
            super(view);
            mNoteOptionButton = (Button) view.findViewById(R.id.filter_text_picture_option_button);
            mNotePictureImageView = (ImageView) view.findViewById(R.id.filter_text_picture_note_picture_imageview);
            mNoteContentTextView = (TextView) view.findViewById(R.id.filter_text_picture_content_textview);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);

        }
    }

    public NoteFilterAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewGroup instanceof RecyclerView) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_NO_PICTURE: {
                    layoutId = R.layout.no_picture_list_item;
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                    view.setFocusable(true);
                    return new NoPictureViewHolder(view);
                }
                case VIEW_TYPE_TEXT_PICTURE: {
                    layoutId = R.layout.text_picture_list_item;
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                    view.setFocusable(true);
                    return new TextPictureViewHolder(view);
                }
                case VIEW_TYPE_NO_TEXT_ONLY_ONE_PICTURE: {
                    layoutId = R.layout.no_text_only_one_picture_list_item;
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                    view.setFocusable(true);
                    return new NoTextOnlyOnePictureViewHolder(view);
                }
                case VIEW_TYPE_NO_TEXT_MORE_THAN_ONE_PICTURE: {
                    layoutId = R.layout.no_text_more_than_one_picture_list_item;
                    View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                    view.setFocusable(true);
                    return new NoTextMoreThanOnePictureViewHolder(view);
                }
                default:
                    return null;
            }
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }


    public class CropTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            // Get the dimensions of the View
            int targetW = point.x * 4 / 5;
            int targetH = point.x * 2 / 5;
            Bitmap result = Bitmap.createScaledBitmap(source, targetW, targetH, true);
            if (result != source) {
                source.recycle();
            }
            return result;
        }

        @Override
        public String key() {
            return "square()";
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final String fileUriPrefix = "file://";
        mCursor.moveToPosition(position);

        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        // Get the dimensions of the View
        int targetW;
        int targetH;
        int noteTextColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT);
        String noteContentText = mCursor.getString(noteTextColumnIndex);
//        Log.d("NoteAdapter", "noteContentText= " + noteContentText);

//        String noteContentText = mCursor.getString(MainActivity.COL_NOTE_TEXT);
        int colorPrimaryColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_COLOR_PRIMARY);
        int colorPrimary = mCursor.getInt(colorPrimaryColumnIndex);

        int imageCountColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT);
        int imageCount = mCursor.getInt(imageCountColumnIndex);
//        Log.d("NoteAdapter", "imageCount= " + imageCount);
        String imagePath1 = null;
        String imagePath2 = null;
        Gson gson = new Gson();
        if (imageCount > 0) {
            int noteImagePathColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_PATH);
            String picPathArrayString = mCursor.getString(noteImagePathColumnIndex);
//            Log.d("NoteAdapter", "picPathArrayString= " + picPathArrayString);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> noteImagePath = gson.fromJson(picPathArrayString, type);
//            Log.d("NoteAdapter", "imagePath1= " + noteImagePath.get(0));
            if (imageCount >= 2) {
                imagePath1 = noteImagePath.get(0);
                imagePath2 = noteImagePath.get(1);
            } else if (imageCount == 1) {
                imagePath1 = noteImagePath.get(0);
            }
        }

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_NO_PICTURE:
                NoPictureViewHolder viewHolder = (NoPictureViewHolder) holder;
                viewHolder.mNoteContentTextView.setText(noteContentText);
                if (Build.VERSION.SDK_INT >= 16) {
                    viewHolder.mNoteOptionButton.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, colorPrimary)));
                }
                break;
            case VIEW_TYPE_TEXT_PICTURE:
                // Get the dimensions of the View
                targetW = point.x * 2 / 5;
                targetH = point.x * 2 / 5;
                TextPictureViewHolder textPictureViewHolder = (TextPictureViewHolder) holder;
                textPictureViewHolder.mNoteContentTextView.setText(noteContentText);
                Picasso.with(mContext).load(fileUriPrefix + imagePath1).resize(targetW, targetH).centerCrop().into(textPictureViewHolder.mNotePictureImageView);
                if (Build.VERSION.SDK_INT >= 16) {
                    textPictureViewHolder.mNoteOptionButton.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, colorPrimary)));
                }
                break;
            case VIEW_TYPE_NO_TEXT_ONLY_ONE_PICTURE:
                // Get the dimensions of the View
                targetW = point.x * 4 / 5;
                targetH = point.x * 2 / 5;
                Log.d(LOG_TAG, "targetW= " + targetW);
                Log.d(LOG_TAG, "targetH= " + targetH);
                NoTextOnlyOnePictureViewHolder noTextOnlyOnePictureViewHolder = (NoTextOnlyOnePictureViewHolder) holder;
                Picasso.with(mContext).load(fileUriPrefix + imagePath1).transform(new CropTransformation()).resize(targetW, targetH).into(noTextOnlyOnePictureViewHolder.mNotePictureImageView);
                if (Build.VERSION.SDK_INT >= 16) {
                    noTextOnlyOnePictureViewHolder.mNoteOptionButton.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, colorPrimary)));
                }
                break;
            case VIEW_TYPE_NO_TEXT_MORE_THAN_ONE_PICTURE:
                // Get the dimensions of the View
                targetW = point.x * 2 / 5;
                targetH = point.x * 2 / 5;
                NoTextMoreThanOnePictureViewHolder noTextMoreThanOnePictureViewHolder = (NoTextMoreThanOnePictureViewHolder) holder;
                Picasso.with(mContext).load(fileUriPrefix + imagePath1).resize(targetW, targetH).centerCrop().into(noTextMoreThanOnePictureViewHolder.mNotePictureImageView1);
                Picasso.with(mContext).load(fileUriPrefix + imagePath2).resize(targetW, targetH).centerCrop().into(noTextMoreThanOnePictureViewHolder.mNotePictureImageView2);
                if (Build.VERSION.SDK_INT >= 16) {
                    noTextMoreThanOnePictureViewHolder.mNoteOptionButton.setBackground(new ColorDrawable(ContextCompat.getColor(mContext, colorPrimary)));
                }
                break;
        }


//        if (holder instanceof NoPictureViewHolder) {
//            NoPictureViewHolder viewHolder = (NoPictureViewHolder) holder;
//            viewHolder.mNoteContentTextView.setText(noteContentText);
////            viewHolder.mNoteOptionButton.setBackgroundColor(colorPrimary);
//            if (Build.VERSION.SDK_INT>=16){
//                viewHolder.mNoteOptionButton.setBackground(new ColorDrawable(ContextCompat.getColor(mContext,colorPrimary)));
//            }
//        } else if (holder instanceof TextPictureViewHolder) {
//            TextPictureViewHolder viewHolder = (TextPictureViewHolder) holder;
//            viewHolder.mNoteContentTextView.setText(noteContentText);
//            Picasso.with(mContext).load(fileUriPrefix + imagePath1).into(viewHolder.mNotePictureImageView);
//            viewHolder.mNoteOptionButton.setBackgroundColor(colorPrimary);
//        } else if (holder instanceof NoTextOnlyOnePictureViewHolder) {
//            NoTextOnlyOnePictureViewHolder viewHolder = (NoTextOnlyOnePictureViewHolder) holder;
//            Picasso.with(mContext).load(fileUriPrefix + imagePath1).into(viewHolder.mNotePictureImageView);
//            viewHolder.mNoteOptionButton.setBackgroundColor(colorPrimary);
//        } else if (holder instanceof NoTextMoreThanOnePictureViewHolder) {
//            NoTextMoreThanOnePictureViewHolder viewHolder = (NoTextMoreThanOnePictureViewHolder) holder;
//            Picasso.with(mContext).load(fileUriPrefix + imagePath1).into(viewHolder.mNotePictureImageView1);
//            Picasso.with(mContext).load(fileUriPrefix + imagePath2).into(viewHolder.mNotePictureImageView2);
//            viewHolder.mNoteOptionButton.setBackgroundColor(colorPrimary);
//        }


//        switch (getItemViewType(position)) {
//            case VIEW_TYPE_NO_PICTURE:
//                holder.mNoteContentTextView.setText(noteContentText);
//                break;
//            case VIEW_TYPE_TEXT_PICTURE:
//                holder.mNoteContentTextView.setText(noteContentText);
//                Picasso.with(context).load(fileUriPrefix + imagePath1).centerCrop().into(holder.mNotePictureImageView);
////                holder.mNotePictureImageView.setImageBitmap(Utility.getBitmapFromPath(targetW,targetH,imagePath1));
//                break;
//            case VIEW_TYPE_NO_TEXT_ONLY_ONE_PICTURE:
//                Picasso.with(context).load(fileUriPrefix + imagePath1).centerCrop().into(holder.mNotePictureNoTextImageView);
////                holder.mNotePictureImageView.setImageBitmap(Utility.getBitmapFromPath(targetW,targetH,imagePath1));
//                break;
//            case VIEW_TYPE_NO_TEXT_MORE_THAN_ONE_PICTURE:
//                Picasso.with(context).load(fileUriPrefix + imagePath1).centerCrop().into(holder.mNotePictureImageView1);
//                Picasso.with(context).load(fileUriPrefix + imagePath2).centerCrop().into(holder.mNotePictureImageView2);
//                break;
////                holder.mNotePictureImageView1.setImageBitmap(Utility.getBitmapFromPath(targetW1,targetH1,imagePath1));
////                holder.mNotePictureImageView2.setImageBitmap(Utility.getBitmapFromPath(targetW1,targetH1,imagePath2));
//        }

    }

    @Override
    public int getItemViewType(int position) {
        mCursor.moveToPosition(position);
        int imageCountColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_IMAGE_COUNT);
        int noteTextColumnIndex = mCursor.getColumnIndex(NoteContract.NoteEntry.COLUMN_NOTE_CONTENT_TEXT);
        String noteText = mCursor.getString(noteTextColumnIndex);
        int imageCount = mCursor.getInt(imageCountColumnIndex);
        if (imageCount >= 2 && TextUtils.isEmpty(noteText)) {
            return VIEW_TYPE_NO_TEXT_MORE_THAN_ONE_PICTURE;
        }
        if (imageCount == 1 && TextUtils.isEmpty(noteText)) {
            return VIEW_TYPE_NO_TEXT_ONLY_ONE_PICTURE;
        }
        if (imageCount > 0 && !TextUtils.isEmpty(noteText)) {
            return VIEW_TYPE_TEXT_PICTURE;
        }
        if (imageCount == 0) {
            return VIEW_TYPE_NO_PICTURE;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();

    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor() {
        return mCursor;
    }
}
