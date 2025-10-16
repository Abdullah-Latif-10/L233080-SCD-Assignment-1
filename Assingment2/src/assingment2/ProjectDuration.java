package assingment2;


import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ProjectDuration {
    public static List<Task> loadTasks(String fileName) throws IOException {
        List<Task> tasks = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String start = parts[2].trim();
                String end = parts[3].trim();

                List<Integer> deps = new ArrayList<>();
                for (int i = 4; i < parts.length; i++) {
                    if (!parts[i].trim().isEmpty())
                        deps.add(Integer.parseInt(parts[i].trim()));
                }
                tasks.add(new AtomicTask(id, title, start, end, deps));
            }
        }
        return tasks;
    }

    public static List<Resource> loadResources(String fileName) throws IOException {
        List<Resource> resources = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                Resource r = new Resource(parts[0].trim());

                for (int i = 1; i < parts.length; i++) {
                    String[] pair = parts[i].trim().split(":");
                    if (pair.length == 2)
                        r.addTaskEffort(Integer.parseInt(pair[0].trim()), Integer.parseInt(pair[1].trim()));
                }
                resources.add(r);
            }
        }
        return resources;
    }

    // i) Project completion time
    public static void findCompletionTime(List<Task> tasks) {
        LocalDateTime earliest = tasks.stream().map(Task::getStart).min(LocalDateTime::compareTo).orElse(null);
        LocalDateTime latest = tasks.stream().map(Task::getEnd).max(LocalDateTime::compareTo).orElse(null);

        if (earliest != null && latest != null) {
            System.out.println("Project Completion Time: " + latest);
            System.out.println("Project Duration: " +
                    java.time.Duration.between(earliest, latest).toDays() + " days");
        }
    }

    // ii) Overlapping tasks
    public static void findOverlappingTasks(List<Task> tasks) {
        System.out.println("Overlapping Tasks:");
        for (Task t : tasks) {
            for (Integer depId : t.getDependencies()) {
                Task dep = tasks.stream().filter(d -> d.getId() == depId).findFirst().orElse(null);
                if (dep != null && t.getStart().isBefore(dep.getEnd())) {
                    System.out.println("Task " + t.getId() + " overlaps with dependency " + dep.getId());
                }
            }
        }
    }

    // iii) Team for a specific task
    public static void findTeamForTask(int taskId, List<Resource> resources) {
        System.out.println("Team for Task " + taskId + ":");
        for (Resource r : resources) {
            if (r.getTaskEfforts().containsKey(taskId)) {
                System.out.println("  " + r.getName() + " (" + r.getTaskEfforts().get(taskId) + "%)");
            }
        }
    }

    // iv) Total effort per resource
    public static void findTotalEffort(List<Resource> resources, List<Task> tasks) {
        for (Resource r : resources) {
            double total = 0.0;
            for (Map.Entry<Integer, Integer> e : r.getTaskEfforts().entrySet()) {
                int taskId = e.getKey();
                int percent = e.getValue();
                Task task = tasks.stream().filter(t -> t.getId() == taskId).findFirst().orElse(null);
                if (task != null) {
                    total += task.getDurationHours() * (percent / 100.0);
                }
            }
            System.out.println(r.getName() + " => " + total + " hours");
        }
    }

    public static void main(String[] args) throws Exception {
        List<Task> tasks = loadTasks("data.txt");
        List<Resource> resources = loadResources("2.txt");

        System.out.println("All tasks:");
        tasks.forEach(System.out::println);

        System.out.println("\nAll resources:");
        for (Resource r : resources) {
            System.out.println("  " + r);
        }

        System.out.println("\nProject starts and ends:");
        findCompletionTime(tasks);

        System.out.println("\nOverlapping tasks (task starts before dependency ended):");
        findOverlappingTasks(tasks);

        System.out.println("\nTeam for Task 1:");
        findTeamForTask(1, resources);

        System.out.println("\nTotal effort (hours) per resource:");
        findTotalEffort(resources, tasks);
    }
}
