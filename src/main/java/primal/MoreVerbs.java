package primal;

import static java.lang.Math.min;
import static primal.statics.Fail.fail;
import static primal.statics.Rethrow.ex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import primal.Nouns.Buffer;
import primal.Nouns.Utf8;
import primal.adt.Pair;
import primal.fp.Funs.Source;
import primal.primitive.adt.Bytes;
import primal.primitive.adt.Bytes.BytesBuilder;
import primal.primitive.adt.Chars;
import primal.primitive.fp.AsChr;
import primal.puller.Puller;

public class MoreVerbs {

	public static class Decode {
		public static Puller<Chars> utf8(Puller<Bytes> bytesPuller) {
			var source = bytesPuller.source();

			return Puller.of(new Source<>() {
				private BytesBuilder bb = new BytesBuilder();

				public Chars g() {
					Chars chars;
					while ((chars = decode()).size() == 0) {
						var bytes = source.g();
						if (bytes != null)
							bb.append(bytes);
						else if (bb.size() == 0)
							return null;
						else
							return fail();
					}
					return chars;
				}

				private Chars decode() {
					var bytes = bb.toBytes();

					return AsChr.build(cb -> {
						var s = 0;

						while (s < bytes.size()) {
							var b0 = Byte.toUnsignedInt(bytes.get(s++));
							int ch, e;
							if (b0 < 0x80) {
								ch = b0;
								e = s;
							} else if (b0 < 0xE0) {
								ch = b0 & 0x1F;
								e = s + 1;
							} else if (b0 < 0xF0) {
								ch = b0 & 0x0F;
								e = s + 2;
							} else if (b0 < 0xF8) {
								ch = b0 & 0x07;
								e = s + 3;
							} else if (b0 < 0xFC) {
								ch = b0 & 0x03;
								e = s + 4;
							} else if (b0 < 0xFE) {
								ch = b0 & 0x01;
								e = s + 5;
							} else
								throw new RuntimeException();
							if (e <= bytes.size()) {
								while (s < e) {
									var b = Byte.toUnsignedInt(bytes.get(s++));
									if ((b & 0xC0) == 0x80)
										ch = (ch << 6) + (b & 0x3F);
									else
										fail();
								}
								cb.append((char) ch);
							} else
								break;
						}

						bb = new BytesBuilder();
						bb.append(bytes.range(s));
					});
				}
			});
		}
	}

	public static class Encode {
		public static Puller<Bytes> encode(Puller<Chars> charsPuller) {
			var source = charsPuller.source();

			return Puller.of(new Source<>() {
				public Bytes g() {
					var chars = source.g();
					if (chars != null) {
						var bb = new BytesBuilder();
						for (var i = 0; i < chars.size(); i++) {
							var ch = chars.get(i);
							if (ch < 0x80)
								bb.append((byte) ch);
							else if (ch < 0x800) {
								bb.append((byte) (0xC0 + ((ch >> 6) & 0x1F)));
								bb.append((byte) (0x80 + ((ch >> 0) & 0x3F)));
							} else if (ch < 0x10000) {
								bb.append((byte) (0xE0 + ((ch >> 12) & 0x0F)));
								bb.append((byte) (0x80 + ((ch >> 6) & 0x3F)));
								bb.append((byte) (0x80 + ((ch >> 0) & 0x3F)));
							} else {
								bb.append((byte) (0xF0 + ((ch >> 18) & 0x07)));
								bb.append((byte) (0x80 + ((ch >> 12) & 0x3F)));
								bb.append((byte) (0x80 + ((ch >> 6) & 0x3F)));
								bb.append((byte) (0x80 + ((ch >> 0) & 0x3F)));
							}
						}
						return bb.toBytes();
					} else
						return null;
				}
			});
		}
	}

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
