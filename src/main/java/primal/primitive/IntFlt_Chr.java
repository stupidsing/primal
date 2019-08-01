package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntFlt_Chr {

	public char apply(int c, float f);

	public default IntFlt_Chr rethrow() {
		return (c, f) -> {
			try {
				return apply(c, f);
			} catch (Exception ex) {
				return fail("for " + c + ":" + f, ex);
			}
		};

	}
}
