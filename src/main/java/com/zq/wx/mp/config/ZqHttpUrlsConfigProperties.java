package com.zq.wx.mp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zq.http.urls")
public class ZqHttpUrlsConfigProperties {

    public String uploadMaterial;

    public void setUploadMaterial(String uploadMaterial) {
        this.uploadMaterial = uploadMaterial;
    }
}
