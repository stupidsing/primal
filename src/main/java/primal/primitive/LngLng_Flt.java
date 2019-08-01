package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngLng_Flt {

	public float apply(long c, long f);

	public default LngLng_Flt rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
