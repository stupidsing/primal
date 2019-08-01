package primal.primitive;

import static primal.statics.Fail.fail;

public interface ChrChrSink {

	public void sink2(char c, char f);

	public default ChrChrSink rethrow() {
		return (k, v) -> {
			try {
				sink2(k, v);
			} catch (Exception ex) {
				fail("for key " + k, ex);
			}
		};
	}

}
