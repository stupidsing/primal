package primal.primitive;

public class ChrVerbs {

	public static class ConcatChr {
		public static char[] arrays(char[]... array) {
			var length = 0;
			for (var fs : array)
				length += fs.length;
			var fs1 = new char[length];
			var i = 0;
			for (var fs : array) {
				var length_ = fs.length;
				CopyChr.array(fs, 0, fs1, i, length_);
				i += length_;
			}
			return fs1;
		}
	}

	public static class CopyChr {
		public static void array(char[] from, int fromIndex, char[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class NewChr {
		public static char[] array(int length, Int_Chr f) {
			var cs = new char[length];
			for (var i = 0; i < length; i++)
				cs[i] = f.apply(i);
			return cs;
		}
	}

}
