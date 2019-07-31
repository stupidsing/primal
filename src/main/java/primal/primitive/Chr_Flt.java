package primal.primitive;

import static primal.statics.Fail.fail;

public interface Chr_Flt {

	public float apply(char c);

	public default Chr_Flt rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
