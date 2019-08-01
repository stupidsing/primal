package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntChr_Dbl {

	public double apply(int c, char f);

	public default IntChr_Dbl rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
