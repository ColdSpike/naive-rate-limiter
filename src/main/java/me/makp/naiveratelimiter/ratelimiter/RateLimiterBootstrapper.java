package me.makp.naiveratelimiter.ratelimiter;

import lombok.Getter;
import lombok.Setter;
import me.makp.naiveratelimiter.config.RLProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RateLimiterBootstrapper {

    private final Map<String, StateAndConfig> tenants = new HashMap<>();

    public RateLimiterBootstrapper(RLProperties properties) {
        properties.getTenants().forEach(tenant -> {
            tenants.put(tenant.getName(), parseConfig(tenant));
        });
    }

    public StateAndConfig getTenant(String tenant) {
        return tenants.getOrDefault(tenant, null);
    }

    private StateAndConfig parseConfig(RLProperties.RateLimiterInstance tenant) {
        return new StateAndConfig(
                new RLConfig(tenant.getCapacity(), tenant.getRefillTokens(), tenant.getRefillPeriodMillis())
        );
    }


}

@Getter
class RLConfig {
    private final long capacity;
    private final double refillTokensPerOneMillis;

    RLConfig(long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokensPerOneMillis = (double) refillTokens / (double) refillPeriodMillis;
    }
}

@Getter
@Setter
class RLState {
    private double tokens;
    private long lastRefreshedAt;

    public RLState(RLConfig config) {
        this.tokens = config.getCapacity();
        this.lastRefreshedAt = System.currentTimeMillis();
    }

    private RLState() {
    }

    public RLState copy() {
        RLState state = new RLState();
        state.setLastRefreshedAt(lastRefreshedAt);
        state.setTokens(tokens);
        return state;
    }

    @Override
    public String toString() {
        return "RLState { tokens=" + tokens + ", lastRefreshedAt=" + lastRefreshedAt + " }";
    }
}
