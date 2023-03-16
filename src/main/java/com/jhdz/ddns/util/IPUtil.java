package com.jhdz.ddns.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class IPUtil {
    /**
     * 获取 IP 地址的服务列表
     */
    private static final String[] IPV4_SERVICES = {
            "https://checkip.amazonaws.com/",
            "https://ipv4.icanhazip.com/",
            "https://api.ipify.org",
            "https://ifconfig.me/ip",
            "https://icanhazip.com",
            "https://ipinfo.io/ip",
            "https://ipecho.net/plain",
            "https://checkipv4.dedyn.io"
    };
    /**
     * IP 地址校验的正则表达式
     */
    private static final Pattern IPV4_PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


    public static String getIP() throws ExecutionException, InterruptedException {
        // 考虑到这些第三方接口不一定 100% 稳定，例如可能出现下线、错误、访问超时或太慢的情况，所以不能仅依赖其中之一。
        List<Callable<String>> callables = new ArrayList<>();
        for (String ipService : IPV4_SERVICES) {
            callables.add(() -> get(ipService));
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            // 返回第一个成功获取的 IP
            return executorService.invokeAny(callables);
        } finally {
            executorService.shutdown();
        }
    }

    private static String get(String url) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            String ip = in.readLine();
            if (IPV4_PATTERN.matcher(ip).matches()) {
                return ip;
            } else {
                throw new IOException("invalid IPv4 address: " + ip);
            }
        }
    }
}
