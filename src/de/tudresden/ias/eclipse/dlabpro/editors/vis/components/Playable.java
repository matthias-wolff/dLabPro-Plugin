package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;

import org.eclipse.jface.action.IAction;

/**
 * This interface is to be implemented by VisEditor data display classes which
 * are capable of playing back their data content on a sound device. 
 * @author Matthias Wolff
 */
public interface Playable
{

  /**
   * Determines if the playable can playback its current internal data.
   * @return <code>true</code> if playback is possible, <code>false</code>
   * otherwise
   */
  public boolean canPlay();

  /**
   * Determines if the playable is currenly playing back sound.
   * @return <code>true</code> if playback is running, <code>false</code>
   * otherwise
   */
  public boolean isPlaying();

  /**
   * Returns the playback sample rate in Hertz (integer).
   * @return The playback sample rate
   * @see getSrate()
   * /
  public int getPlaySrate();*/

  /**
   * Returns the actual sample rate in Hertz (integer).
   * @return The actual sample rate
   * @see getPlaySrate()
   * /
  public int getSrate();*/

  /**
   * 
   */
  public void play(IAction action);

  /**
   * 
   */
  public void stop(IAction action);

  /**
   * 
   * @param action
   */
  public void updatePlayAction(IAction action);
}
