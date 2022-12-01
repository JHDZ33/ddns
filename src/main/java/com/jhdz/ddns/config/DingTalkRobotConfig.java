package com.jhdz.ddns.config;

import com.jhdz.ddns.entity.RobotAccessToken;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "robot")
@Data
public class DingTalkRobotConfig {

    private boolean enabled;
    private List<RobotAccessToken> list;
}
