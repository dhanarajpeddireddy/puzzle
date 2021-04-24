package com.dana.puzzle.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dana.puzzle.Constants;
import com.dana.puzzle.R;
import com.dana.puzzle.tool.Utility;

public class ImageAdapter extends BaseAdapter {

    public void updatelist(String[] files) {
        this.files=files;
        notifyDataSetChanged();
    }

   public interface IcallBack
    {
        void onClickImageAdapterItem(String name);
    }

    private final Context mContext;
    private String[] files;
    private final IcallBack callBack;

    public ImageAdapter(String[] files,Context c,IcallBack callBack) {
        mContext = c;
        this.callBack=callBack;
        this.files=files;

    }

    public int getCount() {
        if (files!=null)
        return files.length;
        else return 0;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grid_element, null);
        }

        final ImageView imageView = convertView.findViewById(R.id.gridImageview);
        Glide.with(mContext)
                .load(Uri.parse("file:///android_asset/"+ Constants.ASSET_FOLDER_NAME+"/"+files[position]))
                .placeholder(R.drawable.spalsh2)
                .into(imageView);


       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Utility.bounce(view,null);
               callBack.onClickImageAdapterItem(files[position]);
           }
       });



        return convertView;
    }

}