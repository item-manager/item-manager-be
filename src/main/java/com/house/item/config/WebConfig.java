package com.house.item.config;

import com.house.item.config.web.LoginCheckInterceptor;
import com.house.item.domain.ConsumableItemsOrderByType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/auth/**", "/error", "/swagger*/**", "/api-docs/**");
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, ConsumableItemsOrderByType>() {
            @Override
            public ConsumableItemsOrderByType convert(String source) {
                return ConsumableItemsOrderByType.fromCode(source);
            }
        });
    }
}
