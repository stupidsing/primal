package primal;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Nouns {

	public static class Buffer {
		public static int size = 4096;
	}

	public static class Dt {
		public static DateTimeFormatter ymd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
		public static DateTimeFormatter ymdhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
	}

	public static class Tmp {
		public static Path root = Paths.get("/tmp");

		public static Path path(String path) {
			return root.resolve(path);
		}
	}

	public static class Utf8 {
		public static Charset charset = StandardCharsets.UTF_8;
	}

}
