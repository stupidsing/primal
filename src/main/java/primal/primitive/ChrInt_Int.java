package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrInt_Int {

	public int apply(char c, int f);

	public default ChrInt_Int rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
