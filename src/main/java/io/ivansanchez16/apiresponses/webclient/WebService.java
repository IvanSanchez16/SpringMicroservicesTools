package io.ivansanchez16.apiresponses.webclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
class WebService {

    private final String name;
    private final String url;

    // Optionals
    private final Boolean useProxy;
    private final Integer connectionTimeout;
    private final Integer responseTimeout;
}
