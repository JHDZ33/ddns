package com.jhdz.ddns.util;


import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.jhdz.ddns.entity.RobotAccessToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class DingTalkRobotUtil {

    public static void sendRobotMsg(String content, List<RobotAccessToken> robotAccessList){
        for (RobotAccessToken robotAccessToken : robotAccessList) {
            DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=" + robotAccessToken.getAccessToken());
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(content);
            request.setText(text);
            OapiRobotSendResponse response = null;
            try {
                response = client.execute(request);
                if (!response.isSuccess()) {
                    log.info(response.getErrmsg());
                }
            } catch (Exception e) {
                log.error("调用钉钉robot发送群消息失败：{}", e.getMessage());
            }
        }

//        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
//        at.setAtMobiles(Arrays.asList("132xxxxxxxx"));
//        // isAtAll类型如果不为Boolean，请升级至最新SDK
//        at.setIsAtAll(true);
//        at.setAtUserIds(Arrays.asList("109929","32099"));
//        request.setAt(at);
//
//        request.setMsgtype("link");
//        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
//        link.setMessageUrl("https://www.dingtalk.com/");
//        link.setPicUrl("");
//        link.setTitle("时代的火车向前开");
//        link.setText("这个即将发布的新版本，创始人xx称它为红树林。而在此之前，每当面临重大升级，产品经理们都会取一个应景的代号，这一次，为什么是红树林");
//        request.setLink(link);
//
//        request.setMsgtype("markdown");
//        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
//        markdown.setTitle("杭州天气");
//        markdown.setText(content);
//        request.setMarkdown(markdown);
    }
}
