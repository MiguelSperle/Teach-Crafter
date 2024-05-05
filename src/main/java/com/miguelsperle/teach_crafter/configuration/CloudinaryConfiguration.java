package com.miguelsperle.teach_crafter.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {

    @Value("${spring.cloudinary.cloud_name}")
    private String cloudinaryCloudName;

    @Value("${spring.cloudinary.api_key}")
    private String cloudinaryApiKey;

    @Value("${spring.cloudinary.api_secret}")
    private String cloudinaryApiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudinaryCloudName, "api_key", cloudinaryApiKey, "api_secret", cloudinaryApiSecret));
    }
}