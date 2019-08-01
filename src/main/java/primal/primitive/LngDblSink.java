package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngDblSink {

	public void sink2(long c, double f);

	public default LngDblSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
