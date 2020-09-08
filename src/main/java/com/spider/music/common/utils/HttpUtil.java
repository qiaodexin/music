package com.spider.music.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定义返回结果的代码以及消息等信息
 *
 * @author muchuanwei@yunzhangfang.com
 * @date 2018/11/02
 */
@Component
@Slf4j
public class HttpUtil {

    private static RestTemplate restTemplate;

    public HttpUtil(RestTemplate restTemplate) {
        HttpUtil.restTemplate = restTemplate;
    }

    public static String get(String url) {
        log.info("[HTTP][GET]{}", url);
        String result = restTemplate.getForObject(url, String.class);
        log.info("[HTTP][GET]{}{}", url, result);
        return result;
    }

    public static String get(String url, JSONObject params) {
        return restTemplate.getForObject(expandURL(url, params.keySet()), String.class, params);
    }

    public static String getForKw(String url, JSONObject params, JSONObject header) {
        HttpHeaders requestHeaders = new HttpHeaders();
        return restTemplate.getForObject(expandURL(url, params.keySet()), String.class, params);
    }

    public static <T> T get(String url, JSONObject params, Class<T> clz) {
        return restTemplate.getForObject(expandURL(url, params.keySet()), clz, params);
    }

    public static String delete(String url, Map<String, ?> uriVariables) {
        return exchange(url, null, MediaType.APPLICATION_JSON, uriVariables, HttpMethod.DELETE);
    }

    public static String delete(String url, JSONObject params, MediaType mediaType, Map<String, ?> uriVariables) {
        return exchange(url, params, mediaType, uriVariables, HttpMethod.DELETE);
    }

    public static String put(String url, Map<String, ?> uriVariables) {
        return put(url, null, MediaType.APPLICATION_JSON, uriVariables);
    }

    public static String put(String url, JSONObject params, MediaType mediaType) {
        return put(url, params, mediaType, null);
    }

    public static String put(String url, JSONObject params, MediaType mediaType, Map<String, ?> uriVariables) {
        return exchange(url, params, mediaType, uriVariables, HttpMethod.PUT);
    }

    public static String post(String url, JSONObject params, MediaType mediaType, Map<String, ?> uriVariables) {
        return exchange(url, params, mediaType, uriVariables, HttpMethod.POST);
    }

    public static String post(String url, JSONObject params, MediaType mediaType) {
        // 拿到header信息
        String result = null;
        try {
            HttpHeaders requestHeaders = new HttpHeaders();
            if (mediaType == MediaType.APPLICATION_JSON || mediaType == MediaType.APPLICATION_JSON_UTF8) {
                requestHeaders.setContentType(mediaType);
                HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, requestHeaders);
                result = restTemplate.postForObject(url, requestEntity, String.class);

            } else if (mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
                MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
                requestHeaders.setContentType(type);
                requestHeaders.set("Accept-Encoding", "gzip,deflate");
                HttpEntity<JSONObject> requestEntity = new HttpEntity<>(null, requestHeaders);
                result = restTemplate.postForObject(expandURL(url, params.keySet()), requestEntity, String.class, params);
            }
        } catch (Exception e) {
            // log.error("http请求异常:{}", ExceptionUtils.getStackTrace(e));
            JSONObject json = new JSONObject();
            json.put("errorCode", "-9999999999");
            json.put("success", false);
            json.put("msg", "http请求异常:" + e.getMessage());
            json.put("message", "http请求异常:" + e.getMessage());
            result = json.toJSONString();
        }
        return result;
    }

    public static <T> T post(String url, JSONObject params, MediaType mediaType, Class<T> clz) {
        // 拿到header信息
        T result = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        if (mediaType == MediaType.APPLICATION_JSON || mediaType == MediaType.APPLICATION_JSON_UTF8) {
            requestHeaders.setContentType(mediaType);
            HttpEntity<?> requestEntity = new HttpEntity<>(params, requestHeaders);

            result = restTemplate.postForObject(url, requestEntity, clz);

        } else if (mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
            MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaders.setContentType(type);
            HttpEntity<?> requestEntity = new HttpEntity<MultiValueMap>(createMultiValueMap(params), requestHeaders);

            result = restTemplate.postForObject(url, requestEntity, clz, params);
        } else {
            requestHeaders.setContentType(mediaType);
            HttpEntity<?> requestEntity = new HttpEntity<MultiValueMap>(createMultiValueMap(params), requestHeaders);
            result = restTemplate.postForObject(url, requestEntity, clz, params);
        }

        return result;
    }

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    private static MultiValueMap<String, String> createMultiValueMap(JSONObject params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        for (String key : params.keySet()) {
            if (params.get(key) instanceof List) {
                for (Iterator<String> it = ((List<String>) params.get(key)).iterator(); it.hasNext(); ) {
                    String value = it.next();
                    map.add(key, value);
                }
            } else {
                map.add(key, params.getString(key));
            }
        }
        return map;
    }

    private static String expandURL(String url, Set<?> keys) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");

        for (Object key : keys) {
            sb.append(key).append("=").append("{").append(key).append("}").append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String expandHeader(Set<?> keys) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");

        for (Object key : keys) {
            sb.append(key).append("=").append("{").append(key).append("}").append("&");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    private static String exchange(String url, JSONObject params, MediaType mediaType, Map<String, ?> uriVariables, HttpMethod httpMethod) {
        String result = null;
        HttpHeaders requestHeaders = new HttpHeaders();
        if (MapUtils.isNotEmpty(uriVariables)) {
            url = expandURL(url, uriVariables.keySet());
        }
        if (mediaType == MediaType.APPLICATION_JSON || mediaType == MediaType.APPLICATION_JSON_UTF8) {
            requestHeaders.setContentType(mediaType);
            HttpEntity<JSONObject> requestEntity = new HttpEntity<>(params, requestHeaders);
            result = restTemplate.exchange(url, httpMethod, requestEntity, String.class, uriVariables).getBody();
        } else if (mediaType == MediaType.APPLICATION_FORM_URLENCODED) {
            MediaType type = MediaType.parseMediaType("application/x-www-form-urlencoded; charset=UTF-8");
            requestHeaders.setContentType(type);
            requestHeaders.set("Accept-Encoding", "gzip,deflate");
            HttpEntity<JSONObject> requestEntity = new HttpEntity<>(null, requestHeaders);
            result = restTemplate.exchange(expandURL(url, params.keySet()), httpMethod, requestEntity, String.class, uriVariables).getBody();
        }
        return result;
    }
}