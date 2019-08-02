package primal.primitive;

public class DblVerbs {

	public static class CopyDbl {
		public static void array(double[] from, int fromIndex, double[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

}
