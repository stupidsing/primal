package primal.adt;

import static primal.statics.Fail.fail;

import java.util.Objects;
import java.util.function.Predicate;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.fp.Funs.Fun;
import primal.fp.Funs.Sink;
import primal.fp.Funs.Source;
import primal.fp.Funs2.Fun2;

public class Opt<T> {

	private static Opt<?> none_ = Opt.of(null);
	private T value;

	@SuppressWarnings("unchecked")
	public static <T> Opt<T> none() {
		return (Opt<T>) none_;
	}

	public static <T> Opt<T> of(T t) {
		var p = new Opt<T>();
		p.value = t;
		return p;
	}

	public <U> Opt<U> concatMap(Fun<T, Opt<U>> fun) {
		return concatMap_(fun);
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == Opt.class && Equals.ab(value, ((Opt<?>) object).value);
	}

	public Opt<T> filter(Predicate<T> pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public boolean hasValue() {
		return hasValue_();
	}

	public boolean isEmpty() {
		return !hasValue_();
	}

	public <U, V> Opt<V> join(Opt<U> opt1, Fun2<T, U, V> fun) {
		return concatMap_(t -> opt1.map(u -> fun.apply(t, u)));
	}

	public T get() {
		return hasValue_() ? value : fail("no result");
	}

	public T get(Source<T> or) {
		return hasValue_() ? value : or.g();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	public <U> Opt<U> map(Fun<T, U> fun) {
		return hasValue_() ? of(fun.apply(value)) : none();
	}

	public T or(T or) {
		return hasValue_() ? value : or;
	}

	public Opt<T> orOpt(Opt<T> or) {
		return hasValue_() ? this : or;
	}

	public void sink(Sink<T> sink) {
		if (hasValue_())
			sink.f(value);
	}

	@Override
	public String toString() {
		return value != null ? value.toString() : "none";
	}

	private <U> Opt<U> concatMap_(Fun<T, Opt<U>> fun) {
		return hasValue_() ? fun.apply(value) : Opt.none();
	}

	private boolean hasValue_() {
		return value != null;
	}

}
