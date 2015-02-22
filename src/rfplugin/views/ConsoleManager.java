package rfplugin.views;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleManager {

	public static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static MessageConsoleStream getMessageConsoleStream(String consoleName) {
		MessageConsole myConsole = findConsole(consoleName);
		MessageConsoleStream out = myConsole.newMessageStream();
		return out;
	}
}

// https://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F