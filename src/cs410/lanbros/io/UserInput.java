package cs410.lanbros.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import main.java.Main;

public class UserInput implements KeyListener {
	private static HashMap<KeyBind, Boolean> keyPressed = new HashMap<KeyBind, Boolean>();
	private static HashMap<String, HashMap<KeyBind, Boolean>> serverKeyPressed = new HashMap<String, HashMap<KeyBind, Boolean>>();

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		KeyBind key = KeyBind.getInputFor(e.getExtendedKeyCode());

		if (key != null) {
			if (!keyPressed.containsKey(key)) {
				keyPressed.put(key, true);
				if (Main.getNetworkFactory().getCurrentClient() != null)
					Main.getNetworkFactory().getCurrentClient().sendMovement(key, true);
			} else if (keyPressed.get(key) != true) {
				keyPressed.put(key, true);
				
				if (Main.getNetworkFactory().getCurrentClient() != null)
					Main.getNetworkFactory().getCurrentClient().sendMovement(key, true);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		KeyBind key = KeyBind.getInputFor(e.getExtendedKeyCode());

		if (key != null) {
			if (!keyPressed.containsKey(key)) {
				keyPressed.put(key, false);
				if (Main.getNetworkFactory().getCurrentClient() != null)
					Main.getNetworkFactory().getCurrentClient().sendMovement(key, false);
			} else if (keyPressed.get(key) != false) {
				keyPressed.put(key, false);
				if (Main.getNetworkFactory().getCurrentClient() != null)
					Main.getNetworkFactory().getCurrentClient().sendMovement(key, false);
			}
		}
	}

	/**
	 * Checks to see if the keybind is currently pressed.
	 * 
	 * @param bind the keybind to check
	 * @return true if it is pressed, false if not
	 */
	public static boolean isKeyBindPressed(KeyBind bind) {
		return keyPressed.get(bind) == null ? false : keyPressed.get(bind);
	}

	/**
	 * Sets the state of a keybind for a remote client, called by
	 * {@link ResponseRouter}.
	 * 
	 * @param user    the name of the user to modify
	 * @param bind    the keybind to modify
	 * @param pressed whether the key is pressed or not
	 */
	public static void setServerKeyPressed(String user, KeyBind bind, boolean pressed) {
		HashMap<KeyBind, Boolean> curKeys = serverKeyPressed.get(user);
		if (curKeys == null) {
			serverKeyPressed.put(user, new HashMap<KeyBind, Boolean>());
		}

		serverKeyPressed.get(user).put(bind, pressed);
	}

	/**
	 * Checks to see if the keybind is currently pressed.
	 * 
	 * @param bind the keybind to check
	 * @return true if it is pressed, false if not
	 */
	public static boolean isServerKeyBindPressed(String client, KeyBind bind) {
		return serverKeyPressed.get(client) == null ? false
				: serverKeyPressed.get(client).get(bind) == null ? false : serverKeyPressed.get(client).get(bind);
	}

}
