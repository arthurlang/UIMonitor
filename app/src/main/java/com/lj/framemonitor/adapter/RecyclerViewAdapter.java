package com.lj.framemonitor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lj.framemonitor.R;

import java.util.List;

/**
 * Description
 * Created by langjian on 2016/11/5.
 * Version
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MagicCategoryViewHolder> {

    private List<String> mCategorys;
    private final Context mContext;

    public RecyclerViewAdapter(Context context, List category) {
        mContext  = context;
        mCategorys = category;
    }

    @Override
    public MagicCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_list_item,parent,false);
        MagicCategoryViewHolder holder = new MagicCategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MagicCategoryViewHolder holder, int position) {
        holder.textView.setText(mCategorys.get(position));

        //todo
//        try {
//            Thread.sleep(20);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        //todo
    }

    @Override
    public int getItemCount() {
        return mCategorys == null ? 0 : mCategorys.size();
    }

    class MagicCategoryViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        public MagicCategoryViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}
