package com.example.qinote;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by 王石旺 on 2016/12/26.
 */

public class NotePictureAdapter extends RecyclerView.Adapter<NotePictureAdapter.NotePictureViewHolder> {
    private static final String LOG_TAG=NotePictureAdapter.class.getSimpleName();

    private ArrayList<String> notePhotoPath;
    private String mCurrentPhotoPath;
    private Context context;

    public class NotePictureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView mNotePictureImageView;
        public final ImageButton mDeletePictureImageButton;

        public NotePictureViewHolder(View view) {
            super(view);
            mNotePictureImageView = (ImageView) view.findViewById(R.id.note_photo_imageview);
            mDeletePictureImageButton = (ImageButton) view.findViewById(R.id.delete_picture_imagebutton);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotePictureAdapter(Context context, ArrayList<String> myNotePhotoPath) {
        if (myNotePhotoPath == null) {
            throw new IllegalArgumentException("Photo path can't be null");
        }
        this.context = context;
        notePhotoPath = myNotePhotoPath;
    }

    @Override
    public NotePictureAdapter.NotePictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_grid_item, parent, false);

        return new NotePictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotePictureAdapter.NotePictureViewHolder holder, int position) {
        final String fileUriPrefix = "file://";

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        // Get the dimensions of the View
        int targetW = (point.x - 32) / 3;
        int targetH = (point.x - 32) / 3;

        if (notePhotoPath.isEmpty()) {
            return;
        } else {
            mCurrentPhotoPath = notePhotoPath.get(position);
            Picasso.with(context).load(fileUriPrefix + mCurrentPhotoPath).resize(targetW, targetH).centerCrop().into(holder.mNotePictureImageView);
        }
    }

    @Override
    public int getItemCount() {
        return notePhotoPath.size();
    }

}
