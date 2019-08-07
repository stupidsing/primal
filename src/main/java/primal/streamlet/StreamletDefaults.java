package primal.streamlet;

import primal.fp.Funs.Fun;
import primal.primitive.DblPrim.Obj_Dbl;
import primal.primitive.IntPrim.Obj_Int;
import primal.puller.PullerDefaults;

public interface StreamletDefaults<T, Opt, Pred, Puller_ extends PullerDefaults<T, Opt, Pred, Sink, Source>, Sink, Source>
		extends Iterable<T> {

	public Puller_ puller();

	public default <R> R collect(Fun<Puller_, R> fun) {
		return fun.apply(puller());
	}

	public default boolean isAll(Pred pred) {
		return puller().isAll(pred);
	}

	public default boolean isAny(Pred pred) {
		return puller().isAny(pred);
	}

	public default Opt opt() {
		return puller().opt();
	}

	public default void sink(Sink sink) {
		puller().sink(sink);
	}

	public default int size() {
		return puller().count();
	}

	public default Source source() {
		return puller().source();
	}

	public default double toDouble(Obj_Dbl<Puller_> fun) {
		return fun.apply(puller());
	}

	public default int toInt(Obj_Int<Puller_> fun) {
		return fun.apply(puller());
	}

	public default String toJoinedString() {
		return puller().toJoinedString();
	}

	public default String toJoinedString(String delimiter) {
		return puller().toJoinedString(delimiter);
	}

	public default String toJoinedString(String before, String delimiter, String after) {
		return puller().toJoinedString(before, delimiter, after);
	}

}
