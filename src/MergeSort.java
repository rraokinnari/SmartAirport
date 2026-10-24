import java.util.Comparator;

/**
 * Generic, stable Merge Sort (top-down, divide and conquer).
 * Time: O(n log n) in every case. Extra space: O(n).
 */
public class MergeSort {

    private MergeSort() { }

    public static <T> void sort(T[] a, Comparator<? super T> cmp) {
        if (a.length < 2) return;
        T[] temp = a.clone();
        sort(a, temp, 0, a.length - 1, cmp);
    }

    private static <T> void sort(T[] a, T[] temp, int lo, int hi, Comparator<? super T> cmp) {
        if (lo >= hi) return;
        int mid = (lo + hi) >>> 1;
        sort(a, temp, lo, mid, cmp);       // sort left half
        sort(a, temp, mid + 1, hi, cmp);   // sort right half
        merge(a, temp, lo, mid, hi, cmp);  // merge the two sorted halves
    }

    private static <T> void merge(T[] a, T[] temp, int lo, int mid, int hi, Comparator<? super T> cmp) {
        for (int k = lo; k <= hi; k++) temp[k] = a[k];

        int i = lo;       // pointer into left half
        int j = mid + 1;  // pointer into right half
        for (int k = lo; k <= hi; k++) {
            if (i > mid)                              a[k] = temp[j++];
            else if (j > hi)                          a[k] = temp[i++];
            else if (cmp.compare(temp[j], temp[i]) < 0) a[k] = temp[j++];
            else                                      a[k] = temp[i++]; // ties keep original order (stable)
        }
    }
}
