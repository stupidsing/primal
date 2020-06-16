package primal.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import primal.MoreVerbs.Read;
import primal.adt.Pair;

/**
 * Non-recursive, performance-improved parser for operator-based languages.
 *
 * @author ywsing
 */
public class Lexer {

	private Commands<Operator> commands;
	private Operator spaceOperator;
	private int spacePrecedence;

	private String in;
	private int pos = 0;
	private Token token0;

	public enum LexType {
		CHAR__, HEX__, ID___, OPER_, SPACE, STR__, SYM__,
	}

	public class Token {
		public final LexType type;
		public final Operator operator;
		private String data;

		private Token(LexType type, Operator operator) {
			this.type = type;
			this.operator = operator;
		}

		public String getData() {
			return data;
		}
	}

	public Lexer(Operator[] operators, Operator spaceOperator, String in) {
		this.spaceOperator = spaceOperator;
		this.spacePrecedence = spaceOperator.precedence();
		this.in = in;

		var operatorByName = Read //
				.from(operators) //
				.filter(operator -> operator != spaceOperator) //
				.toMap(Operator::name_);

		commands = new Commands<>(operatorByName);
	}

	public Token lex() {
		return token0 = lex_();
	}

	private Token lex_() {
		if (pos < in.length()) {
			var start = pos;
			var token = detect();
			var type = token.type;

			if (type == LexType.ID___ || type == LexType.SPACE)
				while (pos < in.length() && detect().type == type)
					pos++;
			else if (type == LexType.CHAR__)
				pos += 4;
			else if (type == LexType.HEX__) {
				pos += 2;
				while (pos < in.length() && 0 <= "0123456789ABCDEF".indexOf(in.charAt(pos)))
					pos++;
			} else if (type == LexType.OPER_)
				pos += token.operator.name_().length();
			else if (type == LexType.STR__) {
				var quote = in.charAt(pos);
				while (pos < in.length() && in.charAt(pos) == quote) {
					pos++;
					while (pos < in.length() && in.charAt(pos) != quote)
						pos++;
					pos++;
				}
			} else if (type == LexType.SYM__)
				pos++;

			var data = in.substring(start, pos);

			if (type == LexType.SPACE) {
				var precs = new ArrayList<Integer>();

				for (var t : List.of(token0, detect()))
					if (t != null && t.operator != null)
						precs.add(t.operator.precedence());

				if (!precs.isEmpty() && spacePrecedence < Collections.min(precs)) {
					token = new Token(LexType.OPER_, spaceOperator);
					token.data = data;
				} else
					token = lex_();
			} else
				token.data = data;

			return token;
		} else
			return null;
	}

	private Token detect() {
		var operator = Pair.fst(commands.recognize(in, pos));
		LexType type;

		if (pos < in.length()) {
			var ch = in.charAt(pos);

			if (operator != null)
				type = LexType.OPER_;
			else if (ch == '+' && pos + 4 <= in.length() && in.charAt(pos + 1) == '\'')
				type = LexType.CHAR__;
			else if (ch == '+' && pos + 2 <= in.length() && in.charAt(pos + 1) == 'x')
				type = LexType.HEX__;
			else if (ch == ' ')
				type = LexType.SPACE;
			else if (ch == '\'' || ch == '"')
				type = LexType.STR__;
			else if (ch == '(' || ch == '[' || ch == '{' //
					|| ch == ')' || ch == ']' || ch == '}' //
					|| ch == '`')
				type = LexType.SYM__;
			else
				type = LexType.ID___;
		} else
			type = null;

		return new Token(type, operator);
	}

}
