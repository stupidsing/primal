package primal.os;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

public class Dump {

	private static int counter = 0;

	public static String dump(String variable, Object object) {
		return "var " + variable + " = " + dump(object) + ";";
	}

	public static String dump(Object object) {
		var sb = new StringBuilder();
		dump(sb::append, " //\n", object);
		return sb.toString();
	}

	/**
	 * Assumes set and map keys are comparable for a consistent sorting order.
	 */
	private static void dump(Consumer<String> sb, String indent, Object object) {
		var clazz = object != null ? object.getClass() : null;
		String indent1 = indent + "\t";

		if (object == null)
			sb.accept("null");
		else if (clazz.isArray()) {
			sb.accept("new Object[] {");
			for (var i = 0; i < Array.getLength(object); i++) {
				sb.accept((i != 0 ? "," : "") + indent1);
				dump(sb, indent1, Array.get(object, i));
			}
			sb.accept("}");
		} else if (object instanceof BigDecimal) {
			sb.accept("new BigDecimal(\"" + object.toString() + "\")");
		} else if (object instanceof Enum)
			sb.accept(clazz + "." + ((Enum<?>) object).name());
		else if (object instanceof Instant)
			sb.accept("Instant.ofEpochMilli(" + ((Instant) object).toEpochMilli() + "l)");
		else if (object instanceof List) {
			sb.accept("List.of(");
			var first = true;
			for (var c : ((Collection<?>) object)) {
				sb.accept((!first ? "," : "") + indent1);
				dump(sb, indent1, c);
				first = false;
			}
			sb.accept(")");
		} else if (object instanceof LocalDate) {
			sb.accept("LocalDate.of(");
			LocalDate ld = (LocalDate) object;
			dump(sb, indent, ld.getYear());
			sb.accept(", ");
			dump(sb, indent, ld.getMonthValue());
			sb.accept(", ");
			dump(sb, indent, ld.getDayOfMonth());
			sb.accept(")");
		} else if (object instanceof Map) {
			sb.accept("Map.ofEntries(");
			var first = true;
			for (var e : new TreeMap<>((Map<?, ?>) object).entrySet()) {
				sb.accept((!first ? "," : "") + indent1 + "Map.entry(");
				dump(sb, indent1, e.getKey());
				sb.accept(", ");
				dump(sb, indent1, e.getValue());
				sb.accept(")");
				first = false;
			}
			sb.accept(")");
		} else if (object instanceof Number)
			sb.accept(((Number) object).toString());
		else if (object instanceof Set) {
			sb.accept("Set.of(");
			var first = true;
			for (var c : new TreeSet<>((Collection<?>) object)) {
				sb.accept((!first ? "," : "") + indent1);
				dump(sb, indent1, c);
				first = false;
			}
			sb.accept(")");
		} else if (object instanceof String)
			sb.accept("\"" + ((String) object).replace("\"", "\\\"") + "\"");
		else if (object instanceof ZonedDateTime) {
			sb.accept("ZonedDateTime.ofInstant(");
			ZonedDateTime zdt = (ZonedDateTime) object;
			dump(sb, indent, zdt.toInstant());
			sb.accept(", ");
			dump(sb, indent, zdt.getZone());
			sb.accept(")");
		} else if (object instanceof ZoneId)
			sb.accept("ZoneId.of(\"" + ((ZoneId) object).toString() + "\")");
		else if (Boolean.TRUE) {
			String v = "r" + counter++;
			String className = clazz.getCanonicalName();

			sb.accept("((Supplier<" + className + ">) () -> {" //
					+ indent1 + "var " + v + " = new " + clazz.getCanonicalName() + "();");

			for (var f : clazz.getFields()) {
				Object child;
				try {
					child = f.get(object);
				} catch (Exception e) {
					child = e.getMessage();
				}
				sb.accept(indent1 + v + "." + f.getName() + " = " + Dump.dump(child) + ";");
			}

			sb.accept(indent1 + "return " + v + ";");
			sb.accept(indent + "}).get()");
		} else if (Boolean.TRUE)
			sb.accept("new " + clazz.getSimpleName() + "()");
		else
			throw new RuntimeException("cannot dump " + clazz);
	}

}
