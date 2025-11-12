package com.example.cn.helloworld.data.repository.support;

import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.List;

/**
 * 任务仓库，负责提供应援任务数据。
 */
public class SupportTaskRepository {

    private final SupportTaskDataSource dataSource;

    public SupportTaskRepository() {
        this(new LocalSupportTaskDataSource());
    }

    public SupportTaskRepository(SupportTaskDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<HomeModels.SupportTask> getSupportTasks() {
        return dataSource.getSupportTasks();
    }
}
