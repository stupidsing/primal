package primal.primitive;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.IntPrim.IntTest;

public class IntOpt {

	private static int empty = IntPrim.EMPTYVALUE;
	private static IntOpt none_ = IntOpt.of(empty);

	private int value;

	public interface Map<T> {
		public T apply(int c);
	}
	
	public static IntOpt none() {
		return none_;
	}

	public static IntOpt of(int t) {
		var p = new IntOpt();
		p.value = t;
		return p;
	}

	public boolean isEmpty() {
		return value == empty;
	}

	public IntOpt filter(IntTest pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Map<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public int get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == IntOpt.class && Equals.ab(value, ((IntOpt) object).value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Integer.toString(value) : "null";
	}

}
