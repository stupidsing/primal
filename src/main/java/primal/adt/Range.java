package primal.adt;

import java.util.Objects;

import primal.Verbs.Compare;
import primal.Verbs.Equals;
import primal.Verbs.Get;

public class Range<T extends Comparable<? super T>> implements Comparable<Range<T>> {

	public final T fr;
	public final T to;

	public static <T extends Comparable<? super T>> Range<T> of(T fr, T to) {
		return new Range<>(fr, to);
	}

	protected Range(T fr, T to) {
		this.fr = fr;
		this.to = to;
	}

	public boolean contains(T t) {
		return fr.compareTo(t) <= 0 && t.compareTo(to) < 0;
	}

	public Range<T> intersect(Range<T> other) {
		var fr0 = fr;
		var fr1 = other.fr;
		var to0 = to;
		var to1 = other.to;
		var fr = fr0.compareTo(fr1) < 0 ? fr1 : fr0;
		var to = to0.compareTo(to1) < 0 ? to0 : to1;
		return of(fr, to);
	}

	public boolean isEmpty() {
		return Compare.objects(fr, to) < 0;
	}

	@Override
	public int compareTo(Range<T> other) {
		var c = 0;
		c = c == 0 ? fr.compareTo(other.fr) : c;
		c = c == 0 ? to.compareTo(other.to) : c;
		return c;
	}

	@Override
	public boolean equals(Object object) {
		if (Get.clazz(object) == Range.class) {
			var other = (Range<?>) object;
			return Equals.ab(fr, other.fr) && Equals.ab(to, other.to);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(fr) ^ Objects.hashCode(to);
	}

	@Override
	public String toString() {
		return fr + "~" + to;
	}

}
