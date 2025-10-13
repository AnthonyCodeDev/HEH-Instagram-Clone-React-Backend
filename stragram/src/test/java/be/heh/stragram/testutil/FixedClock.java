package be.heh.stragram.testutil;

import be.heh.stragram.application.port.out.ClockPort;

import java.time.Instant;

public final class FixedClock implements ClockPort {
    private final Instant fixed;
    
    public FixedClock(String isoInstant) {
        this.fixed = Instant.parse(isoInstant);
    }
    
    @Override
    public Instant now() {
        return fixed;
    }
}
