package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblLng_Int {

	public int apply(double c, long f);

	public default DblLng_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
