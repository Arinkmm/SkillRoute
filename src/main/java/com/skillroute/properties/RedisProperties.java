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
    private long retentionTtlMinutes;
    private String resendLimitPrefix;
    private long resendIntervalMinutes;
    private String passwordResetPrefix;
    private long passwordResetTtlMinutes;
    private String passwordResetLimitPrefix;
    private long passwordResetIntervalMinutes;
    private String registrationLimitPrefix;
    private long registrationIntervalSeconds;
}
