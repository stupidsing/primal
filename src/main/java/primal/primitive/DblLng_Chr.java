package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblLng_Chr {

	public char apply(double c, long f);

	public default DblLng_Chr rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
