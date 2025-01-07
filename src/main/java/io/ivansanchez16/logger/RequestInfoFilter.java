package io.ivansanchez16.logger;

import io.ivansanchez16.logger.classes.ClientInfo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Order
public class RequestInfoFilter implements Filter {

    private final String transactionHeaderName;
    private final String sessionHeadersPrefix;
    private final List<String> sessionHeadersList;

    public RequestInfoFilter(String transactionHeaderName, String sessionHeadersPrefix, String[] sessionHeadersList) {
        this.transactionHeaderName = transactionHeaderName != null ? transactionHeaderName : "TRANSACTION-ID";
        this.sessionHeadersPrefix = sessionHeadersPrefix != null ? sessionHeadersPrefix.toLowerCase() : "";
        this.sessionHeadersList = Stream.of(sessionHeadersList).map(String::toLowerCase).toList();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) servletRequest;

        // Retrieve origin info and transaction
        UUID currentTransaction;
        String providedTransaction = req.getHeader(transactionHeaderName);
        if (providedTransaction == null) {
            currentTransaction = UUID.randomUUID();
        } else {
            currentTransaction = UUID.fromString( providedTransaction );
        }

        req.setAttribute("ORIGIN-INFO", new ClientInfo(req.getRemoteAddr(), req.getRemoteHost(), currentTransaction));

        // Retrieve SessionInfo
        List<String> headers = Collections.list(req.getHeaderNames());
        headers.removeIf(h -> !h.startsWith(sessionHeadersPrefix + "."));
        if (headers.size() > 0) {
            JSONObject sessionObject = new JSONObject();

            headers.forEach(header -> {
                String valueName = header.split("\\.")[1];
                if (!sessionHeadersList.contains(valueName)) {
                    return;
                }

                sessionObject.put(valueName, req.getHeader(header));
            });

            req.setAttribute("SESSION-INFO", sessionObject);
        }

        filterChain.doFilter(req, servletResponse);
    }
}
