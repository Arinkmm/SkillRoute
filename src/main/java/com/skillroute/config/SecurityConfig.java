package com.skillroute.config;

import com.skillroute.model.Role;
import com.skillroute.security.CustomAuthenticationFailureHandler;
import com.skillroute.security.CustomUserDetails;
import com.skillroute.service.CompanyProfileService;
import com.skillroute.service.StudentProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.SecurityFilterChain;

import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/",
            "/login",
            "/register",
            "/verification",
            "/password/forgot",
            "/password/reset"
    };

    private static final String[] PUBLIC_POST_ENDPOINTS = {
            "/register",
            "/register/check-field",
            "/verification/resend",
            "/password/forgot",
            "/password/reset",
            "/password/reset/check-field"
    };

    private static final String[] API_DOC_ENDPOINTS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    private static final String[] STUDENT_PROFILE_ENDPOINTS = {
            "/student/profile",
            "/student/profile/**"
    };

    private static final String[] STUDENT_WORKSPACE_ENDPOINTS = {
            "/student/**",
            "/route/**"
    };

    private static final String[] COMPANY_PROFILE_ENDPOINTS = {
            "/company/profile",
            "/company/profile/**"
    };

    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CompanyProfileService companyProfileService;
    private final StudentProfileService studentProfileService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(API_DOC_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.POST, PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers("/main").hasAnyRole(Role.STUDENT.name(), Role.COMPANY.name(), Role.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/main/companies/*/approve").hasRole(Role.ADMIN.name())
                        .requestMatchers("/main/**").hasRole(Role.ADMIN.name())
                        .requestMatchers("/account/**").authenticated()
                        .requestMatchers(COMPANY_PROFILE_ENDPOINTS).hasRole(Role.COMPANY.name())
                        .requestMatchers("/company/**").access(this::hasReadyCompanyAccess)
                        .requestMatchers(STUDENT_PROFILE_ENDPOINTS).hasRole(Role.STUDENT.name())
                        .requestMatchers(HttpMethod.GET, STUDENT_WORKSPACE_ENDPOINTS).hasRole(Role.STUDENT.name())
                        .requestMatchers(STUDENT_WORKSPACE_ENDPOINTS).access(this::hasCompletedStudentProfileAccess)
                        .requestMatchers("/chat/**").access(this::hasChatAccess)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/main", true)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());
        return http.build();
    }

    private AuthorizationDecision hasReadyCompanyAccess(Supplier<Authentication> authentication,
                                                        RequestAuthorizationContext context) {
        return new AuthorizationDecision(isReadyCompany(authentication.get()));
    }

    private AuthorizationDecision hasChatAccess(Supplier<Authentication> authentication,
                                                RequestAuthorizationContext context) {
        Authentication currentAuthentication = authentication.get();
        return new AuthorizationDecision(isReadyStudent(currentAuthentication) || isReadyCompany(currentAuthentication));
    }

    private AuthorizationDecision hasCompletedStudentProfileAccess(Supplier<Authentication> authentication,
                                                                   RequestAuthorizationContext context) {
        return new AuthorizationDecision(isReadyStudent(authentication.get()));
    }

    private boolean isReadyStudent(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return false;
        }

        return user.getRole() == Role.STUDENT && studentProfileService.isProfileComplete(user.getId());
    }

    private boolean isReadyCompany(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return false;
        }

        return user.getRole() == Role.COMPANY
                && companyProfileService.isProfileComplete(user.getId())
                && companyProfileService.isConfirmed(user.getId());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
