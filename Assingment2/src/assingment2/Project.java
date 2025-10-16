package assingment2;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project aggregates tasks and resources, and provides analysis methods.
 */
public class Project {
    private List<Task> tasks = new ArrayList<>();
    private List<Resource> resources = new ArrayList<>();

    public Project() {}

    public void addTask(Task t) { tasks.add(t); }
    public void addResource(Resource r) { resources.add(r); }

    public List<Task> getTasks() { return tasks; }
    public List<Resource> getResources() { return resources; }

    // i) Project completion time & duration (hours)
    public void printCompletionAndDuration() {
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Task t : tasks) {
            LocalDateTime s = t.getStart();
            LocalDateTime e = t.getEnd();
            if (s != null) {
                if (earliest == null || s.isBefore(earliest)) earliest = s;
            }
            if (e != null) {
                if (latest == null || e.isAfter(latest)) latest = e;
            }
        }

        if (earliest == null || latest == null) {
            System.out.println("Not enough datetime information to compute project duration.");
            return;
        }

        long hours = java.time.Duration.between(earliest, latest).toHours();

        System.out.println("Project starts: " + earliest);
        System.out.println("Project ends:   " + latest);
        System.out.println("Project duration: " + hours + " hours (" + (hours/24) + " days approx.)");
    }

    // ii) Highlight overlapping tasks (task starts before its dependency ends and they have common timeframe)
    public void printOverlappingTasks() {
        System.out.println("\nOverlapping tasks (task starts before dependency ended):");
        // Build a map-style lookup: but keep simple loops
        for (Task t : tasks) {
            List<Integer> deps = t.getDependencies();
            if (deps == null || deps.isEmpty()) continue;

            LocalDateTime tStart = t.getStart();
            LocalDateTime tEnd = t.getEnd();
            if (tStart == null || tEnd == null) continue;

            for (Integer depId : deps) {
                Task dep = findTaskById(depId);
                if (dep == null) continue;
                LocalDateTime depStart = dep.getStart();
                LocalDateTime depEnd = dep.getEnd();
                if (depStart == null || depEnd == null) continue;

                // overlap if their intervals intersect
                boolean intersects = tStart.isBefore(depEnd) && depStart.isBefore(tEnd);

                // specifically check "task starts before dependency finished" per requirement
                boolean startsBeforeDepFinished = tStart.isBefore(depEnd);

                if (intersects && startsBeforeDepFinished) {
                    System.out.println(" - Task " + t.getId() + " (" + t.getTitle() + ") overlaps with dependency Task "
                                       + dep.getId() + " (" + dep.getTitle() + ")");
                }
            }
        }
    }

    // iii) Find resources (team) for a specific task
    public void printTeamForTask(int taskId) {
        System.out.println("\nTeam for Task " + taskId + ":");
        boolean found = false;
        for (Resource r : resources) {
            for (Allocation a : r.getAllocations()) {
                if (a.getTask().getId() == taskId) {
                    System.out.println(" - " + r.getName() + " (" + a.getEffortPercent() + "%)");
                    found = true;
                }
            }
        }
        if (!found) System.out.println(" No resources assigned.");
    }

    // iv) Total effort (hours) required by each resource on the project
    public void printTotalEffortPerResource() {
        System.out.println("\nTotal effort (hours) per resource:");
        for (Resource r : resources) {
            double total = 0.0;
            for (Allocation a : r.getAllocations()) {
                total += a.getEffortHours();
            }
            System.out.println(" - " + r.getName() + " => " + total + " hours");
        }
    }

    // helper
    private Task findTaskById(int id) {
        for (Task t : tasks) {
            if (t.getId() == id) return t;
        }
        return null;
    }
}
