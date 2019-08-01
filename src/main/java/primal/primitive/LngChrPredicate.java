package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngChrPredicate {

	public boolean test(long c, char f);

	public default LngChrPredicate rethrow() {
		return (c, f) -> {
			try {
				return test(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
