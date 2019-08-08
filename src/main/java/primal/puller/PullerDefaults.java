package primal.puller;

import primal.Verbs.Build;

public interface PullerDefaults<T, Opt, Pred, Sink, Source> extends Iterable<T> {

	public int count();

	public Opt opt();

	public boolean isAll(Pred pred);

	public boolean isAny(Pred pred);

	public void sink(Sink sink);

	public Source source();

	public default String toJoinedString() {
		return toJoinedString("");
	}

	public default String toJoinedString(String delimiter) {
		return toJoinedString("", delimiter, "");
	}

	public default String toJoinedString(String before, String delimiter, String after) {
		return "" //
				+ before //
				+ Build.string(sb -> forEach(s -> {
					if (0 < sb.length())
						sb.append(delimiter);
					sb.append(s);
				})) //
				+ after;
	}

}
