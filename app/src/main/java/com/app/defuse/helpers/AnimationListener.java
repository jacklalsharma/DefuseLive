package com.app.defuse.helpers;

/**
 * This interface is a custom listener to determine the end of an animation.
 * 
 * @author Phu
 * 
 */
public interface AnimationListener {

	/**
	 * This method is called when the animation ends.
	 * 
	 * @param animation
	 *            The Animation object.
	 */
	public void onAnimationStart(Animation animation);
	
	public void onAnimationEnd(Animation animation);
}