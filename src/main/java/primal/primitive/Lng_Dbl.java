package primal.primitive;

import static primal.statics.Fail.fail;

public interface Lng_Dbl {

	public double apply(long c);

	public default Lng_Dbl rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
