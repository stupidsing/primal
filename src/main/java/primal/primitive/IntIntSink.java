package primal.primitive;

import static primal.statics.Fail.fail;

public interface IntIntSink {

	public void sink2(int c, int f);

	public default IntIntSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
