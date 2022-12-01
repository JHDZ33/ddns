package com.jhdz.ddns.entity;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DDNS {

    private int id;
    private String domain;
    private String keyword;
    private String regionId;
    private String accessKey;
    private String accessKeySecret;
    private String type;
}
