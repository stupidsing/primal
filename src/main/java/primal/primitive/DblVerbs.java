package primal.primitive;

public class DblVerbs {

	public static class ConcatDbl {
		public static double[] arrays(double[]... array) {
			var length = 0;
			for (var fs : array)
				length += fs.length;
			var fs1 = new double[length];
			var i = 0;
			for (var fs : array) {
				var length_ = fs.length;
				CopyDbl.array(fs, 0, fs1, i, length_);
				i += length_;
			}
			return fs1;
		}
	}

	public static class CopyDbl {
		public static void array(double[] from, int fromIndex, double[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class NewDbl {
		public static double[] array(int length, Int_Dbl f) {
			var cs = new double[length];
			for (var i = 0; i < length; i++)
				cs[i] = f.apply(i);
			return cs;
		}
	}

}
