package primal.primitive;

import static primal.statics.Fail.fail;

public interface Chr_Chr {

	public char apply(char c);

	public default Chr_Chr rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
