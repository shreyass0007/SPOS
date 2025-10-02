import java.util.*;

class Process {
    int id, arrivalTime, burstTime, priority;
    int completionTime, turnaroundTime, waitingTime;
    boolean isCompleted = false;

    Process(int id, int arrivalTime, int burstTime, int priority) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

public class PrioritySchedulingFull {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input Section
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];

        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for Process " + (i + 1));
            System.out.print("Arrival Time: ");
            int at = sc.nextInt();
            System.out.print("Burst Time: ");
            int bt = sc.nextInt();
            System.out.print("Priority (lower number = higher priority): ");
            int pr = sc.nextInt();
            processes[i] = new Process(i + 1, at, bt, pr);
        }

        // Run scheduling
        schedule(processes);

        // Print results
        printResults(processes);
    }

    private static void schedule(Process[] processes) {
        int n = processes.length;
        int currentTime = 0, completed = 0;
        double totalTAT = 0, totalWT = 0;

        List<String> ganttChart = new ArrayList<>();

        while (completed != n) {
            int idx = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (processes[i].arrivalTime <= currentTime && !processes[i].isCompleted) {
                    if (processes[i].priority < highestPriority) {
                        highestPriority = processes[i].priority;
                        idx = i;
                    } else if (processes[i].priority == highestPriority) {
                        if (processes[i].arrivalTime < processes[idx].arrivalTime) {
                            idx = i;
                        }
                    }
                }
            }

            if (idx != -1) {
                Process p = processes[idx];
                p.completionTime = currentTime + p.burstTime;
                p.turnaroundTime = p.completionTime - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                p.isCompleted = true;

                totalTAT += p.turnaroundTime;
                totalWT += p.waitingTime;
                ganttChart.add("P" + p.id + " [" + currentTime + "-" + p.completionTime + "]");

                currentTime = p.completionTime;
                completed++;
            } else {
                currentTime++; // CPU is idle
            }
        }

        // Store averages in static vars for printing
        avgTAT = totalTAT / n;
        avgWT = totalWT / n;
        chart = ganttChart;
    }

    // Variables for storing results
    private static double avgTAT, avgWT;
    private static List<String> chart;

    private static void printResults(Process[] processes) {
        System.out.println("\nProcess\tAT\tBT\tPri\tCT\tTAT\tWT");
        for (Process p : processes) {
            System.out.printf("P%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                    p.id, p.arrivalTime, p.burstTime, p.priority,
                    p.completionTime, p.turnaroundTime, p.waitingTime);
        }

        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTAT);
        System.out.printf("Average Waiting Time: %.2f\n", avgWT);

        // Gantt Chart
        System.out.println("\nGantt Chart:");
        for (String s : chart) {
            System.out.print("| " + s + " ");
        }
        System.out.println("|");
    }
}
