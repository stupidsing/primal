package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntChr_Lng {

	public long apply(int c, char f);

	public default IntChr_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
