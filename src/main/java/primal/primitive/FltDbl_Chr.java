package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltDbl_Chr {

	public char apply(float c, double f);

	public default FltDbl_Chr rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
