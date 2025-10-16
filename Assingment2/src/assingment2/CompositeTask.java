package assingment2;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CompositeTask extends Task {
    private List<Task> subtasks = new ArrayList<>();

    public CompositeTask(int id, String title, List<Integer> dependencies) {
        super(id, title, dependencies);
    }

    public void addSubtask(Task t) {
        subtasks.add(t);
    }

    public List<Task> getSubtasks() { return subtasks; }

    @Override
    public long getDurationHours() {
        long total = 0;
        for (Task t : subtasks) {
            total += t.getDurationHours();
        }
        return total;
    }

    @Override
    public LocalDateTime getStart() {
        LocalDateTime earliest = null;
        for (Task t : subtasks) {
            LocalDateTime s = t.getStart();
            if (s != null) {
                if (earliest == null || s.isBefore(earliest)) earliest = s;
            }
        }
        return earliest;
    }

    @Override
    public LocalDateTime getEnd() {
        LocalDateTime latest = null;
        for (Task t : subtasks) {
            LocalDateTime e = t.getEnd();
            if (e != null) {
                if (latest == null || e.isAfter(latest)) latest = e;
            }
        }
        return latest;
    }

    @Override
    public String toString() {
        return super.toString() + " (composite, subtasks=" + subtasks.size() + ")";
    }
}
