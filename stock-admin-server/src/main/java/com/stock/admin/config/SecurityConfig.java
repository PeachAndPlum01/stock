package com.stock.admin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Spring Boot Admin 安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServer;

    public SecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = 
            new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        successHandler.setDefaultTargetUrl(adminServer.path("/"));

        http.authorizeRequests()
            .antMatchers(adminServer.path("/assets/**")).permitAll()
            .antMatchers(adminServer.path("/login")).permitAll()
            .antMatchers(adminServer.path("/actuator/**")).permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage(adminServer.path("/login"))
            .successHandler(successHandler)
            .and()
            .logout()
            .logoutUrl(adminServer.path("/logout"))
            .and()
            .httpBasic()
            .and()
            .csrf()
            .disable();

        return http.build();
    }
}
