package primal;

import static java.lang.Math.min;
import static primal.statics.Fail.fail;
import static primal.statics.Rethrow.ex;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
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

import primal.fp.Funs.Sink;
import primal.fp.Funs.Source;
import primal.os.Log_;

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

	public static class Equals {
		public static boolean ab(Object a, Object b) {
			return Objects.equals(a, b);
		}
	}

	public static class First {
		public static <T> T of(Collection<T> c) {
			return !c.isEmpty() ? c.iterator().next() : null;
		}

	}

	public static class Get {
		private static AtomicInteger counter = new AtomicInteger();

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
	}

	public static class Min {
		public static <T extends Comparable<? super T>> T of(T t0, T t1) {
			return Compare.objects(t0, t1) < 0 ? t0 : t1;
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

	public static class ReadLine {
		private static int bufferLimit = 65536;

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
					if (bufferLimit <= sb.length())
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
					if (bufferLimit <= sb.length())
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
	}

	public static class Start {
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
