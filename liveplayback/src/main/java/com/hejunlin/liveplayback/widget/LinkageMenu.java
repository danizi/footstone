package com.hejunlin.liveplayback.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hejunlin.liveplayback.adapter.CollectionAdapter;
import com.hejunlin.liveplayback.R;
import com.hejunlin.liveplayback.adapter.TypeAdapter;
import com.hejunlin.liveplayback.bean.FocusSelectBean;
import com.hejunlin.liveplayback.bean.ProgramBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 多级联通菜单
 * 1 填充数据方法
 * 2 点击一级菜单
 * 3 点击二级菜单
 * 4 修改item的样式
 * 5 滑动事件
 * 6 选中颜色
 */
public class LinkageMenu extends FrameLayout {
    /**
     * 日志TAG
     */
    private final String TAG = "LinkageMenu";

    /**
     * 多级菜单点击监听
     */
    private OnClickMenuListener onClickMenuListener;

    private View linkageMenu;
    /**
     * 节目类型
     */
    private RecyclerView rvProgramType;
    /**
     * 节目集合
     */
    private RecyclerView rvProgramCollection;
    /**
     * 节目时刻
     */
    private RecyclerView rvProgramEpg;
    private View rightArrow;
    private TypeAdapter typeAdapter;
    private CollectionAdapter collectionApt;

    private FocusSelectBean rvProgramTypeFocusSelectIndex;
    /**
     * 记录节目类型一个焦点
     */
    private View old = null;

    /**
     * 父容器
     */
    private View mainView;

    /**
     * 菜单是否显示
     */
    private boolean isShow = true;

    public LinkageMenu(Context context) {
        super(context);
        ini();
    }

    public LinkageMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        ini();
    }

    public LinkageMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ini();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinkageMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        ini();
    }

    private void ini() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_linkage_menu, null, false);
        addView(view);
        linkageMenu = LinkageMenu.this;

        rvProgramTypeFocusSelectIndex = new FocusSelectBean();

        // findView
        rvProgramType = view.findViewById(R.id.rv_program_type);
        rvProgramCollection = view.findViewById(R.id.rv_program_collection);
        rvProgramEpg = view.findViewById(R.id.rv_program_epg);
        rightArrow = view.findViewById(R.id.right_arrow);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProgramCollection.setLayoutManager(layoutManager);

        layoutManager = new GridLayoutManager(getContext(), 1);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvProgramType.setLayoutManager(layoutManager);

        // 设置监听
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator objectAnimation;
                if (isShow) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });

        //一级菜单填充数据
        typeAdapter = new TypeAdapter(getContext(), R.layout.detail_menu_item);
        rvProgramType.setAdapter(typeAdapter);
        rvProgramType.setFocusable(true);
        rvProgramType.setFocusableInTouchMode(true);
        rvProgramType.scrollToPosition(0);
        typeAdapter.setOnBindListener(new TypeOnBindListener());
        typeAdapter.setOnItemOnclick(new TypeAdapter.OnItemOnclick() {
            @Override
            public void onItemClick(View v, int pos/*, String url*/) {
                String url = "";
                //initVideo(url);
            }
        });
    }

    public void closeMenu(/*View linkageMenu*/) {
        if (linkageMenu == null) {
            Log.e(TAG, "closeMenu fail");
            return;
        }
        if(!isShow)
            return;
        ObjectAnimator objectAnimation;
        objectAnimation = ObjectAnimator.ofFloat(linkageMenu, "translationX", 0f, -linkageMenu.getMeasuredWidth());
        objectAnimation.start();
        isShow = false;
    }

    public void openMenu(/*View linkageMenu*/) {
        if (linkageMenu == null) {
            Log.e(TAG, "openMenu fail");
            return;
        }
        if(isShow)
            return;
        ObjectAnimator objectAnimation;
        objectAnimation = ObjectAnimator.ofFloat(linkageMenu, "translationX", -linkageMenu.getMeasuredWidth(), 0f);
        objectAnimation.start();
        isShow = true;
    }

    /**
     * 设置菜单点击事件
     *
     * @param listener l
     */
    public void setMenuListener(OnClickMenuListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener is null");
        }
        this.onClickMenuListener = listener;
    }

    public void setMainView(View mainView) {
        this.mainView = mainView;
    }

    public void setData(List<ProgramBean> data) {
        if (typeAdapter != null && typeAdapter.getDatas() != null && null != data) {
            typeAdapter.getDatas().addAll(data);
            typeAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "setData fail");
        }
    }

    public List<ProgramBean> getData() {
        List<ProgramBean> datas = new ArrayList<>();
        if (typeAdapter != null) {
            datas.addAll(typeAdapter.getDatas());
            return datas;
        }
        return datas;
    }

    /**
     * 节目类型焦点监听
     */
    class TypeOnBindListener implements TypeAdapter.OnBindListener {

        @Override
        public void onBind(View view, final int parentIndex) {
            // 一级菜单第一个item获取焦点
            if (parentIndex == 0) {
                view.requestFocus();
                // 获取节目类型名称
                String programTypeName = "";
                if (typeAdapter != null && typeAdapter.getDatas() != null &&
                        typeAdapter.getDatas().size() > parentIndex) {
                    programTypeName = typeAdapter.getDatas().get(parentIndex).getTypeName();
                } else {
                    Log.e(TAG, "collectionApt is failure");
                }

                // 设置节目集合RecyclerView
                setProgramCollection("programTypeName", parentIndex);
                // 保存当前选中节目位置和选中UI状态
                saveFocusIndexAndSelectState(view, "programTypeName", parentIndex);
            }
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // 没有获取焦点，就不加载节目集合数据。
                    if (!hasFocus)
                        return;

                    // 获取节目类型名称
                    String programTypeName = "";
                    if (typeAdapter != null && typeAdapter.getDatas() != null &&
                            typeAdapter.getDatas().size() > parentIndex) {
                        programTypeName = typeAdapter.getDatas().get(parentIndex).getTypeName();
                    } else {
                        Log.e(TAG, "collectionApt is failure");
                    }

                    // 设置节目集合RecyclerView
                    setProgramCollection(programTypeName, parentIndex);
                    // 保存当前选中节目位置和选中UI状态
                    saveFocusIndexAndSelectState(v, programTypeName, parentIndex);

                    // 点击事件设置
                    if (onClickMenuListener != null) {
                        onClickMenuListener.onParentClick(parentIndex);
                    }
                }
            });
        }

        private void saveFocusIndexAndSelectState(View v, String programTypeName, int i) {
            rvProgramTypeFocusSelectIndex.setIndex(i);
            rvProgramTypeFocusSelectIndex.setView(v);
            if (null != old) {
                old.setBackgroundResource(R.drawable.tran);
            }
            v.setBackgroundResource(R.drawable.shp);
            old = v;
            Log.d(TAG, "节目类型焦点选中 -> " + programTypeName + " pos : " + i);
        }

        private void setProgramCollection(String programTypeName, int parentIndex) {
            if (typeAdapter == null ||
                    typeAdapter.getDatas() == null ||
                    typeAdapter.getDatas().size() < parentIndex ||
                    typeAdapter.getDatas().get(parentIndex).getItems() == null) {
                Log.e(TAG, "setProgramCollection fail");
                return;
            }

            List<ProgramBean.ItemsBean> data = typeAdapter.getDatas().get(parentIndex).getItems();
            if (collectionApt == null) {
                collectionApt = new CollectionAdapter(data);
                rvProgramCollection.setAdapter(collectionApt);
                collectionApt.setBindListener(new CollectionOnBindListener(parentIndex));
                Log.i(TAG, "first setProgramCollection");
                return;
            }
            collectionApt.getData().clear();
            collectionApt.getData().addAll(data);
            collectionApt.notifyDataSetChanged();
            Log.i(TAG, "not first setProgramCollection");
        }
    }

    /**
     * 节目集合焦点监听
     */
    class CollectionOnBindListener implements CollectionAdapter.OnBindListener {

        private final int parentIndex;

        public CollectionOnBindListener(int parentIndex) {
            this.parentIndex = parentIndex;
        }

        @Override
        public void onBind(View view, final int i) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickMenuListener != null) {
                        //String url = collectionApt.getData().get(i).getUrl();
                        onClickMenuListener.onChildClick(parentIndex, i);
                    }
                }
            });
            view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus)
                        return;
                    String programTypeName = "";

                    // 获取节目类型名称
                    if (collectionApt != null && collectionApt.getData() != null &&
                            collectionApt.getData().size() > i) {
                        programTypeName = collectionApt.getData().get(i).getName();
                    } else {
                        Log.e(TAG, "collectionApt is failure");
                    }

                    // 二级菜单item获得焦点
                    if (onClickMenuListener != null) {
                        //String url = collectionApt.getData().get(i).getUrl();
                        //onClickMenuListener.onChildClick(parentIndex, i);
                        Log.d(TAG, "节目集合焦点选中 -> " + programTypeName + " pos : " + i);
                    } else {
                        Log.e(TAG, "onClickMenuListener is null");
                    }
                }
            });
        }
    }

    /**
     * 多级菜单点击
     */
    public interface OnClickMenuListener {

        void onParentClick(int parentIndex);

        void onChildClick(int parentIndex, int pos);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // 按键处理分发
        int keyCode = event.getKeyCode();
        View curFocusView = getCurFocusView(event);
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            // 节目集合,第0位置获取焦点,保存位置节目类型位置。
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && isFocusInProgramType(curFocusView)) {
                View view = getRvItemView(rvProgramCollection, 0);
                return requestFocus(view);
            }


            // 节目类型,回到节目类型之前的位置。
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && isFocusInProgramCol(curFocusView)) {
                View view = getRvItemView(rvProgramType, rvProgramTypeFocusSelectIndex.getIndex());
                return requestFocus(view);
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                // 获取当前焦点位置
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

            }

            // 菜单收起和关闭相关操作
            if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT) {
                openMenu();// 遥控器按下右键打开菜单
            }
            if(keyCode == KeyEvent.KEYCODE_BACK) {
                closeMenu();// 遥控器按下返回关闭菜单
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private boolean requestFocus(View view) {
        if (null != view) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            return view.requestFocus();
        } else {
            Log.e("", "view is null,requestFocus is failure");
        }
        return false;
    }

    private View getCurFocusView(KeyEvent keyEvent) {
        View focused = mainView.findFocus();
        if (focused != null) {
            return focused;
        }
        Log.e("TAG", "findFocus View is null");
        return null;
    }

    private boolean isFocusInProgramType(View curFocusView) {
        if (null == curFocusView)
            return false;

        boolean flag = ((ViewGroup) curFocusView.getParent()).getId() == R.id.rv_program_type;
        return flag;
    }

    private boolean isFocusInProgramCol(View curFocusView) {
        if (null == curFocusView)
            return false;

        boolean flag = ((ViewGroup) curFocusView.getParent()).getId() == R.id.rv_program_collection;
        return flag;
    }

    private View getRvItemView(RecyclerView rv, int pos) {
        if (rv == null) {
            throw new NullPointerException("rv is null");
        }
        if (pos < 0 && null != rv.getAdapter() && pos < rv.getAdapter().getItemCount()) {
            throw new IllegalArgumentException("pos is exception");
        }
        return rv.getLayoutManager().findViewByPosition(pos);
    }

}


