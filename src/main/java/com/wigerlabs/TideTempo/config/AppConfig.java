package com.wigerlabs.TideTempo.config;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig(){
        packages("com.wigerlabs.TideTempo.controller");
        register(org.glassfish.jersey.media.multipart.MultiPartFeature.class);
    }
}