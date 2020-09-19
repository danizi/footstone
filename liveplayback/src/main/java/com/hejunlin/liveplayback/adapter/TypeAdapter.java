/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 *
 * Github:https://github.com/hejunlin2013/LivePlayback
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hejunlin.liveplayback.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hejunlin.liveplayback.R;
import com.hejunlin.liveplayback.bean.ProgramBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejunlin on 2015/10/28.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.ViewHolder> {

    private List<ProgramBean> datas = new ArrayList<ProgramBean>();
    private RvItemFocusChangeListener rvItemFocusChangeListener;
    private Context mContext;
    private int id;
    private View.OnFocusChangeListener mOnFocusChangeListener;
    private OnBindListener onBindListener;
    private static final String TAG = TypeAdapter.class.getSimpleName();
    private OnItemOnclick onItemOnclick;

    public OnItemOnclick getOnItemOnclick() {
        return onItemOnclick;
    }

    public void setOnItemOnclick(OnItemOnclick onItemOnclick) {
        this.onItemOnclick = onItemOnclick;
    }

    public List<ProgramBean> getDatas() {
        return datas;
    }

    /**
     * 焦点监听
     */
    public interface RvItemFocusChangeListener {
        void onRvItemFocusChange(View v, boolean hasFocus, int pos);
    }

    public interface OnBindListener {
        void onBind(View view, int i);
    }

    public TypeAdapter(Context context) {
        super();
        mContext = context;
    }

    public TypeAdapter(Context context, int id) {
        super();
        mContext = context;
        this.id = id;
    }

    public TypeAdapter(Context context, int id, View.OnFocusChangeListener onFocusChangeListener) {
        super();
        mContext = context;
        this.id = id;
        this.mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setOnBindListener(OnBindListener onBindListener) {
        this.onBindListener = onBindListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        int resId = R.layout.detail_menu_item_parent;
        if (this.id > 0) {
            resId = this.id;
        }
        View view = LayoutInflater.from(mContext).inflate(resId, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("TAG", "hasFocus:" + hasFocus + "pos:" + i);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        if (datas.size() == 0) {
            Log.d(TAG, "mDataset has no data!");
            return;
        }
        viewHolder.mTextView.setText(datas.get(i).getTypeName());
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != onItemOnclick) {
                    onItemOnclick.onItemClick(v,i/*,mUrlList[i]*/);
                }
                //LiveActivity.activityStart(mContext, mUrlList[i]);
            }
        });

        if (onBindListener != null) {
            onBindListener.onBind(viewHolder.itemView, i);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_menu_title);
        }
    }

    public interface OnItemOnclick{
        void onItemClick(View v, int pos/*, String url*/);
    }

}
