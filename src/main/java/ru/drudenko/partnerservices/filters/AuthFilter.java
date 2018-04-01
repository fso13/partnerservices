package ru.drudenko.partnerservices.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.drudenko.partnerservices.domain.Token;
import ru.drudenko.partnerservices.domain.TokenRepository;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Component
public class AuthFilter implements Filter {
    private static final String HEADER_AUTH = "authorization";
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = asHttp(servletRequest);
        HttpServletResponse httpResponse = asHttp(servletResponse);

        String token = httpRequest.getHeader(HEADER_AUTH);

        if (token == null || (token.trim().isEmpty() || !token.trim().startsWith("Bearer"))) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            token = token.trim().substring("Bearer ".length());
            Token t = tokenRepository.findTokenById(token);
            Instant now = Instant.now();
            if (t == null || !t.getCreated().isBefore(now) || !t.getExpired().isAfter(now)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    @Override
    public void destroy() {

    }

    private HttpServletRequest asHttp(ServletRequest request) {
        return (HttpServletRequest) request;
    }

    private HttpServletResponse asHttp(ServletResponse response) {
        return (HttpServletResponse) response;
    }

}
