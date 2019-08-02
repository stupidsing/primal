package primal;

public class Main implements AutoCloseable {

	public static void main(String[] args) {
		try (var main = new Main()) {
			main.run();
		}
	}

	private void run() {
	}

	@Override
	public void close() {
	}

}
