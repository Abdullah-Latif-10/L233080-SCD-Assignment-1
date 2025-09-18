import java.util.List;

public class ProjectApp {
    public static void main(String[] args) {
        try {
            List<Task> tasks = ProjectLoader.loadTasks("data.txt");
            List<Resource> resources = ProjectLoader.loadResources("2.txt", tasks);

            Project project = new Project();
            for (Task t : tasks) project.addTask(t);
            for (Resource r : resources) project.addResource(r);

            // i) completion & duration
            project.printCompletionAndDuration();

            // ii) overlapping tasks
            project.printOverlappingTasks();

            // iii) team for a specific task (example: ask for task 1)
            project.printTeamForTask(1);

            // iv) total effort per resource
            project.printTotalEffortPerResource();

            // if you want to list everything:
            System.out.println("\nAll tasks:");
            for (Task t : project.getTasks()) System.out.println("  " + t);
            System.out.println("\nAll resources:");
            for (Resource r : project.getResources()) System.out.println("  " + r);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
