package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntInt_Lng {

	public long apply(int c, int f);

	public default IntInt_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
