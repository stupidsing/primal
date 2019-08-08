package primal;

import static java.lang.Math.min;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static primal.statics.Fail.fail;
import static primal.statics.Rethrow.ex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import primal.Nouns.Buffer;
import primal.Nouns.Utf8;
import primal.adt.Pair;
import primal.fp.Funs.Sink;
import primal.fp.Funs.Source;
import primal.io.ReadStream;
import primal.io.WriteStream;
import primal.os.Log_;
import primal.primitive.adt.Bytes;
import primal.puller.Puller;

public class Verbs {

	public static class Build {
		public static String string(Sink<StringBuilder> sink) {
			var sb = new StringBuilder();
			sink.f(sb);
			return sb.toString();
		}
	}

	public static class Close {
		public static void quietly(Closeable... os) {
			for (var o : os)
				if (o != null)
					ex(() -> {
						o.close();
						return o;
					});
		}
	}

	public static class Compare {
		public static <T> int anyway(T t0, T t1) {
			if (t0 instanceof Comparable && t1 instanceof Comparable && t0.getClass() == t1.getClass()) {
				@SuppressWarnings("unchecked")
				var c0 = (Comparable<Object>) t0;
				@SuppressWarnings("unchecked")
				var c1 = (Comparable<Object>) t1;
				return objects(c0, c1);
			} else
				return Integer.compare(Objects.hashCode(t0), Objects.hashCode(t1));
		}

		public static <T extends Comparable<? super T>> int objects(T t0, T t1) {
			var b0 = t0 != null;
			var b1 = t1 != null;
			if (b0 && b1)
				return t0.compareTo(t1);
			else
				return b0 ? 1 : b1 ? -1 : 0;
		}

		public static int primitive(byte a, byte b) {
			return Integer.compare(Byte.toUnsignedInt(a), Byte.toUnsignedInt(b));
		}

		public static int primitive(char a, char b) {
			return Character.compare(a, b);
		}

		public static int primitive(double a, double b) {
			return Double.compare(a, b);
		}

		public static int primitive(float a, float b) {
			return Float.compare(a, b);
		}

		public static int primitive(int a, int b) {
			return Integer.compare(a, b);
		}

		public static int primitive(short a, short b) {
			return Short.compare(a, b);
		}

		public static int string(String a, String b) {
			return objects(a, b);
		}
	}

	public static class Concat {
		@SafeVarargs
		public static <T> T[] arrays(Class<T> clazz, T[]... lists) {
			var size = 0;

			for (var list : lists)
				size += list.length;

			var result = New.array(clazz, size);
			var i = 0;

			for (var list : lists) {
				var length = list.length;
				Copy.array(list, 0, result, i, length);
				i += length;
			}

			return result;
		}

		@SafeVarargs
		public static <T> List<T> lists(Collection<T>... collections) {
			var list = new ArrayList<T>();
			for (var collection : collections)
				list.addAll(collection);
			return list;
		}
	}

	public static class Copy {
		public static <T> void array(T[] from, int fromIndex, T[] to, int toIndex, int size) {
			if (0 < size)
				System.arraycopy(from, fromIndex, to, toIndex, size);
			else if (size < 0)
				throw new IndexOutOfBoundsException();
		}
	}

	public static class Current {
		public static Class<?> clazz() {
			try {
				return Class.forName(Get.stackTrace(3).getClassName());
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static String method() {
			return Get.stackTrace(3).getMethodName();
		}

		public static String package_() {
			var cls = Get.stackTrace(3).getClassName();
			var pos = cls.lastIndexOf(".");
			return cls.substring(0, pos);
		}
	}

	public static class DeleteFile {
		public static void ifExists(Path path) {
			ex(() -> {
				Files.deleteIfExists(path);
				return path;
			});
		}

		public static void on(Path path) {
			ex(() -> {
				Files.delete(path);
				return path;
			});
		}
	}

	public static class Equals {
		public static boolean ab(Object a, Object b) {
			return Objects.equals(a, b);
		}

		public static boolean string(String a, String b) {
			return Objects.equals(a, b);
		}
	}

	public static class First {
		public static <T> T of(Collection<T> c) {
			return !c.isEmpty() ? c.iterator().next() : null;
		}

	}

	public static class Format {
		private static String hexDigits = "0123456789ABCDEF";

		public static String hex(long i) {
			return Character.toString(hexDigits.charAt((int) (i & 0x0F)));
		}

		public static String hex2(long i) {
			return hex(i >>> 4) + hex(i);
		}

		public static String hex4(long i) {
			return hex2(i >>> 8 & 0xFF) + hex2(i & 0xFF);
		}

		public static String hex8(long i) {
			return hex4(i >>> 16 & 0xFFFF) + hex4(i & 0xFFFF);
		}
	}

	public static class Get {
		private static AtomicInteger counter = new AtomicInteger();

		public static char ch(String s, int pos) {
			if (pos < 0)
				pos += s.length();
			return s.charAt(pos);
		}

		public static Class<?> clazz(Object object) {
			return object != null ? object.getClass() : null;
		}

		public static String fileExtension(Path path) {
			var filename = path.toString();
			return filename.substring(filename.lastIndexOf('.') + 1);
		}

		public static long pid() {
			return ManagementFactory.getRuntimeMXBean().getPid();
		}

		public static StackTraceElement stackTrace(int n) {
			return Thread.currentThread().getStackTrace()[n];
		}

		public static int temp() {
			return counter.getAndIncrement();
		}
	}

	public static class Instantiate {
		public static <T> T clazz(Class<T> clazz) {
			Object object;
			if (clazz == ArrayList.class || clazz == Collection.class || clazz == List.class)
				object = new ArrayList<>();
			else if (clazz == HashSet.class || clazz == Set.class)
				object = new HashSet<>();
			else if (clazz == HashMap.class || clazz == Map.class)
				object = new HashMap<>();
			else
				return New.clazz(clazz);

			@SuppressWarnings("unchecked")
			var t = (T) object;
			return t;
		}
	}

	public static class Intersect {
		@SafeVarargs
		public static <T> Set<T> of(Collection<T>... collections) {
			return of(List.of(collections));
		}

		public static <T> Set<T> of(Collection<Collection<T>> collections) {
			var iter = collections.iterator();
			Set<T> set = iter.hasNext() ? new HashSet<>(iter.next()) : fail();
			while (iter.hasNext())
				set.retainAll(iter.next());
			return set;
		}
	}

	public static class Is {
		public static boolean blank(String s) {
			return s == null || isAll(s, Character::isWhitespace);
		}

		public static boolean integer(String s) {
			if (!s.isEmpty()) {
				if (s.charAt(0) == '-')
					s = s.substring(1);

				return !s.isEmpty() && isAll(s, Character::isDigit);
			} else
				return false;
		}

		public static boolean notBlank(String s) {
			return !blank(s);
		}

		public static boolean whitespace(byte b) {
			return b == 0;
		}

		public static boolean whitespace(char c) {
			return Character.isWhitespace(c);
		}

		public static boolean whitespace(double d) {
			return d == 0d;
		}

		public static boolean whitespace(float f) {
			return f == 0f;
		}

		public static boolean whitespace(int i) {
			return i == 0;
		}

		private static boolean isAll(String s, IntPredicate pred) {
			var b = true;
			for (var i = 0; i < s.length(); i++)
				b &= pred.test(s.charAt(i));
			return b;
		}
	}

	public static class Last {
		public static <T> T of(List<T> c) {
			return !c.isEmpty() ? c.get(c.size() - 1) : null;
		}

	}

	public static class Left {
		public static <T> List<T> of(List<T> list, int pos) {
			var size = list.size();
			if (pos < 0)
				pos += size;
			return list.subList(0, min(pos, size));
		}

		public static String of(String s, int pos) {
			var size = s.length();
			if (pos < 0)
				pos += size;
			return s.substring(0, pos);
		}
	}

	public static class Min {
		public static <T extends Comparable<? super T>> T of(T t0, T t1) {
			return Compare.objects(t0, t1) < 0 ? t0 : t1;
		}
	}

	/**
	 * Files.createDirectory() might fail with FileAlreadyExistsException in MacOSX,
	 * contrary to its documentation. This re-implementation would not.
	 */
	public static class Mk {
		public static void dir(Path path) {
			if (path != null) {
				dir(path.getParent());
				if (!Files.isDirectory(path))
					ex(() -> Files.createDirectories(path));
			}
		}
	}

	public static class New {
		public static <T> T clazz(Class<T> clazz) {
			return ex(() -> {
				var ctor = clazz.getDeclaredConstructor();
				ctor.setAccessible(true);
				return ctor.newInstance();
			});
		}

		public static ThreadPoolExecutor executor() {
			return newExecutor(8, 32);
		}

		public static ThreadPoolExecutor executorByProcessors() {
			var nProcessors = Runtime.getRuntime().availableProcessors();
			return newExecutor(nProcessors, nProcessors);
		}

		@SuppressWarnings("unchecked")
		public static <T> T[] array(Class<T> clazz, int dim) {
			return (T[]) Array.newInstance(clazz, dim);
		}

		public static Th thread(RunnableEx runnable) {
			return new Th(runnable);
		}

		private static ThreadPoolExecutor newExecutor(int corePoolSize, int maxPoolSize) {
			var queue = new ArrayBlockingQueue<Runnable>(256);
			return new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS, queue);
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

	public static class Range {
		public static String of(String s, int start, int end) {
			var length = s.length();
			if (start < 0)
				start += length;
			if (end < 0)
				end += length;
			end = min(length, end);
			return s.substring(start, end);
		}
	}

	public static class ReadFile {
		public static ReadStream from(String filename) {
			return in_(Paths.get(filename));
		}

		public static ReadStream from(Path path) {
			return in_(path);
		}

		private static ReadStream in_(Path path) {
			var is = ex(() -> Files.newInputStream(path));

			return ReadStream.of(is);
		}
	}

	public static class ReadLine {
		private static int limit = 65536;

		/**
		 * Reads a line from a stream with a maximum line length limit. Removes carriage
		 * return if it is DOS-mode line feed (CR-LF). Unknown behaviour when dealing
		 * with non-ASCII encoding characters.
		 */
		public static String from(InputStream is) {
			return ex(() -> {
				var sb = new StringBuilder();
				int c;
				while (0 <= (c = is.read()) && c != 10) {
					sb.append((char) c);
					if (limit <= sb.length())
						fail("line too long");
				}
				return 0 <= c ? Strip.string(sb) : null;
			});
		}

		public static String from(Reader reader) {
			return ex(() -> {
				var sb = new StringBuilder();
				int c;
				while (0 <= (c = reader.read()) && c != 10) {
					sb.append((char) c);
					if (limit <= sb.length())
						fail("line too long");
				}
				return 0 <= c ? Strip.string(sb) : null;
			});
		}
	}

	public static class Reverse {
		public static <T> List<T> of(List<T> list0) {
			var list1 = new ArrayList<T>();
			for (var i = list0.size() - 1; 0 <= i; i--)
				list1.add(list0.get(i));
			return list1;
		}
	}

	public static class Right {
		public static <T> List<T> of(List<T> list, int pos) {
			var size = list.size();
			if (pos < 0)
				pos += size;
			return list.subList(min(pos, size), size);
		}

		public static String of(String s, int pos) {
			var size = s.length();
			if (pos < 0)
				pos += size;
			return s.substring(pos);
		}
	}

	public static class Sleep {
		public static void quietly(long time) {
			if (0 < time)
				try {
					Thread.sleep(time);
				} catch (InterruptedException ex) {
					Log_.error(ex);
				}
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

	public static class Start {
		public static void thenJoin(RunnableEx... rs) {
			Arrays.stream(rs).map(Start::thread).collect(Collectors.toList()).forEach(Th::join_);
		}

		public static Void thenJoin(Iterable<Th> threads0) {
			var threads1 = new ArrayList<Th>();
			threads0.iterator().forEachRemaining(threads1::add);
			threads1.forEach(Th::start);
			threads1.forEach(Th::join_);
			return null;
		}

		public static Th thread(RunnableEx runnable) {
			var thread = new Th(runnable);
			thread.start();
			return thread;
		}
	}

	public static class Strip {
		public static String string(StringBuilder sb) {
			var length = sb.length();
			if (0 < length && sb.charAt(length - 1) == 13)
				sb.deleteCharAt(length - 1);
			return sb.toString();
		}
	}

	public static class Sort {
		public static <T extends Comparable<? super T>> List<T> list(Collection<T> list) {
			var list1 = new ArrayList<>(list);
			Collections.sort(list1);
			return list1;
		}

		public static <T> List<T> list(Collection<T> list, Comparator<? super T> comparator) {
			var list1 = new ArrayList<>(list);
			Collections.sort(list1, comparator);
			return list1;
		}
	}

	public static class Take {
		@SafeVarargs
		public static <T> Source<T> from(T... array) {
			return new Source<>() {
				private int i;

				public T g() {
					return i < array.length ? array[i++] : null;
				}
			};
		}

		public static <T> Source<T> from(Enumeration<T> en) {
			return () -> en.hasMoreElements() ? en.nextElement() : null;
		}

		public static <T> Source<T> from(Iterable<T> iterable) {
			var iterator = iterable.iterator();
			return () -> iterator.hasNext() ? iterator.next() : null;
		}
	}

	public static class Trim {
		public static String left(String s) {
			var length = s.length();
			var pos = 0;
			do
				if (!Is.whitespace(s.charAt(pos)))
					break;
			while (++pos < length);
			return s.substring(pos);
		}

		public static String right(String s) {
			var pos = s.length();
			while (0 <= --pos)
				if (!Is.whitespace(s.charAt(pos)))
					break;
			return s.substring(0, pos + 1);
		}
	}

	public static class Union {
		@SafeVarargs
		public static <T> Set<T> of(Collection<T>... collections) {
			return of(List.of(collections));
		}

		private static <T> Set<T> of(Collection<Collection<T>> collections) {
			var set = new HashSet<T>();
			for (var collection : collections)
				set.addAll(collection);
			return set;
		}

	}

	public static class Wait {
		public static void object(Object object) {
			object(object, 0);
		}

		public static void object(Object object, int timeOut) {
			ex(() -> {
				object.wait(timeOut);
				return object;
			});
		}
	}

	public static class WriteFile {
		public static WriteStream to(String filename) {
			return out_(Paths.get(filename));
		}

		public static WriteStream to(Path path) {
			return out_(path);
		}

		private static WriteStream out_(Path path) {
			var parent = path.getParent();
			var path1 = parent.resolve(path.getFileName() + ".new");

			Mk.dir(parent);
			var os = ex(() -> Files.newOutputStream(path1));

			return new WriteStream(os) {
				private boolean isClosed = false;

				public void close() throws IOException {
					if (!isClosed) {
						os.close();
						isClosed = true;
						Files.move(path1, path, ATOMIC_MOVE, REPLACE_EXISTING);
					}
				}
			};
		}
	}

	public interface RunnableEx {
		public void run() throws Exception;
	}

	public static class Th extends Thread {
		private RunnableEx runnable;

		public Th(RunnableEx runnable) {
			this.runnable = runnable;
		}

		public void run() {
			try {
				runnable.run();
			} catch (Exception ex) {
				Log_.error(ex);
			}
		}

		public void join_() {
			ex(() -> {
				join();
				return this;
			});
		}
	}

}
