package primal.primitive;

import static primal.statics.Fail.fail;

public interface Lng_Chr {

	public char apply(long c);

	public default Lng_Chr rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
