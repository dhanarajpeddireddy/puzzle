package com.dana.puzzle.history;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dana.puzzle.PuzzleApplication;
import com.dana.puzzle.R;
import com.dana.puzzle.database.GameBeen;
import com.dana.puzzle.databinding.HistoryItemBinding;
import com.dana.puzzle.game.Constants;

import java.util.ArrayList;
import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryAdapterHolder> {


    private List<GameBeen> datalist;


    public HistoryAdapter(ArrayList<GameBeen> datalist )
    {
        this.datalist=datalist;

    }


    public void updateList(List<GameBeen> datalist)
    {
        this.datalist = datalist;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public HistoryAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        HistoryItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.history_item, parent, false);
        return new HistoryAdapterHolder(binding);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HistoryAdapterHolder holder, final int position) {
        final GameBeen gameBeen=datalist.get(position);

        holder.historyItemBinding.tvDate.setText(gameBeen.date);
        holder.historyItemBinding.tvPieces.setText(gameBeen.numberOfPieces
                +" "+PuzzleApplication.getContext().getString(R.string.peices));

        if (gameBeen.getHours()!=0)
            holder.historyItemBinding.tvTime.append(gameBeen.getHours()+" Hours ");

        if (gameBeen.getMinutes()!=0)
            holder.historyItemBinding.tvTime.append(gameBeen.getMinutes()+" Minutes ");

        if (gameBeen.getSeconds()!=0)
            holder.historyItemBinding.tvTime.append(gameBeen.getSeconds()+" Seconds ");

        String path = null;
        if (gameBeen.getAssetName() != null) {
            path="file:///android_asset/"+ Constants.ASSET_FOLDER_NAME+"/"+ gameBeen.getAssetName();

        }  else if (gameBeen.getPhotoUri() != null) {
            path= gameBeen.getPhotoUri();
        }
        if (path!=null)
        {

            Log.e("setImage","in : "+path);
            Glide.with(PuzzleApplication.getContext())
                    .load(Uri.parse(path))
                    .placeholder(R.drawable.spalsh2)
                    .into(holder.historyItemBinding.ivImage);
        }

    }

    @Override
    public int getItemCount() {
        if(datalist != null)
        return datalist.size();
        else
        return 0;
    }

    static class HistoryAdapterHolder extends RecyclerView.ViewHolder {
        HistoryItemBinding historyItemBinding;
        HistoryAdapterHolder(@NonNull HistoryItemBinding historyItemBinding) {
            super(historyItemBinding.getRoot());
            this.historyItemBinding=historyItemBinding;
        }
    }

    public void clear(){
        this.datalist = null;
    }
}
