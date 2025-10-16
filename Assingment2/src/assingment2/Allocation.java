package assingment2;

public class Allocation {
    private Resource resource;
    private Task task;
    private int effortPercent; // 0..100

    public Allocation(Resource resource, Task task, int effortPercent) {
        this.resource = resource;
        this.task = task;
        this.effortPercent = effortPercent;
    }

    public Resource getResource() { return resource; }
    public Task getTask() { return task; }
    public int getEffortPercent() { return effortPercent; }

    // Hours this allocation requires: task duration * percent / 100
    public double getEffortHours() {
        double hours = task.getDurationHours();
        return hours * (effortPercent / 100.0);
    }

    @Override
    public String toString() {
        return resource.getName() + " -> Task " + task.getId() + " (" + effortPercent + "%)";
    }
}
