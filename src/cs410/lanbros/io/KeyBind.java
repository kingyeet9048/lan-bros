package cs410.lanbros.io;

import java.awt.event.KeyEvent;

public enum KeyBind 
{
	LEFT(KeyEvent.VK_A, KeyEvent.VK_LEFT),
	RIGHT(KeyEvent.VK_D, KeyEvent.VK_RIGHT),
	JUMP(KeyEvent.VK_W, KeyEvent.VK_SPACE),
	PAUSE(KeyEvent.VK_ESCAPE);
	public int[] keyCodes;
	
	private KeyBind(int... keycode)
	{
		keyCodes = keycode;
	}
	
	/**
	 * Retrieves the input based on the key that was pressed.
	 * @param keycode the key to check for
	 * @return the keybind associated with the key, or null if it doesn't exist.
	 */
	public static KeyBind getInputFor(int keycode)
	{
		for(KeyBind key: values())
		{
			for(int code : key.keyCodes)
			{
				if(code == keycode)
				{
					return key;
				}
			}
		}
		
		return null;
	}
}
