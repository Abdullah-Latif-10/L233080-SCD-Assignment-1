

// ...existing code...
import java.util.*;

public abstract class Task {
    protected int id;
    protected String title;
    protected List<Integer> dependencies;

    public Task(int id, String title, List<Integer> dependencies) {
        this.id = id;
        this.title = title;
        this.dependencies = dependencies;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public List<Integer> getDependencies() { return dependencies; }

    public abstract java.time.LocalDateTime getStart();
    public abstract java.time.LocalDateTime getEnd();
    public abstract long getDurationHours();

    @Override
    public String toString() {
        return "Task " + id + ": " + title +
               " Depends on: " + (dependencies.isEmpty() ? "None" : dependencies);
    }
}
