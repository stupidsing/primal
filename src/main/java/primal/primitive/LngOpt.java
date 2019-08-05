package primal.primitive;

import static primal.statics.Fail.fail;

import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.LngPrim.LngPred;
import primal.primitive.LngPrim.Lng_Obj;

public class LngOpt {

	private static long empty = LngPrim.EMPTYVALUE;
	private static LngOpt none_ = LngOpt.of(empty);

	private long value;

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

	public <T> LngOpt concatMap(Lng_Obj<LngOpt> fun) {
		return !isEmpty() ? fun.apply(value) : none_;
	}

	public LngOpt filter(LngPred pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Lng_Obj<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public long get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == LngOpt.class && value == ((LngOpt) object).value;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Long.toString(value) : "null";
	}

}
