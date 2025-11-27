package com.example.cn.helloworld.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cn.helloworld.R;

/**
 * 独立的应援任务列表 Fragment，方便底部导航单独展示。
 */
public class SupportTasksFragment extends Fragment {

    private HomeDataSource dataSource;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_support_tasks, container, false);

        // ⭐ 关键修改：传入 Context
        dataSource = new FakeHomeDataSource(root.getContext());

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_support_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TaskAdapter(dataSource.loadSupportTasks()));
        return root;
    }
}
