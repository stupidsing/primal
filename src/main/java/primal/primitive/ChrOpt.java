package primal.primitive;

import static primal.statics.Fail.fail;

import java.util.Objects;

import primal.Verbs.Equals;
import primal.Verbs.Get;
import primal.adt.Opt;
import primal.primitive.ChrPrim.ChrTest;

public class ChrOpt {

	private static char empty = ChrPrim.EMPTYVALUE;
	private static ChrOpt none_ = ChrOpt.of(empty);

	private char value;

	public interface Map<T> {
		public T apply(char c);
	}
	
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

	public ChrOpt filter(ChrTest pred) {
		return isEmpty() || pred.test(value) ? this : none();
	}

	public <T> Opt<T> map(Map<T> fun) {
		return !isEmpty() ? Opt.of(fun.apply(value)) : Opt.none();
	}

	public char get() {
		return !isEmpty() ? value : fail("no result");
	}

	@Override
	public boolean equals(Object object) {
		return Get.clazz(object) == ChrOpt.class && Equals.ab(value, ((ChrOpt) object).value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}

	@Override
	public String toString() {
		return value != empty ? Character.toString(value) : "null";
	}

}
