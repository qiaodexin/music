package com.spider.music.business;

import com.alibaba.fastjson.JSONObject;
import com.spider.music.common.utils.HttpUtil;

/**
 * @author qiaodexin@yunzhangfang.com
 * @date 2020/9/8 14:25
 * @description 酷我音乐
 */
public class KuWoController {

    private static final String KW_URL = "http://www.kuwo.cn/api/www/search/searchMusicBykeyWord";

    public void spiderKuWo(String name, Integer pn, Integer rn) {
        JSONObject param = new JSONObject();
        param.put("pn", pn);
        param.put("rn", rn);
        param.put("key", name);
        param.put("httpsStatus", "1");
        param.put("reqId", "0bbe6050-f0ee-11ea-849b-41bed68313e4");
        HttpUtil.get(KW_URL, param);
    }
}
