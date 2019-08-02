package primal.primitive;

public class LngVerbs {

	public static class CopyLng {
		public static void array(long[] from, int fromIndex, long[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

}
