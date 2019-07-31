package primal.primitive;

import static primal.statics.Fail.fail;

public interface Flt_Flt {

	public float apply(float c);

	public default Flt_Flt rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
