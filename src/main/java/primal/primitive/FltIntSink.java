package primal.primitive;

import static primal.statics.Fail.fail;

public interface FltIntSink {

	public void sink2(float c, int f);

	public default FltIntSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
