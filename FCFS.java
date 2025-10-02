import java.util.*;

class Process {
    int id;
    int arrival;
    int burst;
    int completion;
    int waiting;
    int turnaround;
    
    Process(int id, int arrival, int burst) {
        this.id = id;
        this.arrival = arrival;
        this.burst = burst;
    }
}

public class SimpleScheduler {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        Process[] processes = new Process[n];
       
        for (int i = 0; i < n; i++) {
            System.out.print("Enter process ID (integer) for process " + (i + 1) + ": ");
            int id = sc.nextInt();
            System.out.print("Enter arrival time for process " + id + ": ");
            int arrival = sc.nextInt();
            System.out.print("Enter burst time for process " + id + ": ");
            int burst = sc.nextInt();
            processes[i] = new Process(id, arrival, burst);
        }
        
        // Sort processes by arrival time
        Arrays.sort(processes, Comparator.comparingInt(p -> p.arrival));
        
        int time = 0;
        int totalTurnaround = 0;
        int totalWaiting = 0;
        
        System.out.println("\nProcessID\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        
        for (Process p : processes) {
            if (time < p.arrival) {
                time = p.arrival;
            }
            time += p.burst;
            p.completion = time;
            p.turnaround = p.completion - p.arrival;
            p.waiting = p.turnaround - p.burst;
            totalTurnaround += p.turnaround;
            totalWaiting += p.waiting;
            
            System.out.println(p.id + "\t\t" + p.arrival + "\t" + p.burst + "\t" + 
                             p.completion + "\t\t" + p.turnaround + "\t\t" + p.waiting);
        }
        
        double avgTurnaround = totalTurnaround / (double) n;
        double avgWaiting = totalWaiting / (double) n;
        
        System.out.printf("\nAverage Turnaround Time: %.2f\n", avgTurnaround);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaiting);
        
        sc.close();
    }
}
