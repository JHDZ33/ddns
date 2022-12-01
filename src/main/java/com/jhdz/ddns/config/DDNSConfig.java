package com.jhdz.ddns.config;

import com.jhdz.ddns.entity.DDNS;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ali")
@Data
public class DDNSConfig {

    private List<DDNS> list;
}
