package io.ivansanchez16.logger;

import io.ivansanchez16.logger.classes.ClientInfo;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Order
@RequiredArgsConstructor
public class RequestInfoFilter implements Filter {

    private final Logger logger = LogManager.getLogger(RequestInfoFilter.class.getName());

    private final String transactionHeaderName;
    private final String sessionHeadersPrefix;

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
            
        }

        filterChain.doFilter(req, servletResponse);
    }
}
