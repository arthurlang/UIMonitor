package com.lj.framemonitor.adapter;



import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Description
 * Created by langjian on 2017/3/2.
 * Version
 */

public abstract class AbsRecyclerViewAdapter<A,H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    protected List<A> mList;

    public AbsRecyclerViewAdapter(List<A> list) {
        mList = list;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return onCreateItemViewHolder(viewGroup, i);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = getItemViewType(i);
        A data = mList.get(i);
        onBindItemViewHolder((H) viewHolder, data, i, viewType);
    }

    protected abstract H onCreateItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindItemViewHolder(H holder, A t, int position, int viewType);
}
