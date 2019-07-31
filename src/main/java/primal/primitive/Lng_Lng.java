package primal.primitive;

import static primal.statics.Fail.fail;

public interface Lng_Lng {

	public long apply(long c);

	public default Lng_Lng rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
