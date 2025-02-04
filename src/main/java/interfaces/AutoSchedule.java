package interfaces;

import java.time.LocalDateTime;

public interface AutoSchedule {
    boolean scheduled();
    void schedule(LocalDateTime startTime, LocalDateTime endTime);
}
