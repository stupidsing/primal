package primal.primitive;

public class LngVerbs {

	public static class ConcatLng {
		public static long[] arrays(long[]... array) {
			var length = 0;
			for (var fs : array)
				length += fs.length;
			var fs1 = new long[length];
			var i = 0;
			for (var fs : array) {
				var length_ = fs.length;
				CopyLng.array(fs, 0, fs1, i, length_);
				i += length_;
			}
			return fs1;
		}
	}

	public static class CopyLng {
		public static void array(long[] from, int fromIndex, long[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class ToLng {
		public static long[] array(int length, Int_Lng f) {
			var cs = new long[length];
			for (var i = 0; i < length; i++)
				cs[i] = f.apply(i);
			return cs;
		}
	}

}
