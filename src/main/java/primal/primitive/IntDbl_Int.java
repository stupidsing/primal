package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntDbl_Int {

	public int apply(int c, double f);

	public default IntDbl_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
