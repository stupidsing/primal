package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngDbl_Lng {

	public long apply(long c, double f);

	public default LngDbl_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
