package cs410.lanbros.gui;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

/**
 * A GUI element to create and animate a list of frames into an animation. 
 * Has support for multiple animations, as well as inheritance.
 * 
 * @author Ashton Schultz
 */
public class SpriteSheet
{
	/**
	 * The image this SpriteSheet is splitting up.
	 */
	public final ImageIcon animImage;
	
	/**
	 * The map of registered stringIDs for this SpriteSheet to their {@link SpriteFrame}.
	 */
	protected final HashMap<String, SpriteFrame> frameMap;

	/**
	 * The map of registered animations for this SpriteSheet to a list of {@link SpriteFrame} for the animation.
	 */
	protected final HashMap<String, List<String>> animMap;
	
	/**
	 * The current animation id.
	 */
	protected String curAnimID;

	/**
	 * The frames left in the current animation.
	 */
	protected ArrayList<String> framesLeft = new ArrayList<String>(); 	
	
	/**
	 * The current frame being acted on.
	 */
	private SpriteFrame curFrame;

	/**
	 * Whether or not the animation should continue updating.
	 */
	protected boolean isPaused;

	/**
	 * Creates a new SpriteSheet object for the specified image.
	 * @param image the source image to use for this spritesheet.
	 */
	public SpriteSheet(ImageIcon image)
	{
		frameMap = new HashMap<String, SpriteFrame>();
		animMap = new HashMap<String, List<String>>();
		animImage = image;
		isPaused = false;
		addDefaultState();
	}
	
	/**
	 * A helper function to ensure that when the SpriteSheet constructs, or the frames or animations are cleared, there will always be something rendered.
	 */
	private void addDefaultState()
	{
		addFrame("default", -1, 0, 0, animImage.getIconWidth(), animImage.getIconHeight()); 
	}
	
	/**
	 * Adds a frame with the given parameters to the frame map for this SpriteSheet. Intended to be called upon construction of SpriteSheet.
	 * @param id the string id of this frame.
	 * @param frameCount the amount of frames this frame should last. If set to -1, any animation that uses this frame will stop updating on this frame.
	 * @param uvx the top left corner of the rendered area of this frame, in pixels.
	 * @param uvy the top left corner of the rendered area of this frame, in pixels.
	 * @param uvw the width of the rendered area of this frame, in pixels. Offset by uvx.
	 * @param uvh the height of the rendered area of this frame, in pixels. Offset by uvy.
	 * @return the current SpriteSheet object.
	 */
	public SpriteSheet addFrame(String id, int frameCount, int uvx, int uvy, int uvw, int uvh)
	{
		return addFrame(new SpriteFrame(id, frameCount, uvx, uvy, uvw, uvh));	
	}

	/**
	 * A function to enable inheriting {@link SpriteFrame} for custom frame behavior. Intended to be called upon construction of SpriteSheet.
	 * @param frame the frame to add to the frame map of this SpriteSheet.
	 * @return the current SpriteSheet object.
	 */
	public SpriteSheet addFrame(SpriteFrame frame)
	{
		frameMap.remove(frame.frameID);
		frameMap.put(frame.frameID, frame);

		//add a default animation in case there is no animations setup
		return addAnimation("default", frame.frameID);	
	}
	
	/**
	 * Wipes all registered frames from this SpriteSheet object.
	 */
	public void clearFrames()
	{
		frameMap.clear();
		addDefaultState();
	}
	
	/**
	 * Adds a registered animation to the animation map of this SpriteSheet with the list of frame IDs, in order. Intended to be called upon construction of SpriteSheet.
	 * @param animID the id of the animation
	 * @param frameIDs the list of frameIDs, in order, that this animation possesses.
	 * @return the current SpriteSheet.
	 */
	public SpriteSheet addAnimation(String animID, String... frameIDs)
	{
		animMap.remove(animID);
		animMap.put(animID, Arrays.asList(frameIDs));
		curAnimID = animID;
		
		if(frameIDs.length > 0)
		{
			curFrame = frameMap.get(frameIDs[0]);
			curFrame.frameCount = curFrame.maxFrameCount;
		}
		
		return this;
	}
	
	/**
	 * Wipes all registered frames from this SpriteSheet object.
	 */
	public void clearAnimations()
	{
		animMap.clear();
		addDefaultState();
	}

	
	/**
	 * Sets the current animation to the specified animation. 
	 * 
	 * @param animID the animation to start playing
	 * @return true if the animation was able to be found, false if animation does not exist.
	 */
	public boolean setCurrentAnimation(String animID)
	{
		if(animMap.containsKey(animID))
		{
			curAnimID = animID;
			//Initialize the working frame
			curFrame = frameMap.get(framesLeft.get(0));	
			curFrame.frameCount = curFrame.maxFrameCount;
			return true;
		}

		return false;
	}
	
	/**
	 * Sets the currently displayed frame of this SpriteSheet. This will freeze any animations currently playing. 
	 * If the frameID is not registered, this function does nothing.
	 * 
	 * @param frameID the string ID of the frame to render.
	 * @return true if the frame exists, false if not.
	 */
	public boolean setCurrentFrame(String frameID)
	{
		if(frameMap.containsKey(frameID))
		{
			curFrame = frameMap.get(frameID);
			curFrame.frameCount = -1;
			return true;
		}

		return false;
	}
	
	/**
	 * Sets the animation state of this SpriteSheet.
	 * 
	 * @param paused true to pause the animation, false to update the animation
	 */
	public void setPaused(boolean paused)
	{
		isPaused = paused;
	}
	
	/**
	 * @return whether the animation is paused or not.
	 */
	public boolean isPaused()
	{
		return isPaused;
	}

	/**
	 * Updates the current animation of this SpriteSheet.
	 */
	public void updateSpriteSheet()
	{
		if(curAnimID == null)
		{
			addDefaultState();
		}
		
		if(framesLeft.size() == 0) //animation is done
		{
			//so get a new copy of the frames.
			Collections.addAll(framesLeft, animMap.get(curAnimID).toArray(new String[] {}));
		}
		
		if(curFrame == null)
		{
			//set the current frame to the first frame in the list of remaining frames of the animation.
			curFrame = frameMap.get(framesLeft.get(0));	
			//and then reset frame lifetime to the maximum lifetime.
			curFrame.frameCount = curFrame.maxFrameCount;
		}
		
		//check for >= 0, because if -1 then permanent frame.
		if(curFrame.frameCount >= 0 && !isPaused)
		{
			curFrame.updateCounter();
			
			if(curFrame.frameCount == 0) //means frame duration was reached
			{
				//remove the working frame, set current frame to next frame
				framesLeft.remove(0);
				
				if(framesLeft.size() == 0)
				{
					//so get a new copy of the frames.
					Collections.addAll(framesLeft, animMap.get(curAnimID).toArray(new String[] {}));
				}

				curFrame = frameMap.get(framesLeft.get(0));
				//and then reset frame lifetime to the maximum lifetime.
				curFrame.frameCount = curFrame.maxFrameCount;					
			}
		}
	}
	
	/**
	 * Renders the spritesheet's current animation (or default, if none specified) to the specified coordinates.
	 * @param graphics the Graphics2D object to render this SpriteSheet to.
	 * @param x the x-coordinate, in pixels, to render at.
	 * @param y the y-coordinate, in pixels, to render at.
	 */
	public void renderSpriteSheet(Graphics2D graphics, float x, float y)
	{
		renderSpriteSheet(graphics, x, y, 1, 1);
	}
	
	/**
	 * Renders the spritesheet's current animation (or default, if none specified) to the specified coordinates at the specified scale.
	 * @param graphics the Graphics2D object to render this SpriteSheet to.
	 * @param x the x-coordinate, in pixels, to render at.
	 * @param y the y-coordinate, in pixels, to render at.
	 * @param xScale the x-scale, where 1.0 is 100%, to render the SpriteSheet at.
	 * @param yScale the y-scale, where 1.0 is 100%, to render the SpriteSheet at.
	 */
	public void renderSpriteSheet(Graphics2D graphics, float x, float y, float xScale, float yScale)
	{
		renderSpriteSheet(graphics, x, y, xScale, yScale, 0, 0, 0);
	}

	/**
	 * Renders the spritesheet's current animation (or default, if none specified) to the specified coordinates at the specified scale.
	 * @param graphics the Graphics2D object to render this SpriteSheet to.
	 * @param x the x-coordinate, in pixels, to render at.
	 * @param y the y-coordinate, in pixels, to render at.
	 * @param xScale the x-scale, where 1.0 is 100%, to render the SpriteSheet at.
	 * @param yScale the y-scale, where 1.0 is 100%, to render the SpriteSheet at.
	 * @param rotation the rotation of the SpriteSheet, in radians.
	 * @param xPivot the x-coordinate of the pivot point for rotation, centered on the SpriteSheet.
	 * @param yPivot the y-coordinate of the pivot point for rotation, centered on the SpriteSheet.
	 */
	public void renderSpriteSheet(Graphics2D graphics, float x, float y, float xScale, float yScale, float rotation, float xPivot, float yPivot)
	{
		if(curFrame != null)
		{
			int width = (int)(curFrame.uvW * xScale);
			int height = (int)(curFrame.uvH * yScale);
			graphics.rotate(rotation, xPivot+x, yPivot+y);
			graphics.drawImage(animImage.getImage(), (int)(x-width/2), (int)(y-height/2), (int)(x+width/2), (int)(y+height/2), curFrame.uvX, curFrame.uvY, curFrame.uvX+curFrame.uvW, curFrame.uvY+curFrame.uvH, null);
			graphics.rotate(-rotation, xPivot+x, yPivot+y);
		}
	}
	
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
		 * A method to decrement the {@link frameCount}. Implemented to support inheritance for custom behavior.
		 */
		protected void updateCounter()
		{
			--frameCount;
		}
	}
}
