package primal.primitive.adt.set;

import java.util.Arrays;

import primal.Verbs.Build;
import primal.primitive.FltPrim;
import primal.primitive.FltPrim.FltSink;
import primal.primitive.FltPrim.FltSource;
import primal.primitive.FltVerbs.CopyFlt;
import primal.primitive.fp.FltFunUtil;

/**
 * Set with floatacter elements. Float.MIN_VALUE is not allowed. Not
 * thread-safe.
 *
 * @author ywsing
 */
public class FltSet {

	private static float empty = FltPrim.EMPTYVALUE;

	private int size;
	private float[] vs;

	public FltSet() {
		this(8);
	}

	public FltSet(int capacity) {
		allocate(capacity);
	}

	public boolean add(float c) {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var vs0 = vs;
			float v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					add_(v_);
		}

		var b = add_(c);
		size += b ? 1 : 0;
		return b;
	}

	public boolean contains(float c) {
		return vs[index(c)] == c;
	}

	public FltSet clone() {
		var capacity = vs.length;
		var set = new FltSet(capacity);
		set.size = size;
		CopyFlt.array(vs, 0, set.vs, 0, capacity);
		return set;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof FltSet) {
			var other = (FltSet) object;
			var b = size == other.size;
			for (var c : FltFunUtil.iter(source()))
				b &= other.contains(c);
			return b;
		} else
			return false;
	}

	public void forEach(FltSink sink) {
		var source = source_();
		float c;
		while ((c = source.g()) != empty)
			sink.f(c);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var c : FltFunUtil.iter(source()))
			h = h * 31 + Float.hashCode(c);
		return h;
	}

	public boolean remove(float c) {
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

	public FltSource source() {
		return source_();
	}

	@Override
	public String toString() {
		return Build.string(sb -> FltFunUtil.iter(source()).forEach(sb::append));
	}

	private boolean add_(float c) {
		var index = index(c);
		var b = vs[index] != c;
		vs[index] = c;
		return b;
	}

	private int index(float c) {
		var mask = vs.length - 1;
		var index = Float.hashCode(c) & mask;
		float c0;
		while ((c0 = vs[index]) != empty && c0 != c)
			index = index + 1 & mask;
		return index;
	}

	private FltSource source_() {
		return new FltSource() {
			private int capacity = vs.length;
			private int index = 0;

			public float g() {
				float v;
				while (index < capacity)
					if ((v = vs[index++]) != empty)
						return v;
				return empty;
			}
		};
	}

	private void allocate(int capacity) {
		vs = new float[capacity];
		Arrays.fill(vs, empty);
	}

}
