package com.zq.wx.mp.utils;

import com.zq.wx.mp.constants.Constants;
import com.zq.wx.mp.resource.zq.AuthTokenResponse;
import com.zq.wx.mp.resource.zq.MaterialUploadResponse;
import org.apache.http.HttpEntity;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FluentUtil {

    private static final Logger logger = LoggerFactory.getLogger(FluentUtil.class);

    public static String httpGet(String url) {
        try {
            HttpEntity entity = Request.Get(url).execute().returnResponse().getEntity();

            String res = null;
            if (entity != null) {
                res = EntityUtils.toString(entity);
            }
            return res;

        } catch (Exception e) {
            logger.error("get request url: {}\nERROR: {}", url, e);
            return null;
        }
    }

    public static AuthTokenResponse httpGet_1(String url) {
        String json = httpGet(url);
        return new AuthTokenResponse(json);
    }

    public static MaterialUploadResponse httpGet_2(String url) {
        String json = httpGet(url);
        return new MaterialUploadResponse(json);
    }

    public static String httpPost(String url, Object requestBody) {
        try {
            String json = JsonUtils.toJson(requestBody);
            HttpEntity entity = Request.Post(url)
//                    .bodyString(json, ContentType.create("text/html", "UTF-8"))
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().returnResponse().getEntity();

            String res = null;
            if (entity != null) {
                res = EntityUtils.toString(entity);
            }
//            logger.debug("http post res: {}", res);

            return res;
        } catch (Exception e) {
            logger.error("post request url: {}\nERROR: {}", url, e);
            return null;
        }
    }

    public static AuthTokenResponse httpPost_1(String url, Object requestBody) {
        String json = httpPost(url, requestBody);
        return new AuthTokenResponse(json);
    }

    public static String httpPost(String url, String token, Object requestBody) {
        try {
            String json = JsonUtils.toJson(requestBody);
            HttpEntity entity = Request.Post(url)
                    .addHeader(Constants.ZQ_WX_AUTH_TOKEN, token)
//                    .bodyString(json, ContentType.create("text/html", "UTF-8"))
                    .bodyString(json, ContentType.APPLICATION_JSON)
                    .execute().returnResponse().getEntity();

            String res = null;
            if (entity != null) {
                res = EntityUtils.toString(entity);
            }
//            logger.debug("http post res: {}", res);

            return res;
        } catch (Exception e) {
            logger.error("post request url: {}\nERROR: {}", url, e);
            return null;
        }
    }
}
