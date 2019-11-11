package primal.primitive;

import static primal.statics.Fail.fail;

import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.FltPrim.FltPred;
import primal.primitive.FltPrim.Flt_Obj;

public class FltOpt {

	private static float empty = FltPrim.EMPTYVALUE;
	private static FltOpt none_ = FltOpt.of(empty);

	private float value;

	public static FltOpt none() {
		return none_;
	}

	public static FltOpt of(float t) {
		var p = new FltOpt();
		p.value = t;
		return p;
	}

	public boolean hasValue() {
		return hasValue_();
	}

	public boolean isEmpty() {
		return !hasValue_();
	}

	public <T> FltOpt concatMap(Flt_Obj<FltOpt> fun) {
		return hasValue_() ? fun.apply(value) : none_;
	}

	public FltOpt filter(FltPred pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Flt_Obj<T> fun) {
		return hasValue_() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public float g() {
		return hasValue_() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == FltOpt.class && value == ((FltOpt) object).value;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(value);
	}

	@Override
	public String toString() {
		return hasValue_() ? Float.toString(value) : "null";
	}

	private boolean hasValue_() {
		return value != empty;
	}

}
