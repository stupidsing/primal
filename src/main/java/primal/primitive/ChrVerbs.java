package primal.primitive;

public class ChrVerbs {

	public static class CopyChr {
		public static void array(char[] from, int fromIndex, char[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

}
