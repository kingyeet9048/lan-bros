package cs410.lanbros.animation;

import java.awt.Rectangle;

/**
 * A GUI element helper class to create and animate a list of frames into an animation. 
 * Has support for inheritance to allow for custom frame behaviors.
 * 
 * @author Ashton Schultz
 */
public class SpriteFrame 
{
	/**
	 * The string ID of this frame.
	 */
	protected final String frameID;
	
	/**
	 * A UV coordinate, specifying a rectangle in the source image of the SpriteSheet this frame is used in.
	 */
	protected int uvX, uvY, uvW, uvH;
	
	/**
	 * The amount of ingame ticks to render this frame for.
	 */
	protected int frameCount;
	
	/**
	 * The amount of ingame ticks to render this frame for. Used to reset {@link frameCount}.
	 */
	protected int maxFrameCount;
	
	/**
	 * Creates a new SpriteFrame for an animation in SpriteSheet.
	 * 
	 * @param id the string id of this frame.
	 * @param frameC The amount of repaints this frame will last.
	 * @param uvx the top-left x-coordinate of the source rect for this frame.
	 * @param uvy the top-left y-coordinate of the source rect for this frame.
	 * @param uvw the width of the source rect for this frame, offset by uvx.
	 * @param uvh the height of the source rect for this frame, offset by uvy.
	 */
	public SpriteFrame(String id, int frameC, int uvx, int uvy, int uvw, int uvh)
	{
		frameID = id;
		uvX = uvx;
		uvY = uvy;
		uvW = uvw;
		uvH = uvh;
		frameCount = frameC;
		maxFrameCount = frameC;
	}
	
	/**
	 * @return the source rectangle for the image this frame is used with.
	 */
	public Rectangle getSourceRect()
	{
		return new Rectangle(uvX,uvY,uvW,uvH);
	}
	
	/**
	 * A method to decrement the {@link frameCount}. Implemented to support inheritance for custom behavior.
	 */
	protected void updateCounter()
	{
		--frameCount;
	}
}