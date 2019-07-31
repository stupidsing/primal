package primal;

import static java.lang.Math.min;

import java.util.function.IntPredicate;

import primal.Verbs.Compare;
import primal.Verbs.Equals;
import primal.adt.Pair;
import primal.fp.Funs.Sink;

public class String_ {

	public static String build(Sink<StringBuilder> sink) {
		var sb = new StringBuilder();
		sink.f(sb);
		return sb.toString();
	}

	public static char charAt(String s, int pos) {
		if (pos < 0)
			pos += s.length();
		return s.charAt(pos);
	}

	public static int compare(String s0, String s1) {
		return Compare.objects(s0, s1);
	}

	public static boolean equals(String s0, String s1) {
		return Equals.ab(s0, s1);
	}

	public static boolean isBlank(String s) {
		return s == null || isAll(s, Character::isWhitespace);
	}

	public static boolean isInteger(String s) {
		if (!s.isEmpty()) {
			if (s.charAt(0) == '-')
				s = s.substring(1);

			return !s.isEmpty() && isAll(s, Character::isDigit);
		} else
			return false;
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static String left(String s, int pos) {
		var size = s.length();
		if (pos < 0)
			pos += size;
		return s.substring(0, pos);
	}

	public static String range(String s, int start, int end) {
		var length = s.length();
		if (start < 0)
			start += length;
		if (end < 0)
			end += length;
		end = min(length, end);
		return s.substring(start, end);
	}

	public static String right(String s, int pos) {
		var size = s.length();
		if (pos < 0)
			pos += size;
		return s.substring(pos);
	}

	public static Pair<String, String> split2l(String s, String delimiter) {
		var pair = split2(s, delimiter);
		return pair != null ? pair : Pair.of(s.trim(), "");
	}

	public static Pair<String, String> split2r(String s, String delimiter) {
		var pair = split2(s, delimiter);
		return pair != null ? pair : Pair.of("", s.trim());
	}

	public static Pair<String, String> split2(String s, String delimiter) {
		var pos = s.indexOf(delimiter);
		return 0 <= pos ? Pair.of(s.substring(0, pos).trim(), s.substring(pos + delimiter.length()).trim()) : null;
	}

	private static boolean isAll(String s, IntPredicate pred) {
		var b = true;
		for (var i = 0; i < s.length(); i++)
			b &= pred.test(s.charAt(i));
		return b;
	}

}
