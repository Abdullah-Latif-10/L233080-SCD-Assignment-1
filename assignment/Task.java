package assignment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Task<T> {
    int id;
    String title;
    LocalDateTime startTime;
    LocalDateTime endTime;
    List<T> dependencies;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Task(int id,
                String title,
                String startTime,
                String endTime,
                List<T> dependencies) {
        this.id = id;
        this.title = title;
        this.startTime = LocalDateTime.parse(startTime, FORMATTER);
        this.endTime = LocalDateTime.parse(endTime, FORMATTER);
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        return "Task " + id + ": " + title +
                " [" + startTime + " - " + endTime + "] " +
                "Depends on: " + (dependencies.isEmpty() ? "None" : dependencies);
    }
    public long getDurationHours() {
    return java.time.Duration.between(startTime, endTime).toHours();
}
}
