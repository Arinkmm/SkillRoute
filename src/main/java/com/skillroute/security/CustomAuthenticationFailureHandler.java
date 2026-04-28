package com.skillroute.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String errorMessage = "Неверный логин или пароль";

        if (exception instanceof DisabledException) {
            errorMessage = "Аккаунт не подтвержден. Проверьте почту";
        }

        request.getSession().setAttribute("loginError", errorMessage);

        getRedirectStrategy().sendRedirect(request, response, "/login");
    }
}