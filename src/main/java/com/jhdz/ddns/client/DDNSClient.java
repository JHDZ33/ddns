package com.jhdz.ddns.client;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.jhdz.ddns.config.DingTalkRobotConfig;
import com.jhdz.ddns.entity.DDNS;
import com.jhdz.ddns.util.DingTalkRobotUtil;
import com.jhdz.ddns.util.IPUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态域名解析
 */
@Component
@Log4j2
public class DDNSClient {

    /**
     * 获取主域名的所有解析记录列表
     */
    private DescribeDomainRecordsResponse describeDomainRecords(DescribeDomainRecordsRequest request, IAcsClient client){
        try {
            // 调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            log.error(e.getMessage());
            // 发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    /**
     * 修改解析记录
     */
    private UpdateDomainRecordResponse updateDomainRecord(UpdateDomainRecordRequest request, IAcsClient client){
        try {
            // 调用SDK发送请求
            return client.getAcsResponse(request);
        } catch (ClientException e) {
            log.error(e.getMessage());
            // 发生调用错误，抛出运行时异常
            throw new RuntimeException();
        }
    }

    private static void log_print(String functionName, Object result) {
        Gson gson = new Gson();
        log.info("-------------------------------" + functionName + "-------------------------------");
        log.info(gson.toJson(result));
    }


    public void refreshDdns(DDNS ddns, DingTalkRobotConfig dingTalkRobotConfig) {
        // 设置鉴权参数，初始化客户端
        DefaultProfile profile = DefaultProfile.getProfile(
                ddns.getRegionId(),// 地域ID
                ddns.getAccessKey(),// 您的AccessKey ID
                ddns.getAccessKeySecret());// 您的AccessKey Secret
        IAcsClient client = new DefaultAcsClient(profile);

        DDNSClient ddnsClient = new DDNSClient();

        // 查询指定二级域名的最新解析记录
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest();
        // 主域名
        describeDomainRecordsRequest.setDomainName(ddns.getDomain());
        // 主机记录
        describeDomainRecordsRequest.setRRKeyWord(ddns.getKeyword());
        // 解析记录类型
        describeDomainRecordsRequest.setType(ddns.getType());
        DescribeDomainRecordsResponse describeDomainRecordsResponse = ddnsClient.describeDomainRecords(describeDomainRecordsRequest, client);
        log_print("describeDomainRecords",describeDomainRecordsResponse);

        List<DescribeDomainRecordsResponse.Record> domainRecords = describeDomainRecordsResponse.getDomainRecords();
        // 最新的一条解析记录
        if(domainRecords.size() != 0 ){
            DescribeDomainRecordsResponse.Record record = domainRecords.get(0);
            // 记录ID
            String recordId = record.getRecordId();
            // 记录值
            String recordsValue = record.getValue();
            // 当前主机公网IP
            String currentHostIP = null;
            try {
                currentHostIP = IPUtil.getIP();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.info("-------------------------------当前主机公网IP为："+currentHostIP+"-------------------------------");
            if(!currentHostIP.equals(recordsValue)){
                // 修改解析记录
                UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest();
                // 主机记录 (不进行修改)
                updateDomainRecordRequest.setRR(ddns.getKeyword());
                // 记录ID
                updateDomainRecordRequest.setRecordId(recordId);
                // 将主机记录值改为当前主机IP
                updateDomainRecordRequest.setValue(currentHostIP);
                // 解析记录类型(不进行修改)
                updateDomainRecordRequest.setType(ddns.getType());
                UpdateDomainRecordResponse updateDomainRecordResponse = ddnsClient.updateDomainRecord(updateDomainRecordRequest, client);
                log_print("updateDomainRecord",updateDomainRecordResponse);

                if (ddns.getId() == 1 && dingTalkRobotConfig.isEnabled()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String content = "pub_ip："+ simpleDateFormat.format(new Date()) +  " 办公室公网IP变化通知, 新IP：" + currentHostIP +  " 旧IP：" + recordsValue;
                    log.info("钉钉通知内容：{}",content);
                    DingTalkRobotUtil.sendRobotMsg(content, dingTalkRobotConfig.getList());
                }
            }
        }
    }
}
