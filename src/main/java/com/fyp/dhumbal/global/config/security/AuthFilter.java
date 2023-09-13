package com.fyp.dhumbal.global.config.security;

import com.fyp.dhumbal.global.service.RequestDataProvider;
import com.fyp.dhumbal.global.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final RequestDataProvider requestDataProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        CustomAuthentication authentication = tokenService.validateToken(requestDataProvider.getAccessToken());
        SecurityContextHolder.getContext()
                .setAuthentication(
                        Objects.requireNonNullElseGet(
                                authentication,
                                () -> new AnonymousAuthenticationToken(AuthConstant.ANONYMOUS_USER, AuthConstant.ANONYMOUS_USER, AuthorityUtils.createAuthorityList(AuthConstant.ANONYMOUS))));
        chain.doFilter(request, response);
    }
}
