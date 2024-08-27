package com.wootecam.api.config;

import com.wootecam.api.controller.CurrentTimeArgumentResolver;
import com.wootecam.api.interceptor.AuthenticationInterceptor;
import com.wootecam.api.util.AuthenticationArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final AuthenticationArgumentResolver authenticationArgumentResolver;
    private final CurrentTimeArgumentResolver currentTimeArgumentResolver;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://lucky.dbsg.co.kr:3000",
                        "http://lucky.dbsg.co.kr:3001",
                        "http://tomcat.dbsg.co.kr:3000",
                        "http://tomcat.dbsg.co.kr:3001"
                )
                .allowedHeaders("*")
                .allowCredentials(true)
                .allowedMethods("GET", "HEAD", "POST", "DELETE", "TRACE", "OPTIONS", "PATCH", "PUT");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationArgumentResolver);
        resolvers.add(currentTimeArgumentResolver);
    }
}
