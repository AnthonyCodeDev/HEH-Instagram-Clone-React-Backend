package be.heh.stragram.application.port.out;

import java.time.Instant;

public interface ClockPort {
    
    Instant now();
}
