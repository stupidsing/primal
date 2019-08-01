package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrDbl_Int {

	public int apply(char c, double f);

	public default ChrDbl_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
