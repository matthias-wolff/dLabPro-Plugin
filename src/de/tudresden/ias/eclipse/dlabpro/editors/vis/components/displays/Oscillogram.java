// dLabPro Plugin for Eclipse
// - VisEditor oscillogram data display
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays;

import java.lang.reflect.Array;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.actions.PlayAction;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.Playable;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.events.DisplayInfoMouseMoveEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.DpiConverter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.NumberFormatter;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.RulerCalculator;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataCompInfo;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.data.DataException;
import de.tudresden.ias.eclipse.dlabpro.utils.SoundUtil;
import de.tudresden.ias.jlab.kernel.JlData;
import de.tudresden.ias.jlab.kernel.JlObject;

public class Oscillogram extends AbstractRvDataDisplay implements Playable {

	public Oscillogram(Composite iParent, int nStyle, DataCompInfo[] aDci,
			Ruler iHruler) throws DataException {
		super(iParent, aDci, iHruler);
	}

	// -- Info --

	/*
	 * (non-Javadoc)
	 */
	public static boolean canDisplay(DataCompInfo[] aDci) {
		JlData iData = aDci[0].iData;
		for (int nComp = 0; nComp < aDci.length; nComp++)
			if (!JlData.isNumericType(iData.getCompType(nComp)))
				return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 */
	public static String getIconFileName() {
		return "icons/obj16/osci_comp_obj.gif";
		//return ("icons/obj16/osci_obj.gif");
	}

	// -- Paining --

	/*
	 * (non-Javadoc)
	 */
	protected void paintData(GC iGc, Rectangle iDamage, RulerCalculator iHrc, RulerCalculator iVrc)
  // TODO: Consider painting on BufferedImage. This would help a lot quickening
  //       scrolling, overlaid markers, selections etc.
	{
    int size = iHrc.getLength();
    int shownRecords = m_nLastRec - m_nFirstRec;
    boolean bigPoints = (size / shownRecords) > VIS.OSCI_POINT_DISTANCE;
    boolean bNan;
    int step = shownRecords / size;
    if (step == 0) step = 1;
    Point[] current = null;
    Point[] last = null;

    int nDotX = (new DpiConverter(iGc.getDevice().getDPI().x)).pt2px(1.2);
    int nDotY = (new DpiConverter(iGc.getDevice().getDPI().y)).pt2px(1.2);    
    
    // Get direct references to data
    float [][] afComp = new float [m_nLastComp+1][];
    double[][] adComp = new double[m_nLastComp+1][];
    for (int j = m_nFirstComp, k=0; j <= m_nLastComp; j++, k++)
    {
      int comp = m_aDci[k].nComp;
      if (m_aDci[k].iData.getCompType(comp)==float.class)
        afComp[j]=(float[])m_aDci[k].iData.getComp(comp);
      else if (m_aDci[k].iData.getCompType(comp)==double.class)
        adComp[j]=(double[])m_aDci[k].iData.getComp(comp);
    }
    
    // iterate over records;
    for (int i = m_nFirstRec; i <= m_nLastRec; i += step)
    {
      int xPos = iHrc.getPosOfDataPoint(i);

      current = new Point[m_nLastComp - m_nFirstComp + 1];

      // iterate over components
      for (int j = m_nFirstComp, k = 0; j <= m_nLastComp; j++, k++)
      {
        if (!m_aDci[k].bVisible) continue;
        iGc.setForeground(m_iVcm.getCompFgColor(k));
        int comp = m_aDci[k].nComp;
        double val;

        if (step > 1)
        {
          double min;
          if      (afComp[j]!=null) min = afComp[j][i];
          else if (adComp[j]!=null) min = adComp[j][i];
          else                      min = m_aDci[k].iData.dFetch(i,comp);
          double max = min;
          bNan = Double.isNaN(max);
          for (int c = 1; c < step; c++)
          {
            try
            {
              if      (afComp[j]!=null) val = afComp[j][i+c];
              else if (adComp[j]!=null) val = adComp[j][i+c];
              else                      val = m_aDci[k].iData.dFetch(i+c,comp);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
              val = m_aDci[k].iData.dFetch(i+c,comp);
            }
            bNan &= Double.isNaN(val);
            if (val > max) max = val;
            if (val < min) min = val;
          }
          int yPos0 = iVrc.getPosOfVal(min);
          int yPos1 = iVrc.getPosOfVal(max);
          current[k] = bNan ? null : new Point(xPos,yPos0);

          if (!bNan) iGc.drawLine(xPos,yPos0,xPos,yPos1);
          if (last != null && last[k] != null && !bNan)
            iGc.drawLine(last[k].x,last[k].y,current[k].x,yPos1);
        }
        else
        {
          if      (afComp[j]!=null) val = afComp[j][i];
          else if (adComp[j]!=null) val = adComp[j][i];
          else                      val = m_aDci[k].iData.dFetch(i,comp);
          bNan = Double.isNaN(val);
          int yPos = iVrc.getPosOfVal(val);
          current[k] = bNan ? null : new Point(xPos,yPos);
          if (last != null && last[k]!=null && !bNan)
          {
            iGc.drawLine(last[k].x,last[k].y,current[k].x,current[k].y);
            if (bigPoints)
            {
              iGc.setBackground(m_iVcm.getCompFgColor(k));
              iGc.fillRectangle(current[k].x - nDotX,
                  current[k].y - nDotY, 2 * nDotX + 1,2 * nDotX + 1);
              iGc.setBackground(m_iVcm.getBgColor(getDisplay()));
            }
          }
          else if (!bNan)
          {
            if (bigPoints)
            {
              iGc.setBackground(m_iVcm.getCompBgColor(k));
              iGc.fillRectangle(current[k].x - nDotX,
                  current[k].y - nDotY, 2 * nDotX + 1,2 * nDotY + 1);
              iGc.setBackground(m_iVcm.getBgColor(getDisplay()));
            }
          }
        }
      }
      last = current;
      current = null;
    }
  }

	protected DisplayInfoMouseMoveEvent createInfoEventOnMouse(int x, int y)
  {
	  // Get defaults
    DisplayInfoMouseMoveEvent e = super.createInfoEventOnMouse(x,y);

    // Determine logical coordinates of nearest data point
    int nC = -1;
    int nR = m_iHruler.getDataPointOfPos(x);
    if (m_aDci.length == 1)
      nC = m_aDci[0].nComp;
    else
    {
      double cVal = 0.0;
      int pos = 0;
      int smallestDiff = Integer.MAX_VALUE;
      int diff = 0;
      for (int i = 0; i < m_aDci.length; i++)
      {
        if (!m_aDci[i].bVisible) continue;
        cVal = getJlData().dFetch(nR,m_aDci[i].nComp);
        pos = m_iVruler.getPosOfVal(cVal);
        diff = Math.abs(y - pos);
        if (diff < smallestDiff)
        {
          smallestDiff = diff;
          nC = m_aDci[i].nComp;
        }
      }
    }
    
    // Store Y (=value) axis information
    JlData iData = getJlData();
    String sValD = NumberFormatter.formatAndAdjust(iData.dFetch(nR,nC));
    if (sValD.startsWith("NaN")) sValD = "(no data)";
    String sData = new String(iData.getCompName(nC));
    if (sData.length()>0) sData = " \""+sData+"\"";
    sData = "comp. "+nC+sData; 
    e.StoreY(e.sYValA,sValD,e.sYUnit,sData);

    // Set tool tip text
    String sXval = NumberFormatter.formatAndAdjust(m_iHruler.getValOfPos(x));
    String sTtip = "R: "+nR+"\t"+sXval+" "+getRunit()+"\n"+
                   "C: "+nC+"\t"+sValD+" "+getVunit();
    setToolTipText(sTtip);
    
    return e;
  }

	// -- Sound Play Back (Implementation of Playable-Interface) --

	/**
	 * Playback thread for the (sound) data stored in <code>mData</code>. 
	 */
	class Player implements Runnable, LineListener {
		private boolean m_bRun = false;

		private Thread m_iThr = null;

		private SourceDataLine m_iSdl = null;

		private IAction m_iPla = null; // Play action

		private double m_nTi = 100.; // Playback time granularity (ms)

		private double m_nTs = 0.; // Playback start time (ms)

		private double m_nTe = 0.; // Playback end time (ms)

		/**
		 * Determines if sound is currently being played back.
		 * 
		 * @return <code>true</code> if playback is running, <code>false</code>
		 * otherwise
		 */
		public boolean isPlaying() {
			return m_bRun;
		}

		/**
		 * Starts playing sound. The method playes back the sound data stored in
		 * {@link mData} starting at sample position {@link mFirstShownRecord} and
		 * ending at sample position {@link mLastShownRecord}.
		 * 
		 * @param iPlayAction Handle to GUI controls reflecting the player's
		 * state, may be <code>null</code>.
		 */
		public void play(IAction iPlayAction) {
			double nSr = getPlaySrate();

			// Initialize sound system
			AudioFormat iAF = new AudioFormat(getPlaySrate(), 16, 1, true, true);
			m_iSdl = SoundUtil.getPlaybackDataline(iAF);
      m_iSdl.addLineListener(this);
      try
      {
        ((SourceDataLine)m_iSdl).open(iAF, m_iSdl.getBufferSize());
      }
      catch (LineUnavailableException e1)
      {
        e1.printStackTrace();
        return;
      }

			// Initialize player
			m_iPla = iPlayAction;
			nSr = getPlaySrate();
			m_nTs = 1000. / nSr * m_nFirstRec;
			m_nTe = 1000. / nSr * m_nLastRec;

			// Start play thread
			m_bRun = true;
			m_iThr = new Thread(this);
			m_iThr.start();
			updatePlayAction(m_iPla);
		}

		/**
		 * Stops the playback thread.
		 */
		public void stop() {
      m_bRun = false;
		}

		/**
		 * Cleans up players SourceDataLine
		 */
		public void finish() {
      m_iSdl.drain(); //    Wait for emtpy queue
      m_iSdl.stop(); //     Immediately stop data line
      m_iSdl.close();
      stop();
		}

      /**
		 * The playback thread. Implementation of <code>Runnable.run</code>
		 */
		public void run() {
			double nTime; // Real time (ms)
			double nSr = getPlaySrate(); // Playback sample rate
			int nSi = (int) (m_nTs / 1000. * nSr); // Current sample index
			double nAmp = getPlayAmp(); // Amplification
			short nSv = 0; // Current sample value
			byte[] aSv = new byte[m_iSdl.getBufferSize()]; // Sample value writing buffer
			int nPlayComp = 0;
			for (; nPlayComp<m_aDci.length; nPlayComp++)
			  if (m_aDci[nPlayComp].bVisible)
			    break;
			if (nPlayComp>=m_aDci.length) return;
			Object aData = m_aDci[0].iData.getComp(m_aDci[nPlayComp].nComp); // The data content of component 0
			long nLen = Array.getLength(aData); // Total number of samples 

			m_iSdl.start(); // Start data line
			for (nTime = m_nTs; (nTime < m_nTe) && m_bRun; nTime += m_nTi) // Go in buffers of m_nTi ms
			{ // >>
				int nSis = nSi; //   Remember start sample position
				int nSie = (int) (Math.min(nTime + m_nTi, m_nTe) / 1000. * nSr); //   Compute end sample position
				while (nSi < nSie) //   Loop over samples 
				{ //   >>
					if (nSi >= nLen) {
	          aSv[(nSi - nSis) * 2] = (byte) 0;
	          aSv[(nSi - nSis) * 2 + 1] = (byte) 0;
					} else {
					  nSv = (short) (nAmp * Array.getDouble(aData, nSi)); //     Get current sample value
					  aSv[(nSi - nSis) * 2] = (byte) (((nSv & 0xFF00) >> 8) & 0xFF); //     Store MSB
					  aSv[(nSi - nSis) * 2 + 1] = (byte) ((nSv & 0x00FF) & 0xFF); //     Store LSB
					}
					nSi++; //     Next sample
				} //   <<
				m_iSdl.write(aSv, 0, (nSi - nSis) * 2); //   Write samples to data line
			} // <<
			if(m_bRun) {
			  Arrays.fill(aSv, (byte)0);
	      m_iSdl.write(aSv, 0, (int)(m_nTi*nSr/1000.0)*2);
			}
			this.finish();
			updatePlayAction(m_iPla); // Notify GUI
      m_iThr = null; // Thread's going to end ...
		}

		/*
		 * (non-Javadoc)
		 * @see javax.sound.sampled.LineListener#update(javax.sound.sampled.LineEvent)
		 */
		public void update(LineEvent event) {
			JlObject.log(event.toString());
		}
	}

	private Player m_iPlayer = null;

	private double m_nPlayAmp = -1;

	/**
	 * Plays back the sound data stored in {@link mData}.
	 * 
	 * @param iPlayAction Handle to GUI controls reflecting the player's state,
	 * may be <code>null</code>.
	 */
	public void play(IAction iPlayAction) {
		if (m_iPlayer == null)
			m_iPlayer = new Player();
		if (m_iPlayer.isPlaying())
			m_iPlayer.stop();
		else
			m_iPlayer.play(iPlayAction);
	}

	/**
	 * Stops a running playback.
	 * 
	 * @param iPlayAction Handle to GUI controls reflecting the player's state,
	 * may be <code>null</code>.
	 */
	public void stop(IAction iPlayAction) {
		if (m_iPlayer != null)
			m_iPlayer.stop();
	}

	/**
	 * Determines if the data stored in {@link mData} are suitable for sound
	 * playback.
	 *
	 * @return <code>true</code> if sound playback is possible,
	 * <code>false</code> otherwise.
	 */
	public boolean canPlay() {
		return getPlayAmp() > 0.;
	}

	/**
	 * Determines if sound is currently being played back.
	 *
	 * @return <code>true</code> if playback is running, <code>false</code>
	 * otherwise
	 */
	public boolean isPlaying() {
		return (m_iPlayer != null && m_iPlayer.isPlaying());
	}

	/**
	 * Determines the actual sample rate of the oscillogram data stored in
	 * {@link mData}.
	 * 
	 * @return The actual sample rate
	 * @see getPlaySrate
	 */
	public int getSrate() {
		if (m_aDci[0].iData.rinc == 0)
			return 0;
		return (int) (1000. / m_aDci[0].iData.rinc);
	}

	/**
	 * Determines the playback sample rate of the oscillogram data stored in
	 * {@link mData}.
	 * 
	 * @return The playback sample rate in Hz (8000, 11025, 16000, 22050, 24000,
	 * 32000, 44100, or 48000) or 0, if the data are not suitable for playback.
	 * @see getSrate
	 */
	public int getPlaySrate() {
		int nSrate = getSrate();
		if (nSrate < 1000)
			return 0; //      <  1.0 kHz -> ---
		if (nSrate < 9500)
			return 8000; //  1.0 -  9.5 kHz -> 8 kHz
		if (nSrate < 13500)
			return 11025; //  9.5 - 13.5 kHz -> 11.025 kHz
		if (nSrate < 19000)
			return 16000; // 13.5 - 19.0 kHz -> 16 kHz
		if (nSrate < 23000)
			return 22050; // 19.0 - 23.0 kHz -> 22.05 kHz
		if (nSrate < 28000)
			return 24000; // 23.0 - 28.0 kHz -> 24.0 kHz
		if (nSrate < 38000)
			return 32000; // 28.0 - 38.0 kHz -> 32.0 kHz
    if (nSrate < 45500)
      return 44100; // 38.0 - 44.5 kHz -> 44.1 kHz
		return 48000; //      > 44.5 kHz -> 48.0 kHz
	}

	/**
	 * Determines a suitable amplification factor for playing back the
	 * oscillogram data stored in {@link mData}. 
	 * 
	 * @return The amplification (1, 128 or 32768) or 0, if the data are not
	 * suitable for playback.
	 */
	public double getPlayAmp() {
    if (m_nPlayAmp>=0) return m_nPlayAmp;
    if (m_aDci == null && m_aDci.length == 0)
    {
      m_nPlayAmp = 0.;
      return 0.;
    }

		// Seek playable component
		int nOnlyVisible = -1;
		for (int i=0; i<m_aDci.length; i++)
		  if (m_aDci[i].bVisible)
		  {
		    nOnlyVisible = i;
		    break;
		  }
		if (nOnlyVisible<0)
		{
      // None visible
      m_nPlayAmp=0.;
      return 0.;
		}
		
		// TODO: use min/max in m_aDci[0]!
		double nMin = Double.MAX_VALUE;
		double nMax = Double.MIN_VALUE;

		Object aData = m_aDci[0].iData.getComp(m_aDci[nOnlyVisible].nComp);
		for (int nSi = 0; nSi < Array.getLength(aData); nSi++) {
			double nSv = Array.getDouble(aData, nSi);
			if (nSv > nMax)
				nMax = nSv;
			if (nSv < nMin)
				nMin = nSv;
		}

		if (nMin >= -0.0001 && nMax <= 0.0001)
			m_nPlayAmp = 0.;
		else if (nMin >= -1.01 && nMax <= 1.01)
			m_nPlayAmp = 32768.;
		else if (nMin >= -128 && nMax <= 128)
			m_nPlayAmp = 128.;
		else if (nMin >= -32768 && nMax <= 32768)
			m_nPlayAmp = 1.;
		else
			m_nPlayAmp = 0.;
		return m_nPlayAmp;
	}

	/**
	 * Reflects the playback state at the GUI.
	 * 
	 * @param iPlayAction Handle to GUI controls reflecting the player's state,
	 * may be <code>null</code>.
	 */
	public void updatePlayAction(IAction action) {
		if (action == null)
			return;
		String sMsg = "";
		if (isPlaying())
			PlayAction.setStop(action);
		else {
			if (getSrate() != getPlaySrate())
				sMsg = "changing sample rate from " + (getSrate() / 1000.)
						+ " kHz to " + (getPlaySrate() / 1000.) + " kHz";
			if (sMsg.length() == 0)
				PlayAction.setPlay(action);
			else
				PlayAction.setPlayWarning(action, sMsg);
		}
	}

}

// EOF

