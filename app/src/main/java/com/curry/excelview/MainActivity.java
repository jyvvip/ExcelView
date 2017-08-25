package com.curry.excelview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ListView;

import com.curry.excelview.adapter.LvInfoAdapter;
import com.curry.excelview.adapter.LvNameAdapter;
import com.curry.excelview.view.LinkedHorizontalScrollView;
import com.curry.excelview.view.NoScrollHorizontalScrollView;


/**
 * Created by Lunger on 2015/02/05 15:40
 */
public class MainActivity extends AppCompatActivity {

    private NoScrollHorizontalScrollView mGoodsNameSV;//不可滑动的顶部右侧的ScrollView
    private LinkedHorizontalScrollView mInfoContainer;//底部右侧的ScrollView
    private ListView mListViewName;//底部左侧的ListView
    private ListView mListViewInfo;//底部右侧的ListView

    boolean isLeftListEnabled = false;
    boolean isRightListEnabled = false;

    private LvNameAdapter mLvNormalNameAdapter;
    private LvInfoAdapter mLvNormalInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAdapter();
    }


    private void initView() {
        mGoodsNameSV = (NoScrollHorizontalScrollView)findViewById(R.id.sv_title);
        mInfoContainer = (LinkedHorizontalScrollView)findViewById(R.id.sv_good_detail);
        mListViewName = (ListView) findViewById(R.id.lv_goods_name);
        mListViewInfo = (ListView)findViewById(R.id.lv_good_info);
        //联动控件
        combination(mListViewName, mListViewInfo, mGoodsNameSV, mInfoContainer);
    }

    private void initAdapter() {
        mLvNormalNameAdapter = new LvNameAdapter(this);
        mLvNormalInfoAdapter = new LvInfoAdapter(this);
        mListViewName.setAdapter(mLvNormalNameAdapter);
        mListViewInfo.setAdapter(mLvNormalInfoAdapter);
    }

    private void combination(final ListView lvName, final ListView lvDetail, final HorizontalScrollView title, LinkedHorizontalScrollView content) {
        /**
         * 左右滑动同步
         */
        content.setMyScrollChangeListener(new LinkedHorizontalScrollView.LinkScrollChangeListener() {
            @Override
            public void onscroll(LinkedHorizontalScrollView view, int x, int y, int oldx, int oldy) {
                title.scrollTo(x, y);
            }
        });

        /**
         * 上下滑动同步
         */
        // 禁止快速滑动
        lvName.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        lvDetail.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        //左侧ListView滚动时，控制右侧ListView滚动
        lvName.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //这两个enable标志位是为了避免死循环
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isRightListEnabled = false;
                    isLeftListEnabled = true;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    isRightListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View child = view.getChildAt(0);
                if (child != null && isLeftListEnabled) {
                    lvDetail.setSelectionFromTop(firstVisibleItem, child.getTop());
                }
            }
        });

        //右侧ListView滚动时，控制左侧ListView滚动
        lvDetail.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    isLeftListEnabled = false;
                    isRightListEnabled = true;
                } else if (scrollState == SCROLL_STATE_IDLE) {
                    isLeftListEnabled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                View c = view.getChildAt(0);
                if (c != null && isRightListEnabled) {
                    lvName.setSelectionFromTop(firstVisibleItem, c.getTop());
                }
            }
        });

    }
}
