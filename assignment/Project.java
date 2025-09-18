package assignment;
import java.io.*;
import java.util.*;

public class Project {
    public static void main(String[] args) {
        System.out.println("Question 1");
        List<Task<Integer>> tasks = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("data.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");

                int id = Integer.parseInt(data[0].trim());
                String title = data[1].trim();
                String startTime = data[2].trim();   // still a string
                String endTime = data[3].trim();     // still a string

                List<Integer> dependencies = new ArrayList<>();
                for (int i = 4; i < data.length; i++) {
                    String dep = data[i].trim();
                    if (!dep.isEmpty()) {
                        dependencies.add(Integer.parseInt(dep));
                    }
                }

                // ✅ Task constructor now handles conversion to LocalDateTime
                Task<Integer> task = new Task<>(id, title, startTime, endTime, dependencies);
                tasks.add(task);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Task<Integer> task : tasks) {
            System.out.println(task);
        }
    }
}
