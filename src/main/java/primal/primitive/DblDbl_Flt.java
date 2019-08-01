package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblDbl_Flt {

	public float apply(double c, double f);

	public default DblDbl_Flt rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
