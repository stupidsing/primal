package primal.primitive;

import static primal.statics.Fail.fail;

import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.DblPrim.DblPred;
import primal.primitive.DblPrim.Dbl_Obj;

public class DblOpt {

	private static double empty = DblPrim.EMPTYVALUE;
	private static DblOpt none_ = DblOpt.of(empty);

	private double value;

	public static DblOpt none() {
		return none_;
	}

	public static DblOpt of(double t) {
		var p = new DblOpt();
		p.value = t;
		return p;
	}

	public boolean hasValue() {
		return hasValue_();
	}

	public boolean isEmpty() {
		return !hasValue_();
	}

	public <T> DblOpt concatMap(Dbl_Obj<DblOpt> fun) {
		return hasValue_() ? fun.apply(value) : none_;
	}

	public DblOpt filter(DblPred pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Dbl_Obj<T> fun) {
		return hasValue_() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public double g() {
		return hasValue_() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == DblOpt.class && value == ((DblOpt) object).value;
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public String toString() {
		return hasValue_() ? Double.toString(value) : "null";
	}

	private boolean hasValue_() {
		return value != empty;
	}

}
