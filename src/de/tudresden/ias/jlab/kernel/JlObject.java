/* jLab type JlInstance
 * - Base class to all jLab modules
 *
 * AUTHOR  : Matthias Wolff
 * UPDATE  : $Date: 2013-02-18 12:00:19 +0100 (Mo, 18 Feb 2013) $, $Author: wolff $
 *           $Revision: 181 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlObject.java 181 2013-02-18 11:00:19Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * @author Matthias Wolff
 */
public abstract class JlObject implements Cloneable, Serializable
{

  private static final long serialVersionUID = 1L;

  /**
   * location of the logfile
   */
  private static String logFile = null;

  /**
   * Log flag
   */
  private static boolean logOn = true;

  /**
   * Error counter
   */
  private static int nErr = 0;

  /**
   * Warning counter
   */
  private static int nWrn = 0;
  
  /**
   * Toggles logging.
   * 
   * @param bOn
   *          <code>true</code> to switch logging on, <code>false</code> to
   *          switch it off
   */
  public static void setLogOn(boolean bOn)
  {
    JlObject.logOn = bOn;
  }
  
  /**
   * Set a logfile for this class.
   * 
   * @param filename
   *          location and filename, relative to working directory
   */
  public static void setLogFile(String filename)
  {
    logFile = filename;
    File f = new File(logFile);
    if (f.exists())
    {
      f.delete();
    }
  }

  /**
   * Append <code>s</code> to logfile, if logfile is set.
   * 
   * @param s
   */
  private static void writeLogFile(String s)
  {
    if (logFile != null && logFile.length() > 0) try
    {
      FileWriter fw = new FileWriter(logFile, true);
      fw.write(s);
      fw.write("\n");
      fw.close();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * Convenience method to store short arrays in a text file. Values are separated by new line '\n'.
   * 
   * @param file
   *          textfile to write the data into
   * @param array
   *          data array
   */
  public static void log(String file, short[] f)
  {
    if (!JlObject.logOn) return;
    StringBuffer s = new StringBuffer();
    if (f != null)
    {
      s.append(f[0]);
      for (int i = 1; i < f.length; s.append("\n").append(f[i++]))
      {
      }
    }
    // s.append("\n");

    log(file, s.toString());
  }

  /**
   * @param file
   *          textfile to write the data into
   * @param msg
   *          text
   */
  public static void log(String file, String msg)
  {
    if (!JlObject.logOn) return;
    try
    {
      FileWriter fw = new FileWriter(file, true);
      fw.write(msg);
      fw.close();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }

  /**
   * Prints a message to the command line and into a log file (if a log file is
   * set).
   * 
   * @param sMsg
   *          The message.
   */
  public static void log(String sMsg)
  {
    System.out.print(sMsg);
    System.out.flush();
    writeLogFile(sMsg);
  }

  /**
   * Prints a formatted message to the command line and into a log file (if a
   * log file is set).
   * 
   * @param sMsg
   *          The message (format string).
   * @param args
   *          The arguments.
   */
  public static void log(String sMsg, Object ... args)
  {
    if (!JlObject.logOn) return;
    sMsg = String.format(Locale.ENGLISH,sMsg,args);
    log(sMsg);
  }

  /**
   * Print and log an error.
   * 
   * @param sMsg
   */
  public static void ERROR(String sMsg)
  {
    String sErr =
      "\n"+at()[2].getFileName()+"("+at()[2].getLineNumber()+") ERROR: "
      +sMsg+"\n";
    nErr++;
    System.err.print(sErr);
  }

  /**
   * Print and log a warning.
   * 
   * @param sMsg
   */
  public static void WARNING(String sMsg)
  {
    String sWrn =
      "\n"+at()[2].getFileName()+"("+at()[2].getLineNumber()+") WARNING: "
      +sMsg+"\n";
    nWrn++;
    System.err.print(sWrn);
  }

  public static StackTraceElement[] at()
  {
    final Exception e = new Exception();
    return e.getStackTrace();
  }
  
  /**
   * Prints <code>Throwable</code> to the command line and into a logfile, if logfile is set.
   * 
   * @param iThrowable
   */
  public static void log(Throwable iThrowable)
  {
    if (!JlObject.logOn) return;    
    iThrowable.printStackTrace();

    StringWriter sw = new StringWriter();
    iThrowable.printStackTrace(new PrintWriter(sw));

    writeLogFile(sw.toString());
  }

  public static String getProperty(String sKey)
  {
    return System.getProperty(sKey);
  }

  public static int getIntProperty(String sKey, int nDefault)
  {
    String sValue = getProperty(sKey);
    if (sValue == null) return nDefault;
    return Integer.valueOf(sValue).intValue();
  }

  // Type information and conversion

  public static boolean isNumericType(Class<?> cType)
  {
    if (cType==null) return false;
    if (cType.isPrimitive() && cType != boolean.class) return true;
    return false;
  }

  public static boolean isStringType(Class<?> cType)
  {
    if (cType == String.class) return true;
    return false;
  }

  public static boolean IsJlInstanceType(Class<?> cType)
  {
    try
    {
      if (JlObject.class.isAssignableFrom(cType)) return true;
    }
    catch (NullPointerException e)
    {
    }

    return false;
  }

  public static Class<?> getPrimitiveType(Class<?> cType)
  {
    if (cType == null) return null;
    if (cType.equals(Byte.class)) return byte.class;
    if (cType.equals(Short.class)) return short.class;
    if (cType.equals(Character.class)) return char.class;
    if (cType.equals(Integer.class)) return int.class;
    if (cType.equals(Long.class)) return long.class;
    if (cType.equals(Float.class)) return float.class;
    if (cType.equals(Double.class)) return double.class;
    if (cType.equals(Boolean.class)) return boolean.class;
    return cType;
  }

  public static Class<?> getObjectType(Class<?> cType)
  {
    if (cType == null) return null;
    if (cType.equals(byte.class)) return Byte.class;
    if (cType.equals(short.class)) return Short.class;
    if (cType.equals(char.class)) return Character.class;
    if (cType.equals(int.class)) return Integer.class;
    if (cType.equals(long.class)) return Long.class;
    if (cType.equals(float.class)) return Float.class;
    if (cType.equals(double.class)) return Double.class;
    if (cType.equals(boolean.class)) return Boolean.class;
    return cType;
  }

  // String formatting

  public static String padString(String sOutput, int nMinLength)
  {
    String sPad = "";
    for (int i = sOutput.length(); i < Math.abs(nMinLength); i++)
      sPad += " ";
    if (nMinLength < 0) sOutput = sPad + sOutput;
    else sOutput += sPad;
    return sOutput;
  }

  /**
   * Implementation of String.replaceAll() introduced in Java 1.4 for backward compatibility
   * 
   * @param sStr
   *          Source string
   * @param sPattern
   *          Pattern to to be replaced
   * @param sReplace
   *          String to replace pattern with
   * @return Replaced string
   */
  public static String stringReplaceAll(String sStr, String sPattern,
      String sReplace)
  {
    if (sStr != null)
    {
      final int len = sPattern.length();
      StringBuffer sb = new StringBuffer();
      int found = -1;
      int start = 0;

      while ((found = sStr.indexOf(sPattern, start)) != -1)
      {
        sb.append(sStr.substring(start, found));
        sb.append(sReplace);
        start = found + len;
      }

      sb.append(sStr.substring(start));

      return sb.toString();
    }
    else return "";
  }

  /**
   * Splits a string at delimiter characters.
   * 
   * @param sStr
   *          The string to be split
   * @param sDelim
   *          The delimiter character(s)
   * @return An array of strings
   */
  public static String[] stringSplit(String sStr, String sDelim)
  {
    if (sDelim==null || sDelim.length()==0) return new String[] { sStr };
    
    Vector<String> iTokens = new Vector<String>();
    StringTokenizer stoken = new StringTokenizer(sStr, sDelim);

    while (stoken.hasMoreElements())
    {
      String token = stoken.nextToken();
      iTokens.addElement(token);
    }

    String[] sTokens = new String[iTokens.size()];
    for (int i = 0; i < sTokens.length; i++)
      sTokens[i] = (String)iTokens.elementAt(i);

    return sTokens;
  }
}

/* EOF */