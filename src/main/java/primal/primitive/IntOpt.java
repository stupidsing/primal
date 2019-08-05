package primal.primitive;

import static primal.statics.Fail.fail;

import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.IntPrim.IntPred;
import primal.primitive.IntPrim.Int_Obj;

public class IntOpt {

	private static int empty = IntPrim.EMPTYVALUE;
	private static IntOpt none_ = IntOpt.of(empty);

	private int value;

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

	public <T> IntOpt concatMap(Int_Obj<IntOpt> fun) {
		return !isEmpty() ? fun.apply(value) : none_;
	}

	public IntOpt filter(IntPred pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Int_Obj<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public int get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == IntOpt.class && value == ((IntOpt) object).value;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Integer.toString(value) : "null";
	}

}
