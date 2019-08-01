package primal.primitive;

import static primal.statics.Fail.fail;

public interface LngLngSink {

	public void sink2(long c, long f);

	public default LngLngSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
