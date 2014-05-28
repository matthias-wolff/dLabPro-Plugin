package de.tudresden.ias.eclipse.dlabpro.utils;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.jface.preference.IPreferenceStore;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.preferences.IVisEditorPreferenceConstants;

public class SoundUtil implements IVisEditorPreferenceConstants
{
  /**
   * Returns an array of {@link javax.sound.sampled.Mixer}s capable of playing
   * back sound. If there are no suitable mixers, the method returns an empty
   * array.
   */
  public static Mixer[] getPlaybackMixers()
  {
    ArrayList<Mixer> liDevs = new ArrayList<Mixer>();
    
    AudioFormat iAF = new AudioFormat(16000, 16, 1, true, true);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, iAF,
        AudioSystem.NOT_SPECIFIED);
    Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
    for (int i = 0; i < mixerInfo.length; i++)
    {
      Mixer m = AudioSystem.getMixer(mixerInfo[i]);
      Line.Info[] sourceInfo = m.getSourceLineInfo();
      for (int j = 0; j < sourceInfo.length; j++)
      {
        if (sourceInfo[j].getLineClass().equals(SourceDataLine.class))
          try
          {
            m.getLine(info); // Just see if it passes without throwing an exception!
            liDevs.add(m); // This mixer has a suitable playback data line
            break;
          }
          catch (Exception e)
          {
            // Ignored!
          }
      }
    }

    return liDevs.toArray(new Mixer[]{});
  }
  
  /**
   * Returns a playback data line on a particular mixer.
   * 
   * @param iAf
   *          The desired audio format of the data line
   * @param sMixer
   *          The name of the mixer (as returned by {@link
   *          javax.sound.sampled.Mixer#getMixerInfo()}<code>.getName()</code>)
   *          or <code>null</code> for any data line. If no mixer of this name
   *          exists, the method will return the first suitable data line it can
   *          find. On Windows systems the line will probably be on the primary
   *          sound device, on other systems the result may be arbitrary. 
   * @return A playback data line of the given mixer suitable for rendering the
   *         given audio format or <code>null</code> there is no suitable data
   *         line. Note: the method does not check whether the line can be
   *         opened!
   */
  public static SourceDataLine getPlaybackDataline(AudioFormat iAf, String sMixer)
  {
    SourceDataLine iFirstLine = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, iAf, AudioSystem.NOT_SPECIFIED);
    Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
    for (int i = 0; i < mixerInfo.length; i++)
    {
      Mixer m = AudioSystem.getMixer(mixerInfo[i]);
      Line.Info[] sourceInfo = m.getSourceLineInfo();
      for (int j = 0; j < sourceInfo.length; j++)
      {
        if (sourceInfo[j].getLineClass().equals(SourceDataLine.class))
          try
          {
            SourceDataLine iLine = (SourceDataLine)m.getLine(info);
            if (m.getMixerInfo().getName().equals(sMixer)) return iLine;
            if (iFirstLine==null) iFirstLine = iLine;
            break;
          }
          catch (Exception e)
          {
            // Ignored!
          }
      }
    }
    return iFirstLine;
  }
  
  /**
   * Returns a playback data line on the mixer selected on the plugin-in's
   * preference page.
   * @param iAf
   *          The desired audio format of the data line
   * @return A playback data line or <code>null</code> of no suitable line can
   *         be found. Note: the method does not check whether the line can be
   *         opened!
   */
  public static SourceDataLine getPlaybackDataline(AudioFormat iAf)
  {
    try
    {
      IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
      return getPlaybackDataline(iAf,iStore.getString(P_VIS_SNDPLAYDEV));
    }
    catch (NullPointerException e) {}
    return getPlaybackDataline(iAf,null);
  }
}