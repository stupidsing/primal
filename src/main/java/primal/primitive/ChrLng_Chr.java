package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrLng_Chr {

	public char apply(char c, long f);

	public default ChrLng_Chr rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
