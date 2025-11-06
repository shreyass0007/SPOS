import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class FIFOPageReplacement {
    public static int pageFaults(int incomingStream[], int n, int frames) {
        HashSet<Integer> s = new HashSet<>(frames); // quickly check if a page is in the frame
        Queue<Integer> queue = new LinkedList<>();  // store pages in FIFO order
        int page_faults = 0;

        for (int i = 0; i < n; i++) {
            // If frame has space
            if (s.size() < frames) {
                if (!s.contains(incomingStream[i])) {
                    s.add(incomingStream[i]);
                    page_faults++;
                    queue.add(incomingStream[i]);
                }
            }
            // If frame is full, use FIFO to remove the oldest
            else {
                if (!s.contains(incomingStream[i])) {
                    int val = queue.peek();
                    queue.poll();        // remove oldest from queue
                    s.remove(val);       // remove oldest from set
                    s.add(incomingStream[i]);
                    queue.add(incomingStream[i]);
                    page_faults++;
                }
            }
            System.out.print("Frames after page " + incomingStream[i] + ": " + queue + "\n");
        }

        return page_faults;
    }

    public static void main(String args[]) {
        int incomingStream[] = {7, 0, 1, 2, 0 , 3, 0, 4, 2, 3, 0, 3, 2, 1};
        int frames = 3;
        int len = incomingStream.length;
        int pageFaults = pageFaults(incomingStream, len, frames);
        int hit = len - pageFaults;
        System.out.println("Total page faults: " + pageFaults);
        System.out.println("Page fault ratio: " + (double) pageFaults / len);
        System.out.println("Total hits: " + hit);
        System.out.println("Hit ratio: " + (double) hit / len);
    }
}
