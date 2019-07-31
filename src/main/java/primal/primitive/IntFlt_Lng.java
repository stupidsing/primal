package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntFlt_Lng {

	public long apply(int c, float f);

	public default IntFlt_Lng rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
