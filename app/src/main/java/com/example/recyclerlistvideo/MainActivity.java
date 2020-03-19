package com.example.recyclerlistvideo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.listener.GridSpanSizeLookup;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<MultiItem> data = new ArrayList<>();
    private MultiItemAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rv);
        SmartRefreshLayout refreshLayout = findViewById(R.id.refreshLayout);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new MultiItemAdapter(data);
        adapter.setGridSpanSizeLookup(new GridSpanSizeLookup() {
            @Override
            public int getSpanSize(@NonNull GridLayoutManager gridLayoutManager, int viewType, int position) {
                if (viewType == MultiItem.GRID) {
                    return 1;
                }
                return 2;
            }
        });
        recyclerView.setAdapter(adapter);
        initDatas();
    }

    public void enter(View view) {
        //暂停视频
        adapter.pauseVideo();
        startActivity(new Intent(this, Main2Activity.class));
    }

    private void initDatas() {
        MultiItem item = new MultiItem();
        item.setItemType(MultiItem.VIDEO);
        data.add(item);
        for (int j = 0; j < 30; j++) {
            MultiItem item1 = new MultiItem();
            item1.setItemType(MultiItem.TWOPIC);
            data.add(item1);
        }
        for (int i = 0; i < 80; i++) {
            MultiItem b = new MultiItem();
            b.setItemType(MultiItem.GRID);
            data.add(b);
        }
        adapter.notifyDataSetChanged();

    }
}
