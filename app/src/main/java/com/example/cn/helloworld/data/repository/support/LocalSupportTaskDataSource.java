package com.example.cn.helloworld.data.repository.support;

import com.example.cn.helloworld.ui.main.HomeModels;

import java.util.Arrays;
import java.util.List;

/**
 * 本地占位实现，后续可切换到真实后端接口。
 */
public class LocalSupportTaskDataSource implements SupportTaskDataSource {

    @Override
    public List<HomeModels.SupportTask> getSupportTasks() {
        return Arrays.asList(
                new HomeModels.SupportTask(
                        "task_live_commentary",
                        "《乌托邦少年》直播护航",
                        "线上控评",
                        "4月20日 18:00-22:00",
                        "微博话题 #易烊千玺乌托邦少年#",
                        "直播期间守护主话题热度，维持讨论区积极氛围，防止恶意引导。",
                        "提前准备 5 条走心评论，直播开始后每 10 分钟轮换一次关键词并截图留档。",
                        "小千守护官（微信：yixixx）",
                        "已有 186 名小橙灯签到，热度冲刺中。",
                        300,
                        186,
                        HomeModels.SupportTask.TaskStatus.ONGOING,
                        HomeModels.SupportTask.RegistrationStatus.CHECK_IN
                ),
                new HomeModels.SupportTask(
                        "task_birthday_billboard",
                        "重庆大屏生日倒计时",
                        "线下募资",
                        "4月25日-4月30日",
                        "解放碑十字星光屏",
                        "筹备千玺生日月户外大屏，联动同城粉丝完成素材制作与投放排期。",
                        "报名后加入企划群，按照模板提交转账截图与祝福语，统一审核后安排时段。",
                        "策划人 小苡（QQ：2881188）",
                        "已完成 60% 时段认领，等待更多同城应援官加入。",
                        60,
                        38,
                        HomeModels.SupportTask.TaskStatus.UPCOMING,
                        HomeModels.SupportTask.RegistrationStatus.OPEN
                ),
                new HomeModels.SupportTask(
                        "task_charity_run",
                        "易烊千玺公益捐步挑战",
                        "公益行动",
                        "4月10日-4月17日",
                        "任意运动 App 上传",
                        "联合千纸鹤发起捐步计划，目标 5 亿步为山区孩子筹集体育基金。",
                        "每日 22:00 前上传步数截图并备注所在地，工作人员统一统计提交公益平台。",
                        "千纸鹤公益组（微博私信 @千纸鹤守护）",
                        "累计达成 120% 目标步数，活动完美收官。",
                        500,
                        500,
                        HomeModels.SupportTask.TaskStatus.COMPLETED,
                        HomeModels.SupportTask.RegistrationStatus.CLOSED
                )
        );
    }
}
