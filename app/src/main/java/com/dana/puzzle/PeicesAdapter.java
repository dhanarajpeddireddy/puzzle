package com.dana.puzzle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dana.puzzle.database.GameBeen;
import com.dana.puzzle.databinding.HistoryItemBinding;
import com.dana.puzzle.databinding.PeiceSelectItemBinding;
import com.dana.puzzle.history.HistoryAdapter;
import com.dana.puzzle.tool.Utility;

public class PeicesAdapter extends  RecyclerView.Adapter<PeicesAdapter.PeiceAdapterHolder>{


    public interface IcallBack {
        void onClickPeicesAdapterItem(int peice);

    }

    private final Context mContext;
    private final int[] files;
    private final IcallBack callBack;

    public PeicesAdapter(Context c, IcallBack callBack) {
        mContext = c;
        this.callBack = callBack;
        this.files = Constants.PUZLE_PEICES;

    }

    @NonNull
    @Override
    public PeiceAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PeiceSelectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.peice_select_item, parent, false);
        return new PeiceAdapterHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PeiceAdapterHolder holder, final int position) {

        holder.peiceSelectItemBinding.tvPeice.setText(String.valueOf(files[position]));

        holder.peiceSelectItemBinding.cvPiece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Utility.bounce(holder.peiceSelectItemBinding.ivPuzzle,null);
                if (callBack!=null)
                    callBack.onClickPeicesAdapterItem(files[position]);
            }
        });

    }

    @Override
    public int getItemCount() {
        return files.length;
    }



static class PeiceAdapterHolder extends RecyclerView.ViewHolder {
    PeiceSelectItemBinding peiceSelectItemBinding;
    PeiceAdapterHolder(@NonNull PeiceSelectItemBinding peiceSelectItemBinding) {
        super(peiceSelectItemBinding.getRoot());
        this.peiceSelectItemBinding=peiceSelectItemBinding;
    }
}


}