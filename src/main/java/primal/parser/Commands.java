package primal.parser;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.Map;

import primal.MoreVerbs.Read;
import primal.adt.Pair;

public class Commands<Command> {

	private Map<String, Command> commandByName;
	private int maxLength;

	public Commands(Command[] commands) {
		this(Read.from(commands).toMap(Command::toString));
	}

	public Commands(Map<String, Command> commandByName) {
		this.commandByName = commandByName;
		maxLength = 0;
		for (var name : commandByName.keySet())
			maxLength = max(maxLength, name.length());
	}

	public Pair<Command, String> recognize(String input) {
		return recognize(input, 0);
	}

	public Pair<Command, String> recognize(String input, int start) {
		for (var end = min(start + maxLength, input.length()); start <= end; end--) {
			var starts = input.substring(start, end);
			var command = commandByName.get(starts);
			if (command != null)
				return Pair.of(command, input.substring(end));
		}
		return null;
	}

}
