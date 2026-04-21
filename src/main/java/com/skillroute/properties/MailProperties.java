package com.skillroute.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "mail")
public class MailProperties {
    private String baseUrl;
    private String from;
    private String sender;
    private String subject;
    private String content;
}
