package com.fifa_app.league_manager.Configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String validApiKey;

    public ApiKeyFilter(@Value("${app.api.key}") String validApiKey) {
        this.validApiKey = validApiKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String apiKey = request.getHeader("X-API-KEY");

        System.out.println("Clé reçue: " + apiKey);
        System.out.println("Clé attendue: " + validApiKey);

        if (apiKey == null || !apiKey.equals(validApiKey)) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Invalid API Key\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}