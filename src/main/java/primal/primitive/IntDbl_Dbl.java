package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntDbl_Dbl {

	public double apply(int c, double f);

	public default IntDbl_Dbl rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
