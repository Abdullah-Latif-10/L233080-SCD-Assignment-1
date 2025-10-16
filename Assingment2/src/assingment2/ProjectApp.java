package assingment2;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ProjectApp {
    public static void main(String[] args) {
        try {
            String tasksFile = resolveFile("data.txt");
            String resourcesFile = resolveFile("2.txt");

            if (tasksFile == null) throw new FileNotFoundException("Could not locate 'data.txt'. Expected it in the project root or on the classpath.");
            if (resourcesFile == null) throw new FileNotFoundException("Could not locate '2.txt'. Expected it in the project root or on the classpath.");

            List<Task> tasks = ProjectLoader.loadTasks(tasksFile);
            List<Resource> resources = ProjectLoader.loadResources(resourcesFile, tasks);

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

    // Try to find the named file in the current working directory and up the directory tree.
    // If not found, try the classpath and copy resource to a temp file.
    private static String resolveFile(String name) {
        // 1) direct path
        File f = new File(name);
        if (f.exists()) return f.getPath();

        // 2) search upward from user.dir for the file (limit depth to avoid long loops)
        Path dir = Paths.get(System.getProperty("user.dir"));
        for (int i = 0; i < 8 && dir != null; i++) {
            Path candidate = dir.resolve(name);
            if (Files.exists(candidate)) return candidate.toAbsolutePath().toString();
            dir = dir.getParent();
        }

        // 3) try classpath resource
        try (InputStream is = ProjectApp.class.getClassLoader().getResourceAsStream(name)) {
            if (is != null) {
                Path tmp = Files.createTempFile("project-data-", "-" + name.replaceAll("[^a-zA-Z0-9._-]", ""));
                Files.copy(is, tmp, StandardCopyOption.REPLACE_EXISTING);
                tmp.toFile().deleteOnExit();
                return tmp.toAbsolutePath().toString();
            }
        } catch (IOException ex) {
            // ignore here; return null below
        }

        return null;
    }
}
