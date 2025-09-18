package assignment;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProjectDuration {

    // Load tasks from file
    public static List<Task<Integer>> loadTasks(String fileName) throws IOException {
        List<Task<Integer>> tasks = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Example: 1, Research, 2025-09-01 10:00, 2025-09-02 12:00, 2 3
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String start = parts[2].trim();
                String end = parts[3].trim();

                List<Integer> dependencies = new ArrayList<>();
                if (parts.length > 4) {
                    String[] deps = parts[4].trim().split(" ");
                    for (String d : deps) {
                        if (!d.isEmpty()) {
                            dependencies.add(Integer.parseInt(d.trim()));
                        }
                    }
                }
                
tasks.add(new Task<Integer>(id, title, start, end, dependencies));
            }
        }
        return tasks;
    }

    // Load resources from file
    public static List<Resources> loadResources(String fileName) throws IOException {
        List<Resources> resources = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Example: Ahmed, 1:50, 3:100, 4:100, 5:50
                String[] parts = line.split(",");
                String name = parts[0].trim();
                Resources res = new Resources(name);

                for (int i = 1; i < parts.length; i++) {
                    String[] taskEffort = parts[i].trim().split(":");
                    int taskId = Integer.parseInt(taskEffort[0].trim());
                    int effort = Integer.parseInt(taskEffort[1].trim());
                    res.addTaskEffort(taskId, effort);
                }
                resources.add(res);
            }
        }
        return resources;
    }

    // Find project completion time & duration
    public static void findCompletionTime(List<Task<Integer>> tasks) {
        LocalDateTime earliest = tasks.stream()
                .map(t -> t.startTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latest = tasks.stream()
                .map(t -> t.endTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (earliest != null && latest != null) {
            System.out.println("Project Completion Time: " + latest);
            System.out.println("Project Duration: " +
                    java.time.Duration.between(earliest, latest).toHours() + " hours");
        }
    }

    // Highlight overlapping tasks
    public static void findOverlappingTasks(List<Task<Integer>> tasks) {
        System.out.println("\nOverlapping Tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            for (int j = i + 1; j < tasks.size(); j++) {
                Task<Integer> t1 = tasks.get(i);
                Task<Integer> t2 = tasks.get(j);

                boolean overlap = t1.startTime.isBefore(t2.endTime) &&
                                  t2.startTime.isBefore(t1.endTime);

                if (overlap) {
                    System.out.println("Task " + t1.id + " (" + t1.title + ") overlaps with Task " +
                                       t2.id + " (" + t2.title + ")");
                }
            }
        }
    }

    // Find resources for a specific task
    public static void findTeamForTask(List<Resources> resources, int taskId) {
        System.out.print("\nTeam for Task " + taskId + ": ");
        for (Resources r : resources) {
            if (r.taskEfforts.containsKey(taskId)) {
                System.out.print(r.name + " (" + r.taskEfforts.get(taskId) + "%)  ");
            }
        }
        System.out.println();
    }

    // Find total effort (hours) per resource
    public static void findTotalEffort(List<Resources> resources, List<Task<Integer>> tasks) {
        System.out.println("\nTotal Effort Per Resource:");
        for (Resources r : resources) {
            long total = 0;
            for (Map.Entry<Integer, Integer> entry : r.taskEfforts.entrySet()) {
                int taskId = entry.getKey();
                int percent = entry.getValue();

                Task<Integer> task = tasks.stream()
                        .filter(t -> t.id == taskId)
                        .findFirst()
                        .orElse(null);

                if (task != null) {
                    total += (task.getDurationHours() * percent) / 100;
                }
            }
            System.out.println(r.name + " => " + total + " hours");
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        try {
            List<Task<Integer>> tasks = loadTasks("tasks.txt");
            List<Resources> resources = loadResources("resources.txt");

            findCompletionTime(tasks);
            findOverlappingTasks(tasks);
            findTeamForTask(resources, 1);
            findTotalEffort(resources, tasks);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
