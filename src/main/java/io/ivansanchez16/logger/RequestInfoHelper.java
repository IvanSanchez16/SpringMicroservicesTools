package io.ivansanchez16.logger;

import io.ivansanchez16.logger.classes.ClientInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RequestInfoHelper {

    @Resource
    public HttpServletRequest request;

    public ClientInfo getClientInfoFromRequest() {
        try {
            return (ClientInfo) request.getAttribute("ORIGIN-INFO");
        } catch (IllegalStateException e) {
            return new ClientInfo("Unknown", "Unknown", UUID.randomUUID());
        }
    }

    public JSONObject getSessionInfoFromRequest() {
        try {
            return (JSONObject) request.getAttribute("SESSION-INFO");
        } catch (IllegalStateException e) {
            return null;
        }
    }
}
