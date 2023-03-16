package com.jhdz.ddns;

import com.jhdz.ddns.util.IPUtil;
import org.junit.jupiter.api.Test;

public class ipTest {


    @Test
    public void getIP() {
        String currentHostIP = null;
        try {
            currentHostIP = IPUtil.getIP();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(currentHostIP);
    }
}
