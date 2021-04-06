package me.makp.naiveratelimiter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "rl")
@Getter
@Setter
@Validated
public class RLProperties {

    @Valid
    private List<RateLimiterInstance> tenants = new ArrayList<>();

    @Getter
    @Setter
    public static class RateLimiterInstance {
        @NotBlank
        private String name;
        @NotNull
        private Long capacity;
        @NotNull
        private Long refillTokens;
        @NotNull
        private Long refillPeriodMillis;
    }
}
