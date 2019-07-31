package primal.adt;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.Verbs.Equals;
import primal.Verbs.Get;

public class Mutable<T> {

	private T value;

	public static <T> Mutable<T> nil() {
		return Mutable.of(null);
	}

	public static <T> Mutable<T> of(T t) {
		var p = new Mutable<T>();
		p.update(t);
		return p;
	}

	public void set(T t) {
		if (value == null)
			update(t);
		else
			fail("value already set");
	}

	public void update(T t) {
		value = t;
	}

	public T value() {
		return value;
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == Mutable.class && Equals.ab(value, ((Mutable<?>) object).value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value != null ? value.toString() : "null";
	}

}
