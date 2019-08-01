package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltDbl_Lng {

	public long apply(float c, double f);

	public default FltDbl_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
