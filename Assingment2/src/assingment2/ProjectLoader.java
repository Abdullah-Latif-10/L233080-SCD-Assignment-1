package assingment2;


import java.io.*;
// ...existing code...
import java.util.*;


public class ProjectLoader {
    // Removed unused DateTimeFormatter fields

 
    public static List<Task> loadTasks(String filename) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 5); // keep title and times even if title contains commas
                if (parts.length < 4) continue;

                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String startStr = parts[2].trim();
                String endStr = parts[3].trim();

                List<Integer> deps = new ArrayList<>();
                if (parts.length == 5) {
                    String depPart = parts[4].trim();
                    // allow commas or spaces or both; normalize spaces
                    depPart = depPart.replaceAll(",", " ").trim();
                    if (!depPart.isEmpty()) {
                        String[] tokens = depPart.split("\\s+");
                        for (String tok : tokens) {
                            if (!tok.isEmpty()) deps.add(Integer.parseInt(tok.trim()));
                        }
                    }
                }

                // pass startStr and endStr directly to AtomicTask (AtomicTask parses them)
                AtomicTask t = new AtomicTask(id, title, startStr, endStr, deps);
                tasks.add(t);
            }
        }
        return tasks;
    }

    // Load resources: line format:
    // Name, taskId:percent, taskId:percent, ...
    // For each allocation, create Allocation and add to Resource and return mapping done in caller.
    public static List<Resource> loadResources(String filename, List<Task> tasks) throws IOException {
        List<Resource> resources = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                String name = parts[0].trim();
                Resource r = new Resource(name);

                for (int i = 1; i < parts.length; i++) {
                    String token = parts[i].trim(); // e.g. "1:50"
                    if (token.isEmpty()) continue;
                    String[] kv = token.split(":");
                    if (kv.length != 2) continue;
                    int taskId = Integer.parseInt(kv[0].trim());
                    int percent = Integer.parseInt(kv[1].trim());

                    Task task = findTaskById(tasks, taskId);
                    if (task != null) {
                        Allocation a = new Allocation(r, task, percent);
                        r.addAllocation(a);
                        r.addTaskEffort(taskId, percent); // <-- ensure taskEfforts is filled
                    } else {
                        // If task not found, skip (or you might want to warn)
                    }
                }
                resources.add(r);
            }
        }
        return resources;
    }

    private static Task findTaskById(List<Task> tasks, int id) {
        for (Task t : tasks) if (t.getId() == id) return t;
        return null;
    }

    // ...existing code...
}
