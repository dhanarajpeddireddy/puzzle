package com.dana.puzzle.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dana.puzzle.Constants;
import com.dana.puzzle.PuzzleApplication;
import com.dana.puzzle.R;
import com.dana.puzzle.database.GameBeen;
import com.dana.puzzle.databinding.GridElementBinding;
import com.dana.puzzle.databinding.HistoryItemBinding;
import com.dana.puzzle.tool.Utility;

import java.util.ArrayList;
import java.util.List;


public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImagesAdapterHolder> {




    private final Context mContext;
    private String[] files;
    private final ImageAdapter.IcallBack callBack;


    public ImagesAdapter(String[] files, Context c, ImageAdapter.IcallBack callBack) {
        mContext = c;
        this.callBack=callBack;
        this.files=files;

    }

    public void updatelist(String[] files) {
        this.files=files;
        notifyDataSetChanged();
    }




    @NonNull
    @Override
    public ImagesAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        GridElementBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.grid_element, parent, false);
        return new ImagesAdapterHolder(binding);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImagesAdapterHolder holder, final int position) {


        Glide.with(mContext)
                .load(Uri.parse("file:///android_asset/"+ Constants.ASSET_FOLDER_NAME+"/"+files[position]))
                .placeholder(R.drawable.spalsh2)
                .into(holder.gridElementBinding.gridImageview);


        holder.gridElementBinding.gridImageview.setOnClickListener(view -> {
            Utility.bounce(view,null);
            callBack.onClickImageAdapterItem(files[position]);
        });
    }

    @Override
    public int getItemCount() {
        if(files != null)
        return files.length;
        else
        return 0;
    }

    static class ImagesAdapterHolder extends RecyclerView.ViewHolder {
        GridElementBinding gridElementBinding;
        ImagesAdapterHolder(@NonNull GridElementBinding gridElementBinding) {
            super(gridElementBinding.getRoot());
            this.gridElementBinding=gridElementBinding;
        }
    }

    public void clear(){
        this.files = null;
    }
}
