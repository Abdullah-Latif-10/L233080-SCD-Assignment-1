

import java.util.*;

public class Resource {
    private String name;
    private Map<Integer, Integer> taskEfforts; // taskId -> %

    public Resource(String name) {
        this.name = name;
        this.taskEfforts = new HashMap<>();
    }

    public void addTaskEffort(int taskId, int percent) {
        taskEfforts.put(taskId, percent);
    }

    public String getName() { return name; }
    public Map<Integer, Integer> getTaskEfforts() { return taskEfforts; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name + " -> [ ");
        for (Map.Entry<Integer, Integer> e : taskEfforts.entrySet()) {
            sb.append(e.getKey()).append(":").append(e.getValue()).append("%, ");
        }
        if (!taskEfforts.isEmpty()) sb.setLength(sb.length() - 2);
        sb.append(" ]");
        return sb.toString();
    }private List<Allocation> allocations = new ArrayList<>();

public List<Allocation> getAllocations() { return allocations; }
public void addAllocation(Allocation a) { allocations.add(a); }

}
