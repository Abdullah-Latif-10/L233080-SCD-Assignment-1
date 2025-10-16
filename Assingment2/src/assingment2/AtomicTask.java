package assingment2;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;



import java.time.format.DateTimeFormatter;

public class AtomicTask extends Task {
    private LocalDateTime start;
    private LocalDateTime end;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd+HHmm");

    public AtomicTask(int id, String title, String start, String end, List<Integer> dependencies) {
        super(id, title, dependencies);
        this.start = LocalDateTime.parse(start, FORMATTER);
        this.end = LocalDateTime.parse(end, FORMATTER);
    }


    @Override
    public long getDurationHours() {
        return Duration.between(start, end).toHours();
    }


    @Override
    public LocalDateTime getStart() { return start; }

    @Override
    public LocalDateTime getEnd() { return end; }

    @Override
    public String toString() {
        return super.toString() + " [" + start + " - " + end + "] deps=" + getDependencies();
    }
}
