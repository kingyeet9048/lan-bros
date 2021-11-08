package cs410.lanbros.io;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class UserInput implements KeyListener
{
	private static HashMap<KeyBind, Boolean> keyPressed = new HashMap<KeyBind,Boolean>();
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		KeyBind key = KeyBind.getInputFor(e.getExtendedKeyCode());
		
		if(key != null)
		{
			keyPressed.put(key, true);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		KeyBind key = KeyBind.getInputFor(e.getExtendedKeyCode());
		
		if(key != null)
		{
			keyPressed.put(key, false);
		}		
	}
	
	/**
	 * Checks to see if the keybind is currently pressed.
	 * @param bind the keybind to check
	 * @return true if it is pressed, false if not
	 */
	public static boolean isKeyBindPressed(KeyBind bind)
	{
		return keyPressed.get(bind) == null ? false : keyPressed.get(bind);
	}

}
