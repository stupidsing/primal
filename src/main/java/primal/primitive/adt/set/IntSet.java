package primal.primitive.adt.set;

import java.util.Arrays;

import primal.Verbs.Build;
import primal.primitive.IntPrim;
import primal.primitive.IntPrim.IntSink;
import primal.primitive.IntPrim.IntSource;
import primal.primitive.IntVerbs.CopyInt;
import primal.primitive.fp.IntFunUtil;

/**
 * Set with intacter elements. Integer.MIN_VALUE is not allowed. Not
 * thread-safe.
 *
 * @author ywsing
 */
public class IntSet {

	private static int empty = IntPrim.EMPTYVALUE;

	private int size;
	private int[] vs;

	public IntSet() {
		this(8);
	}

	public IntSet(int capacity) {
		allocate(capacity);
	}

	public boolean add(int c) {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var vs0 = vs;
			int v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					add_(v_);
		}

		var b = add_(c);
		size += b ? 1 : 0;
		return b;
	}

	public boolean contains(int c) {
		return vs[index(c)] == c;
	}

	public IntSet clone() {
		var capacity = vs.length;
		var set = new IntSet(capacity);
		set.size = size;
		CopyInt.array(vs, 0, set.vs, 0, capacity);
		return set;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof IntSet) {
			var other = (IntSet) object;
			var b = size == other.size;
			for (var c : IntFunUtil.iter(source()))
				b &= other.contains(c);
			return b;
		} else
			return false;
	}

	public void forEach(IntSink sink) {
		var source = source_();
		int c;
		while ((c = source.g()) != empty)
			sink.f(c);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var c : IntFunUtil.iter(source()))
			h = h * 31 + Integer.hashCode(c);
		return h;
	}

	public boolean remove(int c) {
		var mask = vs.length - 1;
		var index = index(c);
		var b = vs[index] == c;
		if (b) {
			vs[index] = empty;
			size--;
			new Object() {
				private void rehash(int index) {
					var index1 = (index + 1) & mask;
					var v = vs[index1];
					if (v != empty) {
						vs[index1] = empty;
						rehash(index1);
						vs[index(v)] = v;
					}
				}
			}.rehash(index);
		}
		return b;
	}

	public int size() {
		return size;
	}

	public IntSource source() {
		return source_();
	}

	@Override
	public String toString() {
		return Build.string(sb -> IntFunUtil.iter(source()).forEach(sb::append));
	}

	private boolean add_(int c) {
		var index = index(c);
		var b = vs[index] != c;
		vs[index] = c;
		return b;
	}

	private int index(int c) {
		var mask = vs.length - 1;
		var index = Integer.hashCode(c) & mask;
		int c0;
		while ((c0 = vs[index]) != empty && c0 != c)
			index = index + 1 & mask;
		return index;
	}

	private IntSource source_() {
		return new IntSource() {
			private int capacity = vs.length;
			private int index = 0;

			public int g() {
				int v;
				while (index < capacity)
					if ((v = vs[index++]) != empty)
						return v;
				return empty;
			}
		};
	}

	private void allocate(int capacity) {
		vs = new int[capacity];
		Arrays.fill(vs, empty);
	}

}
