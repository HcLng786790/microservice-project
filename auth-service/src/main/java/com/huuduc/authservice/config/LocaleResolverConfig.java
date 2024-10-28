package com.huuduc.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@SuppressWarnings("unused")
@Configuration
public class LocaleResolverConfig  {

    // Tạo Bean để quản lý các tệp tin nhắn thông điệp i18n, đồng thời tải các tệp tin
    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600); // Cache for an hour
        return messageSource;
    }

    // Xác định locale(vùng miền) nào sẽ được sử dụng
    @Bean
    public LocaleResolver localeResolver() {

        // Xác định sử dụng Http header Accept-Language từ phía client để xác định ngôn ngữ
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH); // Đặt mặc định cho miền US
        return localeResolver;
    }
}
