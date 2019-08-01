package primal.primitive;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.FltPrim.FltTest;

public class FltOpt {

	private static float empty = FltPrim.EMPTYVALUE;
	private static FltOpt none_ = FltOpt.of(empty);

	private float value;

	public interface Map<T> {
		public T apply(float c);
	}
	
	public static FltOpt none() {
		return none_;
	}

	public static FltOpt of(float t) {
		var p = new FltOpt();
		p.value = t;
		return p;
	}

	public boolean isEmpty() {
		return value == empty;
	}

	public FltOpt filter(FltTest pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Map<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public float get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == FltOpt.class && Equals.ab(value, ((FltOpt) object).value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Float.toString(value) : "null";
	}

}
