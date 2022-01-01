package com.zelazobeton.cognitiveexercisesmemory.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class GzipBodyDecompressFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean isGzipped = request.getHeader(HttpHeaders.CONTENT_ENCODING) != null &&
                request.getHeader(HttpHeaders.CONTENT_ENCODING).contains("gzip");
        // CHECK IF THIS COMPARISON WORKS
        boolean requestTypeSupported = HttpMethod.POST.toString().equals(request.getMethod());
        if (!isGzipped) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!requestTypeSupported) {
            throw new IllegalStateException(request.getMethod() +
                    " request gzip compression is not supported. Only POST requests are currently supported.");
        }
        request = new GzippedInputStreamWrapper(request);
        filterChain.doFilter(request, response);
    }
}