package primal.primitive.adt;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static primal.statics.Rethrow.ex;

import java.io.IOException;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import primal.Nouns.Buffer;
import primal.Verbs.Build;
import primal.Verbs.Compare;
import primal.Verbs.Get;
import primal.Verbs.Is;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Sink;
import primal.primitive.LngPrim;
import primal.primitive.LngPrim.LngSource;
import primal.primitive.LngVerbs.CopyLng;
import primal.puller.Puller;
import primal.puller.primitive.LngPuller;

public class Longs implements Iterable<Long> {

	private static long[] emptyArray = new long[0];
	private static int reallocSize = 65536;

	public static Longs empty = of(emptyArray);

	public final long[] cs; // immutable
	public final int start, end;

	public interface WriteChar {
		public void write(long[] cs, int offset, int length) throws IOException;
	};

	public static Comparator<Longs> comparator = (longs0, longs1) -> {
		int start0 = longs0.start, start1 = longs1.start;
		int size0 = longs0.size_(), size1 = longs1.size_(), minSize = min(size0, size1);
		int index = 0, c = 0;

		while (c == 0 && index < minSize) {
			var c0 = longs0.cs[start0 + index];
			var c1 = longs1.cs[start1 + index];
			c = Compare.primitive(c0, c1);
			index++;
		}

		return c != 0 ? c : size0 - size1;
	};

	public static Longs build(Sink<LongsBuilder> sink) {
		return build_(sink);
	}

	public static Longs concat(Longs... array) {
		var length = 0;
		for (var longs : array)
			length += longs.size();
		var cs1 = new long[length];
		var i = 0;
		for (var longs : array) {
			var size_ = longs.size();
			CopyLng.array(longs.cs, longs.start, cs1, i, size_);
			i += size_;
		}
		return Longs.of(cs1);
	}

	public static Longs of(Puller<Longs> puller) {
		return build(cb -> puller.forEach(cb::append));
	}

	public static Longs of(LongBuffer cb) {
		var offset = cb.arrayOffset();
		return of(cb.array(), offset, offset + cb.limit());
	}

	public static Longs of(Longs longs) {
		return of(longs.cs, longs.start, longs.end);
	}

	public static Longs of(long... cs) {
		return of(cs, 0);
	}

	public static Longs of(long[] cs, int start) {
		return of(cs, start, cs.length);
	}

	public static Longs of(long[] cs, int start, int end) {
		return new Longs(cs, start, end);
	}

	private static Longs build_(Sink<LongsBuilder> sink) {
		var sb = new LongsBuilder();
		sink.f(sb);
		return sb.toLongs();
	}

	private Longs(long[] cs, int start, int end) {
		this.cs = cs;
		this.start = start;
		this.end = end;
	}

	public Longs append(Longs a) {
		int size0 = size_(), size1 = a.size_(), newSize = size0 + size1;
		var nb = new long[newSize];
		CopyLng.array(cs, start, nb, 0, size0);
		CopyLng.array(a.cs, a.start, nb, size0, size1);
		return of(nb);
	}

	public <T> T collect(Fun<Longs, T> fun) {
		return fun.apply(this);
	}

	public long get(int index) {
		if (index < 0)
			index += size_();
		var i1 = index + start;
		checkClosedBounds(i1);
		return cs[i1];
	}

	public int indexOf(Longs longs, int start) {
		for (var i = start; i <= size_() - longs.size_(); i++)
			if (startsWith(longs, i))
				return i;
		return -1;
	}

	public boolean isEmpty() {
		return end <= start;
	}

	public boolean isWhitespaces() {
		var b = true;
		for (var i = start; b && i < end; i++)
			b &= Is.whitespace(cs[i]);
		return b;
	}

	public Longs pad(int size) {
		var cb = new LongsBuilder();
		cb.append(this);
		while (cb.size() < size)
			cb.append((long) 0);
		return cb.toLongs();
	}

	public LngPuller puller() {
		return LngPuller.of(new LngSource() {
			private int i = start;

			public long g() {
				return i < end ? cs[i++] : LngPrim.EMPTYVALUE;
			}
		});
	}

	public Longs range(int s) {
		return range_(s);
	}

	public Longs range(int s, int e) {
		return range_(s, e);
	}

	public Longs replace(Longs from, Longs to) {
		var cb = new LongsBuilder();
		int i0 = 0, i;
		while (0 <= (i = indexOf(from, i0))) {
			cb.append(range_(i0, i));
			cb.append(to);
			i0 = i + from.size_();
		}
		cb.append(range_(i0));
		return cb.toLongs();
	}

	public Longs reverse() {
		var cs_ = new long[size_()];
		int si = start, di = 0;
		while (si < end)
			cs_[di++] = cs[si++];
		return Longs.of(cs_);

	}

	public int size() {
		return size_();
	}

	public Longs sort() {
		var cs = toArray();
		Arrays.sort(cs);
		return Longs.of(cs);
	}

	public boolean startsWith(Longs longs) {
		return startsWith_(longs, 0);
	}

	public boolean startsWith(Longs longs, int s) {
		return startsWith_(longs, s);
	}

	public long[] toArray() {
		if (start != 0 || end != cs.length)
			return Arrays.copyOfRange(cs, start, end);
		else
			return cs;
	}

	public LongBuffer toLongBuffer() {
		return LongBuffer.wrap(cs, start, end - start);
	}

	public Longs trim() {
		var s = start;
		var e = end;
		while (s < e && Is.whitespace(cs[s]))
			s++;
		while (s < e && Is.whitespace(cs[e - 1]))
			e--;
		return of(cs, s, e);
	}

	public void write(WriteChar out) {
		ex(() -> {
			out.write(cs, start, end - start);
			return out;
		});
	}

	@Override
	public Iterator<Long> iterator() {
		return new Iterator<>() {
			private int pos = start;

			public boolean hasNext() {
				return pos < end;
			}

			public Long next() {
				return cs[pos++];
			}
		};
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == Longs.class) {
			var other = (Longs) object;

			if (size_() == other.size_()) {
				var diff = other.start - start;
				for (var i = start; i < end; i++)
					if (cs[i] != other.cs[i + diff])
						return false;
				return true;
			} else
				return false;
		} else
			return false;
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var i = start; i < end; i++)
			h = h * 31 + Long.hashCode(cs[i]);
		return h;
	}

	@Override
	public String toString() {
		return Build.string(sb -> {
			for (var i = start; i < end; i++)
				sb.append(cs[i]);
		});
	}

	private boolean startsWith_(Longs longs, int s) {
		if (s + longs.size_() <= size_()) {
			var b = true;
			for (var i = 0; b && i < longs.size_(); i++)
				b &= get(s + i) == longs.get(i);
			return b;
		} else
			return false;
	}

	private Longs range_(int s) {
		return range_(s, size_());
	}

	private Longs range_(int s, int e) {
		var size = size_();
		if (s < 0)
			s += size;
		if (e < 0)
			e += size;
		s = min(size, s);
		e = min(size, e);
		int start_ = start + min(size, s);
		int end_ = start + min(size, e);
		var result = of(cs, start_, end_);

		// avoid small pack of longs object keeping a large buffer
		if (Boolean.FALSE && reallocSize <= cs.length && end_ - start_ < reallocSize / 4)
			result = empty.append(result); // do not share reference

		return result;
	}

	private void checkClosedBounds(int index) {
		if (index < start || end <= index)
			throw new IndexOutOfBoundsException("Index " + (index - start) + " is not within [0-" + size_() + "]");
	}

	private int size_() {
		return end - start;
	}

	public static class LongsBuilder {
		private long[] cs = emptyArray;
		private int size;

		public LongsBuilder append(Longs longs) {
			return append(longs.cs, longs.start, longs.end);
		}

		public LongsBuilder append(long c) {
			extendBuffer(size + 1);
			cs[size++] = c;
			return this;
		}

		public LongsBuilder append(long[] cs_) {
			return append(cs_, 0, cs_.length);
		}

		public LongsBuilder append(long[] cs_, int start, int end) {
			var inc = end - start;
			extendBuffer(size + inc);
			CopyLng.array(cs_, start, cs, size, inc);
			size += inc;
			return this;
		}

		public void clear() {
			cs = emptyArray;
			size = 0;
		}

		public void extend(int size1) {
			extendBuffer(size1);
			size = size1;
		}

		public int size() {
			return size;
		}

		public Longs toLongs() {
			return of(cs, 0, size);
		}

		private void extendBuffer(int capacity1) {
			var capacity0 = cs.length;

			if (capacity0 < capacity1) {
				int capacity = max(capacity0, 4);
				while (capacity < capacity1)
					capacity = capacity < Buffer.size ? capacity << 1 : capacity * 3 / 2;

				cs = Arrays.copyOf(cs, capacity);
			}
		}
	}

}
