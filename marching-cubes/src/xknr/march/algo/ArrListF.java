package xknr.march.algo;

public class ArrListF 
{	
	public ArrListF(int initialCapacity) {
		assert initialCapacity >= 0;
		size = 0;
		arr = new float[initialCapacity];
	}
	
	public void add(float v) {
		ensureCapacity(size + 1);
		arr[size++] = v;
	}

	public void clear() {
		size = 0;			
	}

	public void append(ArrListF b) {
		ensureCapacity(size + b.size);
		System.arraycopy(b.arr, 0, arr, size, b.size);
		size += b.size;
	}

	private void ensureCapacity(int reqSize) {
		if (reqSize >= arr.length) {
			int newCap = Math.max(arr.length + 1, Math.max(reqSize, arr.length * 3 / 2));
			float[] temp = new float[newCap];
			System.arraycopy(arr, 0, temp, 0, arr.length);
			arr = temp;
		}
	}
	
	public float[] arr;
	public int size;
}