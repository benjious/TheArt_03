package com.example.benjious.theart_03;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ui.HorizontalScrollViewEx;
import util.MyUtils;

/**
 * Created by benjious on 2016/11/6.
 */

public class DemoActivity1 extends AppCompatActivity {
    public static final String TAG = "DemoActivity1";
    private HorizontalScrollViewEx mListContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_1);
        Log.d(TAG, "xyz  onCreate: onCreate()");
        initView();
    }

    //把三个ListView装载进去
    private void initView() {
        LayoutInflater layoutInflater = getLayoutInflater();
        mListContainer = (HorizontalScrollViewEx) findViewById(R.id.container);
        final int screenWidth = MyUtils.getScreenMetrics(this).widthPixels;
        final int screenHeight = MyUtils.getScreenMetrics(this).heightPixels;
        for (int i = 0; i < 3; i++) {
            //inflate()这个方法是把View装载到ViewGroup
            ViewGroup layout = (ViewGroup) layoutInflater.inflate(R.layout.content_layout, mListContainer, false);
            layout.getLayoutParams().width = screenWidth;
            layout.setBackgroundColor(Color.rgb(255 / (i + 1), 255 / (i + 1), 0));
            TextView textView = (TextView)layout.findViewById(R.id.conten_title);
            textView.setText("Page " + (i + 1));
            createList(layout);
            mListContainer.addView(layout);

        }

    }

    //ListView中放数据
    private void createList(ViewGroup viewGroup) {
        ListView listView = (ListView) viewGroup.findViewById(R.id.content_list);
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 50; i++) {
            arrayList.add("data " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.content_list_item, R.id.name, arrayList);
        listView.setAdapter(adapter);
    }
}
