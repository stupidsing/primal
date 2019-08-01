package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrChr_Chr {

	public char apply(char c, char f);

	public default ChrChr_Chr rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
