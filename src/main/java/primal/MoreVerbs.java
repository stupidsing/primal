package primal;

import static java.lang.Math.min;
import static primal.statics.Rethrow.ex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import primal.Nouns.Buffer;
import primal.Nouns.Utf8;
import primal.adt.Pair;
import primal.primitive.adt.Bytes;
import primal.puller.Puller;

public class MoreVerbs {

	public static class Pull {
		public static Puller<Bytes> from(String data) {
			return from(new ByteArrayInputStream(data.getBytes(Utf8.charset)));
		}

		public static Puller<Bytes> from(InputStream is) {
			var bis = new BufferedInputStream(is);
			return Puller.of(() -> {
				var bs = new byte[Buffer.size];
				var nBytesRead = ex(() -> bis.read(bs));
				return 0 <= nBytesRead ? Bytes.of(bs, 0, nBytesRead) : null;
			}).closeAtEnd(bis).closeAtEnd(is);
		}
	}

	public static class Split {
		public static <T> List<List<T>> chunk(List<T> list, int n) {
			var s = 0;
			var subsets = new ArrayList<List<T>>();
			while (s < list.size()) {
				int s1 = min(s + n, list.size());
				subsets.add(list.subList(s, s1));
				s = s1;
			}
			return subsets;
		}

		public static Pair<String, String> string(String s, String delimiter) {
			var pos = s.indexOf(delimiter);
			return 0 <= pos ? Pair.of(s.substring(0, pos).trim(), s.substring(pos + delimiter.length()).trim()) : null;
		}

		public static Pair<String, String> strl(String s, String delimiter) {
			var pair = string(s, delimiter);
			return pair != null ? pair : Pair.of(s.trim(), "");
		}

		public static Pair<String, String> strr(String s, String delimiter) {
			var pair = string(s, delimiter);
			return pair != null ? pair : Pair.of("", s.trim());
		}
	}

}
