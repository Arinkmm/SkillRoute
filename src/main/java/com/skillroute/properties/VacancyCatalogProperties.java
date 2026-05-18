package com.skillroute.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "vacancy.catalog")
public class VacancyCatalogProperties {
    private int highDemandLimit;
}
