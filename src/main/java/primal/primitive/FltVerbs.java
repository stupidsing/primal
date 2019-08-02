package primal.primitive;

public class FltVerbs {

	public static class CopyFlt {
		public static void array(float[] from, int fromIndex, float[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

}
