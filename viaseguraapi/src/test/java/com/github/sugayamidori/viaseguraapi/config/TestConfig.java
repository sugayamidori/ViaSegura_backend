package com.github.sugayamidori.viaseguraapi.config;

import org.springframework.boot.test.web.server.LocalServerPort;

public interface TestConfig {

    @LocalServerPort
    int SERVER_PORT = 8888;

    String HEADER_PARAM_AUTHORIZATION = "Authorization";
    String HEADER_PARAM_ORIGIN = "Origin";

    String ORIGIN_LOCALHOST = "http://localhost:8080";
    String ORIGIN_INVALID_LOCALHOST = "http://localhost:1000";
}
