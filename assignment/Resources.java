package assignment;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Resources {
    String name;
    Map<Integer, Integer> taskEfforts; // TaskID -> percentage effort

    public Resources(String name) {
        this.name = name;
        this.taskEfforts = new HashMap<>();
    }

    public void addTaskEffort(int taskId, int percent) {
        taskEfforts.put(taskId, percent);
    }

    @Override
    public String toString() {
        String efforts = taskEfforts.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue() + "%")
                .collect(Collectors.joining(", "));

        return "Name: " + name + " -> TaskEfforts: [" + efforts + "]";
    }
}
