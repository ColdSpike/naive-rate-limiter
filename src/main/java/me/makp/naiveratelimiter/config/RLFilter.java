package me.makp.naiveratelimiter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.makp.naiveratelimiter.ratelimiter.RateLimiterBootstrapper;
import me.makp.naiveratelimiter.ratelimiter.StateAndConfig;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
@Order(1)
@RequiredArgsConstructor
public class RLFilter implements Filter {

    private final ObjectMapper mapper;
    private final RateLimiterBootstrapper bootstrapper;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String uri = request.getRequestURI();
//        String tenant = request.getHeader("X-tenant");

        String tenant = null;
        try {
            tenant = uri.split("/")[2];
        } catch (Exception e) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), Map.of("error", "API NOT SUPPORTED"));
        }

        StateAndConfig rl = bootstrapper.getTenant(tenant);

        if (rl == null) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), Map.of("error", "TENANT NOT SUPPORTED"));
        } else {
            if (!rl.tryConsumeTokens(1)) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader("X-RateLimit-Remaining", String.valueOf(rl.getRemainingLimit()));
                mapper.writeValue(response.getWriter(), Map.of("error", "REQUEST LIMIT REACHED", "X-RateLimit-Remaining", rl.getRemainingLimit()));
            } else {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setHeader("X-RateLimit-Remaining", String.valueOf(rl.getRemainingLimit()));
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
