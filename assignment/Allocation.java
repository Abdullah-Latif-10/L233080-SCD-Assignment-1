// package assignment;

// public class Allocation {
//     private Resources resource;
//     private Task<?> task;
//     private int effortPercent; // e.g., 50 means 50%

//     public Allocation(Resources resource, Task<?> task, int effortPercent) {
//         this.resource = resource;
//         this.task = task;
//         this.effortPercent = effortPercent;
//     }

//     public long getEffortHours() {
//         return (task.getDurationHours() * effortPercent) / 100;
//     }

//     @Override
//     public String toString() {
//         return resource.getName() + " -> Task " + task.id + " (" + effortPercent + "%)";
//     }
// }
