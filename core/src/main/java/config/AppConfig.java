package config;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@Singleton
@Startup
public class AppConfig {
    private final BigDecimal ANNUAL_INTEREST_RATE = new BigDecimal("0.05");

    public BigDecimal getANNUAL_INTEREST_RATE() {
        return ANNUAL_INTEREST_RATE;
    }
}
