package primal.primitive.adt.set;

import java.util.Arrays;

import primal.Verbs.Build;
import primal.primitive.DblPrim;
import primal.primitive.DblPrim.DblSink;
import primal.primitive.DblPrim.DblSource;
import primal.primitive.DblVerbs.CopyDbl;
import primal.primitive.fp.DblFunUtil;

/**
 * Set with double elements. Double.MIN_VALUE is not allowed. Not thread-safe.
 *
 * @author ywsing
 */
public class DblSet {

	private static double empty = DblPrim.EMPTYVALUE;

	private int size;
	private double[] vs;

	public DblSet() {
		this(8);
	}

	public DblSet(int capacity) {
		allocate(capacity);
	}

	public boolean add(double c) {
		var capacity = vs.length;

		if (capacity * 3 / 4 < size) {
			var vs0 = vs;
			double v_;

			allocate(capacity * 2);

			for (var i = 0; i < capacity; i++)
				if ((v_ = vs0[i]) != empty)
					add_(v_);
		}

		var b = add_(c);
		size += b ? 1 : 0;
		return b;
	}

	public boolean contains(double c) {
		return vs[index(c)] == c;
	}

	public DblSet clone() {
		var capacity = vs.length;
		var set = new DblSet(capacity);
		set.size = size;
		CopyDbl.array(vs, 0, set.vs, 0, capacity);
		return set;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof DblSet) {
			var other = (DblSet) object;
			var b = size == other.size;
			for (var c : DblFunUtil.iter(source()))
				b &= other.contains(c);
			return b;
		} else
			return false;
	}

	public void forEach(DblSink sink) {
		var source = source_();
		double c;
		while ((c = source.g()) != empty)
			sink.f(c);
	}

	@Override
	public int hashCode() {
		var h = 7;
		for (var c : DblFunUtil.iter(source()))
			h = h * 31 + Double.hashCode(c);
		return h;
	}

	public boolean remove(double c) {
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

	public DblSource source() {
		return source_();
	}

	@Override
	public String toString() {
		return Build.string(sb -> DblFunUtil.iter(source()).forEach(sb::append));
	}

	private boolean add_(double c) {
		var index = index(c);
		var b = vs[index] != c;
		vs[index] = c;
		return b;
	}

	private int index(double c) {
		var mask = vs.length - 1;
		var index = Double.hashCode(c) & mask;
		double c0;
		while ((c0 = vs[index]) != empty && c0 != c)
			index = index + 1 & mask;
		return index;
	}

	private DblSource source_() {
		return new DblSource() {
			private int capacity = vs.length;
			private int index = 0;

			public double g() {
				double v;
				while (index < capacity)
					if ((v = vs[index++]) != empty)
						return v;
				return empty;
			}
		};
	}

	private void allocate(int capacity) {
		vs = new double[capacity];
		Arrays.fill(vs, empty);
	}

}
