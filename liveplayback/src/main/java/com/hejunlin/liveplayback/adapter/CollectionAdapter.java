package com.hejunlin.liveplayback.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hejunlin.liveplayback.R;
import com.hejunlin.liveplayback.bean.ProgramBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 节目集合ViewHolder
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    private List<ProgramBean.ItemsBean> data = new ArrayList<>();

    private OnBindListener bindListener;

    public CollectionAdapter(List<ProgramBean.ItemsBean> data) {
        this.data.addAll(data);
    }

    public List<ProgramBean.ItemsBean> getData() {
        return data;
    }

    public OnBindListener getBindListener() {
        return bindListener;
    }

    public void setBindListener(OnBindListener bindListener) {
        this.bindListener = bindListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_menu_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.getTv().setText(data.get(i).getName());
        if (null != bindListener) {
            bindListener.onBind(viewHolder.itemView, i);
        }
    }


    @Override
    public int getItemCount() {
        if (data.isEmpty())
            return 0;
        return data.size();
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv_menu_title);
        }

        TextView getTv() {
            return tv;
        }
    }

    public interface OnBindListener {
        void onBind(View view, int i);
    }
}
