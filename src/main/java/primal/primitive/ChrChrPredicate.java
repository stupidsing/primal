package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrChrPredicate {

	public boolean test(char c, char f);

	public default ChrChrPredicate rethrow() {
		return (c, f) -> {
			try {
				return test(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f + ", ", ex);
			}
		};
	}

}
