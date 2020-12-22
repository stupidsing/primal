package primal.primitive;

public class IntVerbs {

	public static class ConcatInt {
		public static int[] arrays(int[]... array) {
			var length = 0;
			for (var fs : array)
				length += fs.length;
			var fs1 = new int[length];
			var i = 0;
			for (var fs : array) {
				var length_ = fs.length;
				CopyInt.array(fs, 0, fs1, i, length_);
				i += length_;
			}
			return fs1;
		}
	}

	public static class CopyInt {
		public static void array(int[] from, int fromIndex, int[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class ToInt {
		public static int[] array(int length, Int_Int f) {
			var cs = new int[length];
			for (var i = 0; i < length; i++)
				cs[i] = f.apply(i);
			return cs;
		}
	}

}
