package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntChr_Flt {

	public float apply(int c, char f);

	public default IntChr_Flt rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
