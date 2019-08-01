package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblInt_Dbl {

	public double apply(double c, int f);

	public default DblInt_Dbl rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
