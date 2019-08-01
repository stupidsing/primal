package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblDbl_Lng {

	public long apply(double c, double f);

	public default DblDbl_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
