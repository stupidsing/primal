package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrDbl_Lng {

	public long apply(char c, double f);

	public default ChrDbl_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
