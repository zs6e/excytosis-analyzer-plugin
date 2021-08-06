package ExocytosisAnalyzer.detection;

public class FindMedian {

	public static float findMedian(float[] input) {
		float[] a = new float[input.length];
    	for(int i = 0; i<a.length;i++ )
    		a[i]=input[i];
		 
	     return select1(a, a.length / 2);
	    }


	public static float select1(float[] a, int k) {
		int lo = 0, hi = a.length - 1;
		while (hi > lo) {
			int j = partition(a, lo, hi);
			if (j == k) {
				return a[k];
			} else if (j > k) {
				hi = j - 1;
			} else if (j < k) {
				lo = j + 1;
			}
		}
		return a[k];
	}


	public static float select2(float[] a, int k, int lo, int hi) {
		int j = partition(a, lo, hi);
		if (j == k) {
			return a[k];
		} else if (j > k) {
			return select2(a, k, lo, j - 1);
		} else {
			return select2(a, k, j + 1, hi);
		}
	}

	public static int partition(float[] a, int lo, int hi) {
		int i = lo, j = hi + 1;
		float v = a[lo];
		while (true) {
			while (a[++i] < v) {
				if (i == hi) {
					break;
				}
			}
			while (v < a[--j]) {
				if (j == lo) {
					break;
				}
			}
			if (i >= j) {
				break;
			}
			exch(a, i, j);
		}
		exch(a, lo, j);
		return j;
	}


	private static void exch(float[] a, int i, int j) {
		float t = a[i];
		a[i] = a[j];
		a[j] = t;
	}

}
