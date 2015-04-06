
package de.tudresden.ias.eclipse.dlabpro.editors.vis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.osgi.framework.Bundle;

import de.tucottbus.kt.jlab.datadisplays.utils.DdUtils;
import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.utils.LaunchUtil;

/**
 * Provides a progress-monitorable shell to a x2xml.itp dLabPro file conversion script. This class is used by the
 * <code>VisEditor</code> in order to convert binary data files to XML for display.
 * 
 * @author Matthias Wolff
 */
public class X2XmlConverter implements IRunnableWithProgress
{
  public static final String T_DN3      = "-Tdn3";
  public static final String T_WAV      = "-Twav";
  public static final String T_CSV      = "-Tcsv_de";
  public static final String T_TXT      = "-Tascii";

  protected String           sFname     = null;
  protected String           sCnvtd     = null;
  protected String           sLog       = null;
  protected String           sFmt       = null;
  protected String           sDlpExe    = null;
  protected String           sDlpDir    = null;
  protected String           sX2xml     = null;

  public static final String M_SUBTASK = "Is this taking too long? Press [Cancel] to abort and see error log.\n"
                                       + "Does this dialog hang? Kill all processes named \"dLabPro\".";

  /**
   * Determines if the converter can process the specified file.
   * 
   * @param sFname
   *          Path to the binary data file to convert
   * @return A static string identifying the converter type or <code>null</code> if the file cannot be converted
   */
  public static String canConvert(String sFname)
  {
    if (sFname == null) return null;
    String sExt = sFname.substring(sFname.lastIndexOf('.'));
    if (sExt.equals(".xml")) return null;
    if (sExt.equals(".wav")) return T_WAV;
    if (sExt.equals(".txt")) return T_TXT;
    if (sExt.equals(".csv")) return T_CSV;
    return T_DN3;
  }

  /**
   * 
   * @param sFname
   *          Path to the binary data file to convert
   * @param sFmt
   *          Binary file format, one of the <code>T_XXX</code> constants
   */
  public X2XmlConverter(String sFname, String sFmt) throws IOException
  {
    this.sFname = sFname;
    this.sFmt = sFmt;

    // Get dLabPro binary path and file name
    sDlpExe = LaunchUtil.getDlabproExe(true).getAbsolutePath();
    if (sDlpExe==null)
      throw new IOException("Cannot find dLabPro executable");
    int nSplit = Math.max(sDlpExe.lastIndexOf('/'),sDlpExe.lastIndexOf('\\'));
    sDlpDir = sDlpExe.substring(0,nSplit);
    
    // Get converter script name
    Bundle iBundle  = DLabProPlugin.getDefault().getBundle();
    Path   iRelPath = new Path("scripts/X2XML.xtp"); 
    URL    iURL     = FileLocator.find(iBundle,iRelPath,null);
    sX2xml = FileLocator.getBundleFile(iBundle) + iURL.getPath();
  }

  /**
   * To be called <em>after</em> the converter has run in order to return the name of the converted XML file
   * 
   * @return Name of the converted XML file
   */
  public String getConvertedFileName()
  {
    return sCnvtd;
  }

  public String getLog()
  {
    return sLog;
  }

  public static String getFileType(String sFmt)
  {
    if (sFmt == null) return "???";
    if (sFmt.equals(T_DN3)) return "DNorm 3";
    if (sFmt.equals(T_WAV)) return "Wav";
    if (sFmt.equals(T_CSV)) return "Comma separated values";
    if (sFmt.equals(T_TXT)) return "Text";
    return "???";
  }

  private String readAvail(String s, InputStream iIs, int nLimit)
  {
    if (s != null && s.length() >= nLimit && nLimit > 0) return s;
    try
    {
      int nAvail = iIs.available();
      if (nAvail <= 0) return s;
      int nRead = nAvail;
      if (s != null && nLimit > 0) nRead = Math.min(nAvail,nLimit - s.length());
      for (int i = 0; i < nRead; i++)
        s += (char)iIs.read();
      return s;
    }
    catch (IOException e)
    {
      return s;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
   */
  public void run(IProgressMonitor monitor) throws InvocationTargetException,
      InterruptedException
  {
    monitor.beginTask("Converting " + getFileType(sFmt) + " file.",
        IProgressMonitor.UNKNOWN);
    monitor.subTask(M_SUBTASK);

    ProcessBuilder pb;
    Process p;
    try
    {
      File iTmpFile = File.createTempFile("VisEditor",null);
      String sTmpFile = iTmpFile.getAbsolutePath();

      String[] iArgs = new String[]
      { sDlpExe, sX2xml, sFmt, sFname.replace('\\','/'), sTmpFile };
      
      pb = new ProcessBuilder(iArgs);
      pb.directory(new File(sDlpDir));
      p = pb.start();
      sLog = "Calling    : ";
      for (int i = 0; i < iArgs.length; i++)
        sLog += (" " + iArgs[i]);
      sLog += "Working dir: " + sDlpDir + "\n";

      for (int nWait = 0;; nWait++)
        try
        {
          if (p.exitValue() == 0) sCnvtd = sTmpFile;
          sLog = readAvail(sLog,p.getInputStream(),6000);
          break;
        }
        catch (IllegalThreadStateException e1)
        {
          try
          {
            if (monitor.isCanceled()) p.destroy();
            sLog = readAvail(sLog,p.getInputStream(),6000);
            monitor.worked(1);
            Thread.sleep(100);
          }
          catch (InterruptedException e2)
          {
          }
        }
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("\nX2XML converter log:\n"+sLog+"\n");
      throw new Error();
    }
    DdUtils.MSG(sLog);
  }

}
