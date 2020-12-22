package primal.primitive;

public class FltVerbs {

	public static class ConcatFlt {
		public static float[] arrays(float[]... array) {
			var length = 0;
			for (var fs : array)
				length += fs.length;
			var fs1 = new float[length];
			var i = 0;
			for (var fs : array) {
				var length_ = fs.length;
				CopyFlt.array(fs, 0, fs1, i, length_);
				i += length_;
			}
			return fs1;
		}
	}

	public static class CopyFlt {
		public static void array(float[] from, int fromIndex, float[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class NewFlt {
		public static float[] array(int length, Int_Flt f) {
			var cs = new float[length];
			for (var i = 0; i < length; i++)
				cs[i] = f.apply(i);
			return cs;
		}
	}

}
