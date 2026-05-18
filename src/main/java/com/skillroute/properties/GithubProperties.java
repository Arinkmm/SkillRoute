package com.skillroute.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "github")
public class GithubProperties {
    private String token;
    private Sync sync = new Sync();

    @Setter
    @Getter
    public static class Sync {
        private long codeSearchDelayMillis = 7000;
        private long workerDelayMillis = 5000;
        private long defaultRateLimitWaitMinutes = 5;
        private long runningTimeoutMinutes = 30;
    }
}
