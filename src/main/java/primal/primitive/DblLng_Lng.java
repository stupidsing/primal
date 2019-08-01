package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblLng_Lng {

	public long apply(double c, long f);

	public default DblLng_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
