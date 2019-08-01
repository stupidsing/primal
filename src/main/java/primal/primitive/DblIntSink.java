package primal.primitive;

import static primal.statics.Fail.fail;

public interface DblIntSink {

	public void sink2(double c, int f);

	public default DblIntSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
