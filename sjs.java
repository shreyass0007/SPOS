import java.util.*;

class Process {
    int id;
    int arrival;
    int burst;
    int completion;
    int waiting;
    int turnaround;
    boolean isCompleted = false;
    
    Process(int id, int arrival, int burst) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
    }
}

public class SimpleScheduler1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        
        Process[] processes = new Process[n];
        
        // Input processes
        for (int i = 0; i < n; i++) {
            System.out.print("Enter process ID (integer) for process " + (i + 1) + ": ");
            int id = sc.nextInt();
            System.out.print("Enter arrival time for process " + id + ": ");
            int arrival = sc.nextInt();
            System.out.print("Enter burst time for process " + id + ": ");
            int burst = sc.nextInt();
            processes[i] = new Process(id, arrival, burst);
        }
        
        // SJF Scheduling
        int time = 0, completed = 0;
        int totalTurnaround = 0, totalWaiting = 0;
        
        while (completed != n) {
            int idx = -1;
            int minBurst = Integer.MAX_VALUE;
            
            // Find process with shortest burst time that has arrived
            for (int i = 0; i < n; i++) {
                if (processes[i].arrival <= time && !processes[i].isCompleted) {
                    if (processes[i].burst < minBurst) {
                        minBurst = processes[i].burst;
                        idx = i;
                    } else if (processes[i].burst == minBurst) {
                        // Tie-breaker: lower arrival time
                        if (processes[i].arrival < processes[idx].arrival) {
                            idx = i;
                        }
                    }
                }
            }
            
            if (idx != -1) {
                Process p = processes[idx];
                p.completion = time + p.burst;
                p.turnaround = p.completion - p.arrival;
                p.waiting = p.turnaround - p.burst;
                p.isCompleted = true;
                time = p.completion;
                completed++;
                totalTurnaround += p.turnaround;
                totalWaiting += p.waiting;
            } else {
                time++; // CPU idle
            }
        }
        
        // Sort processes by ID before printing
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));
        
        // Print table
        System.out.printf("\n%-10s %-10s %-10s %-15s %-15s %-10s\n", 
                          "ProcessID", "Arrival", "Burst", "Completion", "Turnaround", "Waiting");
        for (Process p : processes) {
            System.out.printf("%-10d %-10d %-10d %-15d %-15d %-10d\n",
                              p.id, p.arrival, p.burst, p.completion, p.turnaround, p.waiting);
        }
        
        // Print averages
        double avgTurnaround = totalTurnaround / (double) n;
        double avgWaiting = totalWaiting / (double) n;
        
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaround);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaiting);
        
        sc.close();
    }
}
