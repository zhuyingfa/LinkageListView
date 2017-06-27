package com.jeff.linkagelistview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jeff.linkagelistview.adapter.AdapterLeft;
import com.jeff.linkagelistview.adapter.AdapterRight;
import com.jeff.linkagelistview.bean.Bean;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView mListViewLeft;
    private AdapterLeft mAdapterLeft;

    private RecyclerView mListViewRight;
    private AdapterRight mAdapterRight;

    private ArrayList<Bean> dataList = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<Integer> titlePosList = new ArrayList<>();

    private String mCurTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView(){
        mListViewLeft = (ListView) findViewById(R.id.listview_left);
        mAdapterLeft = new AdapterLeft(this, titleList);
        mListViewLeft.setAdapter(mAdapterLeft);
        mListViewLeft.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                mAdapterLeft.setSelection(pos);
                if (null != titleList && titleList.size()>pos)
                    mAdapterRight.setSelection(pos);
            }
        });

        mListViewRight = (RecyclerView) findViewById(R.id.listview_right);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mListViewRight.setLayoutManager(linearLayoutManager);
        mAdapterRight = new AdapterRight(this, dataList, titlePosList, mListViewRight);
        mListViewRight.addItemDecoration(new ItemDecoration(this, dataList, new ItemDecoration.OnDecorationCallback() {
            @Override
            public String onGroupId(int pos) {
                if (dataList.get(pos).getTitle() != null)
                    return dataList.get(pos).getTitle();
                return "-1";
            }

            @Override
            public String onGroupFirstStr(int pos) {
                if (dataList.get(pos).getTitle() != null)
                    return dataList.get(pos).getTitle();
                return "";
            }

            @Override
            public void onGroupFirstStr(String title) {
                for (int i=0; i<titleList.size(); i++){
                    if (!mCurTitle.equals(title) && title.equals(titleList.get(i))){
                        mCurTitle = title;
                        mAdapterLeft.setSelection(i);
                        Log.i(TAG, "onGroupFirstStr: i = "+i);
                    }
                }
            }
        }));
        mListViewRight.setAdapter(mAdapterRight);
    }

    /**
     * 数据
     */
    private void initData(){
        titlePosList.add(0);
        for (int i=0; i<5; i++){
            Bean bean = new Bean();
            bean.setTitle("0");
            bean.setText("zzzz");
            dataList.add(bean);
        }
        titleList.add(dataList.get(dataList.size()-1).getTitle());
        titlePosList.add(dataList.size());
        for (int i=0; i<15; i++){
            Bean bean = new Bean();
            bean.setTitle("1");
            bean.setText("xxxx");
            dataList.add(bean);
        }
        titleList.add(dataList.get(dataList.size()-1).getTitle());
        titlePosList.add(dataList.size());
        for (int i=0; i<20; i++){
            Bean bean = new Bean();
            bean.setTitle("2");
            bean.setText("cccc");
            dataList.add(bean);
        }
        titleList.add(dataList.get(dataList.size()-1).getTitle());
        titlePosList.add(dataList.size());
        for (int i=0; i<10; i++){
            Bean bean = new Bean();
            bean.setTitle("3");
            bean.setText("dddd");
            dataList.add(bean);
        }
        titleList.add(dataList.get(dataList.size()-1).getTitle());
        mAdapterLeft.notifyDataSetChanged();
        mAdapterRight.notifyDataSetChanged();
    }
}
