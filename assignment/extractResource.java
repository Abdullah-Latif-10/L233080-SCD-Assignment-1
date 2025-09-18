package assignment;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class extractResource {
    public static void main(String[] args) {
        System.out.println("Code to Get Resources and task allocation");
        List<ListResources> list = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File("2.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                String name1 = parts[0].trim();

                Map<Integer, Integer> resource1 = new HashMap<>();

                for (int i = 1; i < parts.length; i++) {
                    String res = parts[i].trim(); // e.g., "1:50"
                    String[] tokens = res.split(":");
                    if (tokens.length == 2) {
                        int taskId = Integer.parseInt(tokens[0].trim());
                        int percent = Integer.parseInt(tokens[1].trim());
                        resource1.put(taskId, percent);
                    }
                }

                ListResources item = new ListResources(name1, resource1);
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ListResources lr : list) {
            System.out.println(lr);
        }
    }
}

class ListResources {
    String name;
    Map<Integer, Integer> resource;

    public ListResources(String n, Map<Integer, Integer> r) {
        name = n;
        resource = r;
    }

    @Override
    public String toString() {
        String efforts = resource.entrySet()
                .stream()
                .map(e -> e.getKey() + ":" + e.getValue() + "%")
                .collect(Collectors.joining(", "));

        return name + " -> Resources: [" + efforts + "]";
    }
}
