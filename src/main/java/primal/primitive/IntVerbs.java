package primal.primitive;

public class IntVerbs {

	public static class CopyInt {
		public static void array(int[] from, int fromIndex, int[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

}
