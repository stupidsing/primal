package primal.primitive;

import static primal.statics.Fail.fail;

public interface Chr_Lng {

	public long apply(char c);

	public default Chr_Lng rethrow() {
		return t -> {
			try {
				return apply(t);
			} catch (Exception ex) {
				return fail("for key " + t, ex);
			}
		};
	}

}
