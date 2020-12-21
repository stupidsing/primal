package primal;

import static java.lang.Math.min;
import static primal.statics.Fail.fail;
import static primal.statics.Rethrow.ex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import primal.Nouns.Buffer;
import primal.Nouns.Utf8;
import primal.Verbs.ReadLine;
import primal.adt.FixieArray;
import primal.adt.Pair;
import primal.adt.map.ListMultimap;
import primal.fp.FunUtil;
import primal.fp.FunUtil2;
import primal.fp.Funs.Iterate;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Source2;
import primal.primitive.adt.Bytes;
import primal.primitive.adt.Bytes.BytesBuilder;
import primal.primitive.adt.Chars;
import primal.primitive.fp.AsChr;
import primal.puller.Puller;
import primal.puller.Puller2;
import primal.streamlet.Streamlet;
import primal.streamlet.Streamlet2;

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
		public static Puller<Bytes> utf8(Puller<Chars> charsPuller) {
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

	public static class Fit {
		public static FixieArray<String> parts(String in, String... parts) {
			return fit(in, parts, s -> s);
		}

		public static FixieArray<String> partsCaseInsensitive(String in, String... parts) {
			return fit(in, parts, String::toLowerCase);
		}

		private static FixieArray<String> fit(String in, String[] parts, Iterate<String> lower) {
			var outs = new ArrayList<String>();
			var inl = lower.apply(in);
			var p = 0;
			for (var part : parts) {
				var p1 = inl.indexOf(lower.apply(part), p);
				if (0 <= p1) {
					outs.add(in.substring(p, p1));
					p = p1 + part.length();
				} else
					return null;
			}
			outs.add(in.substring(p));
			return FixieArray.of(outs);
		}
	}

	public static class Pull {
		public static Puller<Bytes> from(String data) {
			return from(data.getBytes(Utf8.charset));
		}

		public static Puller<Bytes> from(byte[] bs) {
			return from(new ByteArrayInputStream(bs));
		}

		public static Puller<Bytes> from(InputStream is) {
			var bis = new BufferedInputStream(is);
			return from_(bis).closeAtEnd(bis).closeAtEnd(is);
		}

		public static Puller<Bytes> from_(InputStream is) {
			return Puller.of(() -> {
				var bs = new byte[Buffer.size];
				var n = ex(() -> is.read(bs));
				return 0 <= n ? Bytes.of(bs, 0, n) : null;
			});
		}
	}

	public static class Read {
		private static Streamlet<?> empty = from(() -> FunUtil.nullSource());
		private static Streamlet2<?, ?> empty2 = from2(() -> FunUtil2.nullSource());

		public static <T> Streamlet<T> empty() {
			@SuppressWarnings("unchecked")
			var st = (Streamlet<T>) empty;
			return st;
		}

		public static <K, V> Streamlet2<K, V> empty2() {
			@SuppressWarnings("unchecked")
			var st = (Streamlet2<K, V>) empty2;
			return st;
		}

		@SafeVarargs
		public static <T> Streamlet<T> each(T... ts) {
			return from(ts);
		}

		@SafeVarargs
		public static <K, V> Streamlet2<K, V> each2(Pair<K, V>... pairs) {
			return from2(Arrays.asList(pairs));
		}

		public static <T> Streamlet<T> from(T[] ts) {
			return new Streamlet<>(() -> Puller.of(ts));
		}

		public static <T> Streamlet<T> from(Enumeration<T> en) {
			return new Streamlet<>(() -> Puller.of(en));
		}

		public static <T> Streamlet<T> from(Iterable<T> col) {
			return new Streamlet<>(() -> Puller.of(col));
		}

		public static <T> Streamlet<T> from(Source<Source<T>> source) {
			return new Streamlet<>(() -> Puller.of(source.g()));
		}

		public static <K, V> Streamlet2<K, V> from2(Map<K, V> map) {
			return new Streamlet2<>(() -> Puller2.of(map));
		}

		public static <K, V> Streamlet2<K, V> from2(Iterable<Pair<K, V>> col) {
			return new Streamlet2<>(() -> Puller2.of(col));
		}

		public static <K, V> Streamlet2<K, V> from2(ListMultimap<K, V> multimap) {
			return fromMultimap(multimap).concatMapValue(Read::from);
		}

		public static <K, V> Streamlet2<K, V> from2(Source<Source2<K, V>> source) {
			return new Streamlet2<>(() -> Puller2.of(source.g()));
		}

		public static <K, V, C extends Collection<V>> Streamlet2<K, V> fromListMap(Map<K, C> map) {
			return from2(map).concatMap2((k, l) -> from(l).map2(v -> k, v -> v));
		}

		public static <K, V> Streamlet2<K, List<V>> fromMultimap(ListMultimap<K, V> multimap) {
			return from2(multimap.map);
		}
	}

	public static class ReadLines {
		public static Streamlet<String> from(Path path) {
			return from(path.toFile());
		}

		public static Streamlet<String> from(File file) {
			return from(ex(() -> new FileInputStream(file)));
		}

		public static Streamlet<String> from(InputStream is) {
			return from(new InputStreamReader(is, Utf8.charset)).closeAtEnd(is);
		}

		public static Streamlet<String> from(Reader reader) {
			var br = new BufferedReader(reader);
			return new Streamlet<>(() -> Puller //
					.of(() -> ex(() -> ReadLine.from(br))) //
					.closeAtEnd(br) //
					.closeAtEnd(reader));
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
