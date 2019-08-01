package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntDblPredicate {

	public boolean test(int c, double f);

	public default IntDblPredicate rethrow() {
		return (c, f) -> {
			try {
				return test(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
