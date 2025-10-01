package com.evantra.evantra.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Binds properties prefixed with "jwt" from application.properties
 *  to the fields in this class.
 */
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * The secret key for signing the JWT. Must be a long, random Base64-encoded string.
     * This is read from the 'jwt.secret' property.
     */
    private String secret;

}