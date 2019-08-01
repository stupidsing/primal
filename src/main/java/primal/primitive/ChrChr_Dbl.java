package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrChr_Dbl {

	public double apply(char c, char f);

	public default ChrChr_Dbl rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
