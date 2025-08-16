package me.bang9.api.global.config;

public class Whitelist {

    public static final String[] URL_LIST = {
            "/api/v1/docs/swagger*/**",
            "/v1/api-docs/**",
    };

    public static final String[] DOMAIN_LIST = {
            "https://api.bang9.me",
            "http:/localhost:8080",
    };

}
