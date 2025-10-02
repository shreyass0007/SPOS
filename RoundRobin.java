import java.util.Scanner;

public class RoundRobin {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        int[] burstTime = new int[n];
        int[] remainingTime = new int[n];
        int[] waitingTime = new int[n];
        int[] turnaroundTime = new int[n];
        int[] completionTime = new int[n];

        System.out.println("Enter burst time for each process:");
        for (int i = 0; i < n; i++) {
            System.out.print("P" + (i + 1) + ": ");
            burstTime[i] = sc.nextInt();
            remainingTime[i] = burstTime[i];
        }

        System.out.print("Enter time quantum: ");
        int quantum = sc.nextInt();

        int time = 0; // Current time
        boolean done;

        do {
            done = true;
            for (int i = 0; i < n; i++) {
                if (remainingTime[i] > 0) {
                    done = false;

                    if (remainingTime[i] > quantum) {
                        time += quantum;
                        remainingTime[i] -= quantum;
                    } else {
                        time += remainingTime[i];
                        remainingTime[i] = 0;
                        completionTime[i] = time;
                    }
                }
            }
        } while (!done);

        // Calculate WT & TAT
        for (int i = 0; i < n; i++) {
            turnaroundTime[i] = completionTime[i];  // TAT = Completion time
            waitingTime[i] = turnaroundTime[i] - burstTime[i]; // WT = TAT - BT
        }

        // Print table
        System.out.println("\nProcess\tBurst\tWaiting\tTurnaround");
        for (int i = 0; i < n; i++) {
            System.out.println("P" + (i + 1) + "\t" + burstTime[i] + "\t" + waitingTime[i] + "\t" + turnaroundTime[i]);
        }

        // Print averages
        double avgWT = 0, avgTAT = 0;
        for (int i = 0; i < n; i++) {
            avgWT += waitingTime[i];
            avgTAT += turnaroundTime[i];
        }
        System.out.printf("\nAverage Waiting Time = %.2f", avgWT / n);
        System.out.printf("\nAverage Turnaround Time = %.2f", avgTAT / n);

        sc.close();
    }
}
