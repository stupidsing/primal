package primal.primitive;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.LngPrim.LngTest;

public class LngOpt {

	private static long empty = LngPrim.EMPTYVALUE;
	private static LngOpt none_ = LngOpt.of(empty);

	private long value;

	public interface Map<T> {
		public T apply(long c);
	}
	
	public static LngOpt none() {
		return none_;
	}

	public static LngOpt of(long t) {
		var p = new LngOpt();
		p.value = t;
		return p;
	}

	public boolean isEmpty() {
		return value == empty;
	}

	public LngOpt filter(LngTest pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Map<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public long get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == LngOpt.class && Equals.ab(value, ((LngOpt) object).value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Long.toString(value) : "null";
	}

}
