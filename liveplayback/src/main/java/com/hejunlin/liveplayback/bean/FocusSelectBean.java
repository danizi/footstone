package com.hejunlin.liveplayback.bean;

import android.view.View;

/**
 * 焦点实体bean
 */
public class FocusSelectBean {
    private int index;
    private View view;

    public FocusSelectBean() {
    }

    public FocusSelectBean(int index, View view) {
        this.index = index;
        this.view = view;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
