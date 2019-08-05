package primal.primitive;

import static primal.statics.Fail.fail;

import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.ChrPrim.ChrPred;
import primal.primitive.ChrPrim.Chr_Obj;

public class ChrOpt {

	private static char empty = ChrPrim.EMPTYVALUE;
	private static ChrOpt none_ = ChrOpt.of(empty);

	private char value;

	public static ChrOpt none() {
		return none_;
	}

	public static ChrOpt of(char t) {
		var p = new ChrOpt();
		p.value = t;
		return p;
	}

	public boolean isEmpty() {
		return value == empty;
	}

	public <T> ChrOpt concatMap(Chr_Obj<ChrOpt> fun) {
		return !isEmpty() ? fun.apply(value) : none_;
	}

	public ChrOpt filter(ChrPred pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Chr_Obj<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public char get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == ChrOpt.class && value == ((ChrOpt) object).value;
	}

	@Override
	public int hashCode() {
		return Character.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Character.toString(value) : "null";
	}

}
