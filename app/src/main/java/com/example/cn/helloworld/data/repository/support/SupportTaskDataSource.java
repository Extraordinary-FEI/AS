package com.example.cn.helloworld.data.repository.support;

import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.List;

/**
 * 数据源接口，便于后续替换为网络或数据库实现。
 */
public interface SupportTaskDataSource {

    List<HomeModels.SupportTask> getSupportTasks();
}
