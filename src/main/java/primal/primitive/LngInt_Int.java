package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngInt_Int {

	public int apply(long c, int f);

	public default LngInt_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
