package com.example.oauth2.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/","/index.html","/oauth_login.html").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(l -> l
            .loginPage("/oauth_login.html")
            .defaultSuccessUrl("/", true)
        )
        .logout(l -> l
            // .logoutUrl("/logout").permitAll()
            .logoutSuccessUrl("/")
            .permitAll()
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
        );
        // .csrf(c -> c
        //     .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        // );
        return http.build();
    }
}
