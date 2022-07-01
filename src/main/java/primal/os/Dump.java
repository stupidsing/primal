package primal.os;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
		new Dump().dump(sb::append, " //\n", object);
		return sb.toString();
	}

	private Set<Integer> ids = new HashSet<>();

	/**
	 * Assumes set and map keys are comparable for a consistent sorting order.
	 */
	private void dump(Consumer<String> sb, String indent, Object object) {
		var id = System.identityHashCode(object);
		if (ids.add(id))
			try {
				dump_(sb, indent, object);
			} finally {
				ids.remove(id);
			}
		else
			sb.accept("<recurse>");
	}

	private void dump_(Consumer<String> sb, String indent, Object object) {
		var clazz = object != null ? object.getClass() : null;
		var indent1 = indent + "\t";

		if (object == null)
			sb.accept("null");
		else if (clazz.isArray()) {
			sb.accept("new Object[] {");
			for (var i = 0; i < Array.getLength(object); i++) {
				sb.accept((i != 0 ? "," : "") + indent1);
				dump(sb, indent1, Array.get(object, i));
			}
			sb.accept("}");
		} else if (BigDecimal.class.isAssignableFrom(clazz))
			sb.accept("new BigDecimal(\"" + object.toString() + "\")");
		else if (Class.class.isAssignableFrom(clazz))
			sb.accept( ((Class<?>) object).getName() + ".class");
		else if (Enum.class.isAssignableFrom(clazz))
			sb.accept(clazz + "." + ((Enum<?>) object).name());
		else if (Instant.class.isAssignableFrom(clazz))
			sb.accept("Instant.ofEpochMilli(" + ((Instant) object).toEpochMilli() + "l)");
		else if (List.class.isAssignableFrom(clazz)) {
			sb.accept("List.of(");
			var first = true;
			for (var c : ((Collection<?>) object)) {
				sb.accept((!first ? "," : "") + indent1);
				dump(sb, indent1, c);
				first = false;
			}
			sb.accept(")");
		} else if (LocalDate.class.isAssignableFrom(clazz)) {
			sb.accept("LocalDate.of(");
			var ld = (LocalDate) object;
			dump(sb, indent, ld.getYear());
			sb.accept(", ");
			dump(sb, indent, ld.getMonthValue());
			sb.accept(", ");
			dump(sb, indent, ld.getDayOfMonth());
			sb.accept(")");
		} else if (Map.class.isAssignableFrom(clazz)) {
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
		} else if (Number.class.isAssignableFrom(clazz))
			sb.accept(((Number) object).toString());
		else if (Set.class.isAssignableFrom(clazz)) {
			sb.accept("Set.of(");
			var first = true;
			for (var c : new TreeSet<>((Collection<?>) object)) {
				sb.accept((!first ? "," : "") + indent1);
				dump(sb, indent1, c);
				first = false;
			}
			sb.accept(")");
		} else if (String.class.isAssignableFrom(clazz))
			sb.accept("\"" + ((String) object).replace("\"", "\\\"") + "\"");
		else if (ZonedDateTime.class.isAssignableFrom(clazz)) {
			sb.accept("ZonedDateTime.ofInstant(");
			var zdt = (ZonedDateTime) object;
			dump(sb, indent, zdt.toInstant());
			sb.accept(", ");
			dump(sb, indent, zdt.getZone());
			sb.accept(")");
		} else if (ZoneId.class.isAssignableFrom(clazz))
			sb.accept("ZoneId.of(\"" + object + "\")");
		else if (Boolean.TRUE) {
			var v = "r" + counter++;
			var className = clazz.getCanonicalName();
			var fields = new HashSet<String>();

			sb.accept("((Supplier<" + className + ">) () -> {" //
					+ indent1 + "var " + v + " = new " + clazz.getCanonicalName() + "();");

			for (var f : clazz.getFields()) {
				if (fields.add(f.getName().toLowerCase(Locale.ROOT))) {
					Object child;
					try {
						child = f.get(object);
					} catch (Exception e) {
						child = e.getMessage();
					}
					sb.accept(indent1 + v + "." + f.getName() + " = ");
					dump(sb, indent1, child);
					sb.accept(";");
				}
			}

			for (var m : clazz.getMethods()) {
				var methodName = m.getName();
				var key = methodName.substring(3);
				if (methodName.startsWith("get") //
						&& m.getParameterCount() == 0 //
						&& fields.add(key.toLowerCase(Locale.ROOT))) {
					Object child;
					try {
						child = m.invoke(object);
					} catch (Exception e) {
						child = e.getMessage();
					}
					sb.accept(indent1 + v + ".set" + key + "(");
					dump(sb, indent1, child);
					sb.accept(");");
				}
			}

			sb.accept(indent1 + "return " + v + ";");
			sb.accept(indent + "}).get()");
		} else if (Boolean.TRUE)
			sb.accept("new " + clazz.getSimpleName() + "()");
		else
			throw new RuntimeException("cannot dump " + clazz);
	}

}
