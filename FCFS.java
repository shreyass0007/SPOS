import java.util.Scanner;

public class SimpleScheduler {
    
    static class ProcessInfo {
        int processId;
        int arrivalTime;
        int burstTime;
        int completionTime;
        int turnaroundTime;
        int waitingTime;
        
        ProcessInfo(int pid, int at, int bt) {
            this.processId = pid;
            this.arrivalTime = at;
            this.burstTime = bt;
        }
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter number of processes: ");
        int numProcesses = scanner.nextInt();
        
        ProcessInfo[] processList = new ProcessInfo[numProcesses];
        
        // Input process details
        for (int i = 0; i < numProcesses; i++) {
            System.out.print("Enter process ID for process " + (i + 1) + ": ");
            int pid = scanner.nextInt();
            
            System.out.print("Enter arrival time for process " + pid + ": ");
            int at = scanner.nextInt();
            
            System.out.print("Enter burst time for process " + pid + ": ");
            int bt = scanner.nextInt();
            
            processList[i] = new ProcessInfo(pid, at, bt);
        }
        
        // Sort by arrival time (bubble sort)
        for (int i = 0; i < numProcesses - 1; i++) {
            for (int j = 0; j < numProcesses - i - 1; j++) {
                if (processList[j].arrivalTime > processList[j + 1].arrivalTime) {
                    ProcessInfo temp = processList[j];
                    processList[j] = processList[j + 1];
                    processList[j + 1] = temp;
                }
            }
        }
        
        // Calculate times
        int currentTime = 0;
        int totalTurnaround = 0;
        int totalWaiting = 0;
        
        for (int i = 0; i < numProcesses; i++) {
            ProcessInfo proc = processList[i];
            
            if (currentTime < proc.arrivalTime) {
                currentTime = proc.arrivalTime;
            }
            
            currentTime += proc.burstTime;
            proc.completionTime = currentTime;
            proc.turnaroundTime = proc.completionTime - proc.arrivalTime;
            proc.waitingTime = proc.turnaroundTime - proc.burstTime;
            
            totalTurnaround += proc.turnaroundTime;
            totalWaiting += proc.waitingTime;
        }
        
        // Display results
        System.out.println("\n================================================================================");
        System.out.println("                        FCFS SCHEDULING RESULTS");
        System.out.println("================================================================================");
        System.out.println("PID\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        System.out.println("--------------------------------------------------------------------------------");
        
        for (int i = 0; i < numProcesses; i++) {
            ProcessInfo proc = processList[i];
            System.out.println(proc.processId + "\t" + proc.arrivalTime + "\t" + 
                             proc.burstTime + "\t" + proc.completionTime + "\t\t" + 
                             proc.turnaroundTime + "\t\t" + proc.waitingTime);
        }
        
        System.out.println("================================================================================");
        
        double avgTurnaround = (double) totalTurnaround / numProcesses;
        double avgWaiting = (double) totalWaiting / numProcesses;
        
        System.out.println("\nAverage Turnaround Time: " + String.format("%.2f", avgTurnaround));
        System.out.println("Average Waiting Time: " + String.format("%.2f", avgWaiting));
        
        scanner.close();
    }
}
