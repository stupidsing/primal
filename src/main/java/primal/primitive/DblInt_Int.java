package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblInt_Int {

	public int apply(double c, int f);

	public default DblInt_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
