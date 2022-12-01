package com.jhdz.ddns.task;

import com.jhdz.ddns.client.DDNSClient;
import com.jhdz.ddns.config.DDNSConfig;
import com.jhdz.ddns.config.DingTalkRobotConfig;
import com.jhdz.ddns.entity.DDNS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class DdnsTask {

    @Autowired
    private DDNSConfig ddnsConfig;
    @Autowired
    private DingTalkRobotConfig dingTalkRobotConfig;

    private final DDNSClient ddnsClient;

    public DdnsTask(DDNSClient ddnsClient) {
        this.ddnsClient = ddnsClient;
    }

    /**
     * 刷新阿里巴巴DNS解析记录,同时通过自定义钉钉机器人通知各群ip变化
     * 项目启动自动执行一次，之后每隔五分钟执行一次
     */
    @Scheduled(fixedRate=300000)
    public void refreshDdns() {
        List<DDNS> ddnsList = ddnsConfig.getList();
        for (DDNS ddns: ddnsList) {
            ddnsClient.refreshDdns(ddns, dingTalkRobotConfig);
        }

    }
}
