package com.skillroute.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "redis.verification")
public class RedisProperties {
    private String prefix;
    private long ttlMinutes;
    private String resendLimitPrefix;
    private long resendIntervalMinutes;
}