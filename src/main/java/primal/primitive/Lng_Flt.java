package primal.primitive;

import static primal.statics.Fail.fail;

public interface Lng_Flt {

	public float apply(long c);

	public default Lng_Flt rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
