package me.makp.naiveratelimiter.ratelimiter;

import java.util.concurrent.atomic.AtomicReference;

public class StateAndConfig {
    private final AtomicReference<RLState> state;
    private final RLConfig config;

    public StateAndConfig(RLConfig config) {
        this.config = config;
        this.state = new AtomicReference<>(new RLState(config));
    }

    public boolean tryConsumeTokens(int tokens) {
        RLState currentState = state.get();
        RLState newState = currentState.copy();

        while (true) {
            refresh(newState);

            if (newState.getTokens() < tokens) {
                return false;
            }

            newState.setTokens(newState.getTokens() - tokens);

            if (state.compareAndSet(currentState, newState)) {
                return true;
            } else {
                currentState = state.get();
                newState = currentState.copy();
            }
        }
    }

    private void refresh(RLState state) {
        long currentTime = System.currentTimeMillis();
        if (currentTime > state.getLastRefreshedAt()) {
            long millisSinceLastRefill = currentTime - state.getLastRefreshedAt();
            double perMili = config.getRefillTokensPerOneMillis();
            double refill = ((double) millisSinceLastRefill) * perMili;

            state.setTokens(Math.min(config.getCapacity(), state.getTokens() + refill));
            state.setLastRefreshedAt(currentTime);
        }
    }

    public double getRemainingLimit() {
        return Math.floor(state.get().getTokens());
    }
}
