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
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CompanyProfileService companyProfileService;
    private final StudentProfileService studentProfileService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/", "/login", "/register", "/verification", "/verification/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register", "/register/check-field", "/register/resend-verification").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/student/profile", "/student/profile/**").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/student/**", "/route/**").hasRole("STUDENT")
                        .requestMatchers("/student/**", "/route/**").access(this::hasCompletedStudentProfileAccess)
                        .requestMatchers("/company/profile", "/company/profile/**").hasRole("COMPANY")
                        .requestMatchers("/company/**").access(this::hasConfirmedCompanyAccess)
                        .requestMatchers(HttpMethod.POST, "/main/companies/**").hasRole("ADMIN")
                        .requestMatchers("/admin/**", "/companies/**").hasRole("ADMIN")
                        .requestMatchers("/chat/**").access(this::hasChatAccess)
                        .requestMatchers("/main", "/logout").hasAnyRole("STUDENT", "COMPANY", "ADMIN")
                        .requestMatchers("/main/**").hasRole("ADMIN")
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

    private AuthorizationDecision hasConfirmedCompanyAccess(Supplier<Authentication> authentication,
                                                            RequestAuthorizationContext context) {
        return new AuthorizationDecision(isConfirmedCompany(authentication.get()));
    }

    private AuthorizationDecision hasChatAccess(Supplier<Authentication> authentication,
                                                RequestAuthorizationContext context) {
        Authentication currentAuthentication = authentication.get();

        if (!(currentAuthentication.getPrincipal() instanceof CustomUserDetails user)) {
            return new AuthorizationDecision(false);
        }

        boolean hasAccess = (user.getRole() == Role.STUDENT && studentProfileService.isProfileComplete(user.getId())) ||
                (user.getRole() == Role.COMPANY && companyProfileService.isConfirmed(user.getId()));

        return new AuthorizationDecision(hasAccess);
    }

    private AuthorizationDecision hasCompletedStudentProfileAccess(Supplier<Authentication> authentication,
                                                                   RequestAuthorizationContext context) {
        Authentication currentAuthentication = authentication.get();

        if (!(currentAuthentication.getPrincipal() instanceof CustomUserDetails user)) {
            return new AuthorizationDecision(false);
        }

        boolean hasAccess = user.getRole() == Role.STUDENT && studentProfileService.isProfileComplete(user.getId());

        return new AuthorizationDecision(hasAccess);
    }

    private boolean isConfirmedCompany(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails user)) {
            return false;
        }

        return user.getRole() == Role.COMPANY && companyProfileService.isConfirmed(user.getId());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
