package com.miguelsperle.teach_crafter.infra.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final SecurityFilter securityFilter;
    @Qualifier("customAuthenticationEntryPoint")
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Qualifier("customAccessDeniedHandler")
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfiguration(final SecurityFilter securityFilter, final AuthenticationEntryPoint authenticationEntryPoint, final AccessDeniedHandler accessDeniedHandler) {
        this.securityFilter = securityFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    private static final String[] SWAGGER_LIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(SWAGGER_LIST).permitAll()
                                .requestMatchers("/users/register").permitAll()
                                .requestMatchers("/auth/login").permitAll()
                                .requestMatchers("/reset-password/send-email").permitAll()
                                .requestMatchers("/reset-password").permitAll()
                                .requestMatchers("/courses/create").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseId}/update-name").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseId}/update-description").hasRole("CREATOR")
                                .requestMatchers("/courses/creator-owned").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseId}/deactivate").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseId}/content").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseContentId}/content/upload-video").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseContentId}/content/update-description").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseContentId}/content/update-video").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseContentId}/content/update-release-date").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseContentId}/content/update-course-module").hasRole("CREATOR")
                                .requestMatchers("/courses/{courseId}/contents/creator-owned").hasRole("CREATOR")
                                .anyRequest().authenticated())
                .exceptionHandling((exceptions) -> exceptions.authenticationEntryPoint(authenticationEntryPoint).accessDeniedHandler(accessDeniedHandler))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
