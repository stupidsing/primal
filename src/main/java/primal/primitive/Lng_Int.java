package primal.primitive;

import static primal.statics.Fail.fail;

public interface Lng_Int {

	public int apply(long c);

	public default Lng_Int rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
