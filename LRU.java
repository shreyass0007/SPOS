import java.util.*;

public class LRUPageReplacement {
    public static int pageFaults(int[] pages, int n, int frames) {
        Set<Integer> set = new HashSet<>(frames);
        Map<Integer, Integer> indexes = new HashMap<>();
        int pageFaults = 0;

        for (int i = 0; i < n; i++) {
            if (set.size() < frames) {
                if (!set.contains(pages[i])) {
                    set.add(pages[i]);
                    pageFaults++;
                }
                indexes.put(pages[i], i);
            } else {
                if (!set.contains(pages[i])) {
                    int lru = Integer.MAX_VALUE, val = Integer.MIN_VALUE;
                    for (int page : set) {
                        if (indexes.get(page) < lru) {
                            lru = indexes.get(page);
                            val = page;
                        }
                    }
                    set.remove(val);
                    set.add(pages[i]);
                    pageFaults++;
                }
                indexes.put(pages[i], i);
            }
        }
        return pageFaults;
    }

    public static void main(String[] args) {
        int[] pages = {7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2};
        int frames = 3;
        int n = pages.length;
        int faults = pageFaults(pages, n, frames);
        System.out.println("LRU - Total Page Faults: " + faults);
    }
}
