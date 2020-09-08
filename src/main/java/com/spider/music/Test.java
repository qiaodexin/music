package com.spider.music;

import com.alibaba.fastjson.JSONObject;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author qiaodexin@yunzhangfang.com
 * @date 2020/9/7 21:48
 * @description com.spider.music
 */
public class Test {

    public static void main(String[] args) {
        final String url = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord?key="+"醒来折花&httpsStatus=1&reqId=0bbe6050-f0ee-11ea-849b-41bed68313e4";
        JSONObject param = new JSONObject();
        param.put("pn", 1);
        param.put("rn", 10);
        param.put("key", "醒来折花");
        param.put("httpsStatus", "1");
        param.put("reqId", "0bbe6050-f0ee-11ea-849b-41bed68313e4");
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.135 Safari/537.36 Edg/84.0.522.63");
        requestHeaders.add("Cookie", "_ga=GA1.2.1083049585.1590317697; _gid=GA1.2.2053211683.1598526974; _gat=1; Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1597491567,1598094297,1598096480,1598526974; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1598526974; kw_token=HYZQI4KPK3P");
        requestHeaders.add("Referer", "http://www.kuwo.cn/search/list?key=%E5%91%A8%E6%9D%B0%E4%BC%A6");
        requestHeaders.add("csrf", "HYZQI4KPK3P");
        RestTemplate template = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String sttr = response.getBody();
        System.out.println(sttr);
    }
}
