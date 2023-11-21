package io.ivansanchez16.apiresponses;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * ResponseFilter
 */
@Component
@RequiredArgsConstructor
public class ResponseFilter implements Filter {

    private final Logger LOGGER = LogManager.getLogger(ResponseFilter.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletResponse res = (HttpServletResponse) response;

        ContentCachingResponseWrapper resp = new ContentCachingResponseWrapper(res);
        chain.doFilter(request, resp);

        byte[] responseBody = resp.getContentAsByteArray();
        String strResponse = new String(responseBody, StandardCharsets.UTF_8);

        try {
            final JSONObject jsonObject = objectMapper.convertValue(strResponse, JSONObject.class);

            res.setStatus( jsonObject.getJSONObject("meta").getInt("statusCode") );
        } catch (IllegalArgumentException | JSONException e) {
            LOGGER.warn("The response haven't Meta's structure");
        }

        resp.copyBodyToResponse();
    }
}
