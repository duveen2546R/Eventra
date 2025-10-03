package com.evantra.evantra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Binds properties prefixed with "razorpay.key" from application.properties
 *  to the fields in this class. This is the recommended way to handle
 *  custom application properties.
 */
@Configuration
@ConfigurationProperties(prefix = "razorpay.key")
@Getter
@Setter
public class RazorpayProperties {

    /**
     * The public Key ID from your Razorpay dashboard (e.g., rzp_test_...).
     */
    private String id;

    /**
     * The private Key Secret from your Razorpay dashboard.
     */
    private String secret;

}