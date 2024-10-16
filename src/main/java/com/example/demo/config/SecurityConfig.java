package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 禁用 CSRF 保护
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/register", "/admin/login","/permissions/**","/submitapplication/**").permitAll()  // 允许匿名访问注册和登录接口
                        .anyRequest().authenticated())  // 其他请求需要认证
                .formLogin().disable();  // 禁用默认的表单登录
        return http.build();
    }
}
