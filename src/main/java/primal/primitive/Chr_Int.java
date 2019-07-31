package primal.primitive;

import static primal.statics.Fail.fail;

public interface Chr_Int {

	public int apply(char c);

	public default Chr_Int rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
