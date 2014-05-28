package de.tudresden.ias.eclipse.dlabpro.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.actions.console.ReuseConsoleViewActionDelegate;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * Provides convenient launching of dLabPro processes
 * 
 * @author Matthias Wolff
 */
public class LaunchUtil implements IPreferenceConstants
{

  /**
   * The value of the {@link org.eclipse.debug.core.model.IProcess#ATTR_PROCESS_TYPE
   * IProcess.ATTR_PROCESS_TYPE} attribute indicating a dLabPro process.
   * <p>WARNING: Do not change! Console line tracker bound to this name.</p>
   */
  public static final String PT_DLABPRO  = "DLabProRunProcess";
  
  /**
   * The extended process type used for recycling dLabPro process consoles. 
   */
  public static final String ATTR_PROCESS_EXTTYPE = DLabProPlugin.PLUGIN_NAME
                                                      + ".ATTR_PROCESS_EXTTYPE";

  /**
   * Returns the absolute path to the dLabPro executable.
   * 
   * @param bDiscover
   *          If <code>false</code> the method just returns the path stored in the preferences, if <code>true</code>
   *          the method checks the path obtained from the preference store and, if necessary, tries to discover the
   *          actual location of dLabPro. In the latter case, the method will initialize empty preference values. 
   * @return the absolute path to the executable or <code>null</code> if the executable cannot be found
   */
  public static File getDlabproExe(boolean bDiscover)
  {
    return getExe(P_PRG_DLPEXE,"dlabpro",bDiscover);
  }

  /**
   * Returns the dLabPro command line arguments specified in the preferences.
   * 
   * @return a string vector containing the command line arguments
   */
  public static ArrayList<String> getDlabproExeArgs()
  {
    return getExeArgs(P_PRG_DLPARGS);
  }
  
  /**
   * Returns the absolute path to the cgen executable.
   * 
   * @param bDiscover
   *          If <code>false</code> the method just returns the path stored in the preferences, if <code>true</code>
   *          the method checks the path obtained from the preference store and, if necessary, tries to discover the
   *          actual location of cgen. In the latter case, the method will initialize empty preference values. 
   * @return the absolute path to the executable or <code>null</code> if the executable cannot be found
   */
  public static File getCgenExe(boolean bDiscover)
  {
    File exe = getExe(P_PRG_CGENEXE,"dcg",bDiscover);
    if (exe==null)
      exe = getExe(P_PRG_CGENEXE,"cgen",bDiscover);
    return exe;
  }

  /**
   * Returns the CGen command line arguments specified in the preferences.
   * 
   * @return a string vector containing the command line arguments
   */
  public static ArrayList<String> getCgenExeArgs()
  {
    return getExeArgs(P_PRG_CGENARGS);
  }

  /**
   * Returns the absolute path to the UASR home directory.
   * 
   * @param bDiscover
   *          If <code>false</code> the method just returns the path stored in the preferences, if <code>true</code>
   *          the method checks the path obtained from the preference store and, if necessary, tries to discover the
   *          actual location of UASR. In the latter case, the method will initialize empty preference values. 
   * @return the absolute path to the home directory or <code>null</code> if the directory cannot be found
   */
  public static File getUasrHome(boolean bDiscover)
  {
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    String sHome = iStore.getString(P_PRG_UASRHOME);
    if (!bDiscover)
      return sHome.length()>0 ? new File(sHome) : null;

    // Check UASR home path, if not exists discover
    File f = new File(sHome);
    if (!f.exists())
    {
      sHome = System.getenv("UASR_HOME");
      if (sHome!=null)
      {
        f = new File(sHome);
        if (f.exists())
          sHome = f.getAbsolutePath();
        else
          sHome = null;
      }
    }

    // Initialize empty preference values
    if (sHome!=null)
    {
      if (iStore.getString(P_PRG_UASRHOME).equals(""))
        iStore.setValue(P_PRG_UASRHOME,sHome);
      if (iStore.getString(P_PRG_UASRDOC).equals(""))
        iStore.setValue(P_PRG_UASRDOC,sHome+File.separator+"manual");
    }
    
    if (sHome==null) return null;
    return new File(sHome);
  }
  
  /**
   * Obtains the absolute path of the dLabPro or cgen executable.
   * 
   * @param sPrefName
   *          <code>P_DLABPRO_EXECUTIVE</code> or
   *          <code>P_CGEN_EXECUTIVE</code>
   * @param sExeName
   *          "dlabpro" or "cgen"
   * @return the absolute path to the executable or <code>null</code> if the executable cannot be found
   */
  private static File getExe(String sPrefName, String sExeName, boolean bDiscover)
  {
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    String sExe = iStore.getString(sPrefName);
    if (!bDiscover)
      return sExe.length()>0 ? new File(sExe) : null;
    
    // 1. Check the executable obtained from preferences
    File f = new File(sExe);
    if (!f.exists()) sExe = null;
    
    // 2. Discover executable in dLabPro home path
    if (sExe==null)
    {
      String sHome = iStore.getString(P_PRG_DLPHOME);
      String sMach = System.getenv("MACHINE");
      if (sHome.length()>0 && sMach!=null)
      {
        String sFile = sHome + File.separator + "bin.release." + sMach
            + File.separator + sExeName;
        f = new File(sFile);
        if (f.exists()) sExe = f.getAbsolutePath();
        else
        {
          sFile += ".exe";
          f = new File(sFile);
          if (f.exists()) sExe = f.getAbsolutePath();
        }
      }
    }
    
    // 3. Discover executable in path
    if (sExe==null)
    {
      String aPath[] = System.getenv("PATH").split(File.pathSeparator);
      for (int i=0; i<aPath.length; i++)
      {
        f = new File(aPath[i]+File.separator+sExeName);
        if (f.exists())
        {
          sExe =f.getAbsolutePath();
          break;
        }
        f = new File(aPath[i]+File.separator+sExeName+".exe");
        if (f.exists())
        {
          sExe =f.getAbsolutePath();
          break;
        }
      }
    }
    
    // 4. Aftermath
    if (sExe!=null)
    {
      if (iStore.getString(sPrefName).equals(""))
        iStore.setValue(sPrefName,sExe);
      
      try
      {
        String sHome = sExe.substring(0,sExe.lastIndexOf(File.separator));
        sHome = sHome.substring(0,sHome.lastIndexOf(File.separator));
  
        if (iStore.getString(P_PRG_DLPHOME).equals(""))
          iStore.setValue(P_PRG_DLPHOME,sHome);
        if (iStore.getString(P_PRG_DLPDOC).equals(""))
          iStore.setValue(P_PRG_DLPDOC,sHome + File.separator + "manual");
      }
      catch (Exception e)
      {
        // Silently catch all exceptions
      }
    }
    
    if (sExe==null) return null;
    return new File(sExe);
  }

  /**
   * Obtains the command line arguments specified by the preferences of the
   * dLabPro or cgen executable.
   * 
   * @param sPrefName
   *          <code>P_DLABPRO_ARGUMENTS</code> or
   *          <code>P_CGEN_ARGUMENTS</code>
   * @return a string vector containing the command line arguments
   */
  private static ArrayList<String> getExeArgs(String sPrefName)
  {
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    ArrayList<String> lsArgs = new ArrayList<String>();
    String sArgs = iStore.getString(sPrefName);
    if (sArgs==null || sArgs.length()==0) return lsArgs;
    String[] asArgs = sArgs.replace('\t',' ').split(" "); 
    for (int i=0; i<asArgs.length; i++) lsArgs.add(asArgs[i]);
    return lsArgs;
  }
  
  /**
   * Determines if a file is a dLabPro executable.
   * 
   * @param iExe the file
   * @return <code>true</code> if and only if <code>iExe</code> is a dLabPro
   * executable
   */
  private static boolean isDLabProExe(File iExe)
  {
    if (iExe==null) return false;
    return iExe.equals(getDlabproExe(false)); 
  }
  
  /**
   * Determines if a file is a CGen executable.
   * 
   * @param iExe the file
   * @return <code>true</code> if and only if <code>iExe</code> is a CGen
   * executable
   */
  private static boolean isCgenExe(File iExe)
  {
    if (iExe==null) return false;
    return iExe.equals(getCgenExe(false)); 
  }
  
  /**
   * Guesses the executable for a script file.
   */
  private static File getExeForScript(File iScr)
  {
    System.out.println("- auto-discovering executable");
    String sScr = iScr.getName().toLowerCase(); 
    if (sScr.endsWith(".itp") || sScr.endsWith(".xtp"))
      return getDlabproExe(false);
    else if (sScr.endsWith(".def"))
      return getCgenExe(false);
    else
      return null;
  }
  
  /**
   * Obtains the arguments for the specified executable from the preference
   * store.
   * 
   * @param iExe the executable
   * @return the list of arguments
   */
  private static ArrayList<String> getPrefArgsForExe(File iExe)
  {
    ArrayList<String> lsPrefArgs = new ArrayList<String>();
    if (isDLabProExe(iExe))
    {
      System.out.println("- adding dLabPro arguments from preferences");
      lsPrefArgs.addAll(getDlabproExeArgs());
    }
    else if (isCgenExe(iExe))
    {
      System.out.println("  - adding cgen arguments from preferences");
      lsPrefArgs.addAll(getCgenExeArgs());
    }
    return lsPrefArgs;
  }

  /**
   * Adjusts command line arguments for certain needs of the dLabPro executable.
   * 
   * @param lsCmdl
   *          the parsed command line
   * @param iExe
   *          the executable file
   * @param nStartAt
   *          the zero-based index of the first command line argument to tweak
   * @return the tweaked command line
   */
  private static ArrayList<String> tweakCmdlForExe(ArrayList<String> lsCmdl,
      File iExe, int nStartAt)
  {
    if (isDLabProExe(iExe))
    {
      System.out.println("- Removing double quotation marks form arguments");
      for (int i=(nStartAt>=0?nStartAt:0); i<lsCmdl.size(); i++)
        lsCmdl.set(i,lsCmdl.get(i).replace("\"",""));
    }
    return lsCmdl;
  }
  
  /**
   * Makes (and sets) dLabPro process attributes.
   * 
   * @param iProcess
   *          the process whose attributes shall be set (may be <code>null</code>)
   * @param sKey
   *          the attribute's key, one of the <code>ATTR_PROCESS_XXX</code> or
   *          <code>IProcess.ATTR_XXX</code> constants
   * @param iExe
   *          the executable file
   * @param lsECmdl
   *          the arguments of the executable
   * @param iScr
   *          the script file
   * @param lsSCmdl
   *          the arguments of the script
   * @return the attribute string
   */
  private static String makeProcAttr(IProcess iProcess, String sKey, File iExe,
      ArrayList<String> lsECmdl, File iScr, ArrayList<String> lsSCmdl)
  {
    String sValue = "";
    if (sKey.equals(IProcess.ATTR_PROCESS_TYPE))
    {
      if (isDLabProExe(iExe)) sValue = PT_DLABPRO;
      else if (isCgenExe(iExe)) sValue = PT_DLABPRO;
      else sValue = iExe.getName();
    }
    else if (sKey.equals(IProcess.ATTR_PROCESS_LABEL))
    {
      sValue = iExe.getName();
      if (lsECmdl.size()>0) sValue += " [...]";
      if (iScr!=null      ) sValue += " "+iScr.getName();
      sValue += " "+LaunchUtil.joinCommandLine(lsSCmdl);
    }
    else if (sKey.equals(IProcess.ATTR_CMDLINE))
    {
      try
      {
        sValue = iExe.getCanonicalPath()+" ";
        sValue += LaunchUtil.joinCommandLine(lsECmdl)+" ";
        if (iScr!=null) sValue += iScr.getCanonicalPath()+" ";
        sValue += LaunchUtil.joinCommandLine(lsSCmdl); 
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return null;
      }
    }
    else if (sKey.equals(ATTR_PROCESS_EXTTYPE))
    {
      sValue = iExe.getName().replace(".exe","");
      if (iScr!=null) sValue+="."+iScr.getName();
    }

    if (iProcess!=null) iProcess.setAttribute(sKey,sValue);
    return sValue;
  }

  /**
   * Checks whether a dLabPro process of the extended type <code>sExtProcType</code>
   * can be launched. If other processes of the same type are running and dLabPro
   * console recycling is enabled (see
   * {@link de.tudresden.ias.eclipse.dlabpro.run.actions.common#ReuseConsoleViewActionDelegate
   * ReuseConsoleViewActionDelegate}) the method will display a dialog which will
   * prompt the user for terminating concurrent processes.
   * 
   * @param sExtProcType the extended process type
   * @return <code>true</code> if a new dLabPro process can be launched
   */
  private static boolean clearToLaunch(String sExtProcType)
  {
    ILaunchManager iLm = DebugPlugin.getDefault().getLaunchManager();
    ILaunch[] aiLn = iLm.getLaunches();
    if (ReuseConsoleViewActionDelegate.isEnabled() && aiLn.length > 0)
      for (int i=0; i<aiLn.length; i++)
       {
        if (aiLn[i].getProcesses().length==0) continue;
        IProcess iIpr = aiLn[i].getProcesses()[0];
        if (iIpr.isTerminated()) continue;
        if (!iIpr
            .getAttribute(ATTR_PROCESS_EXTTYPE).equals(sExtProcType))
          continue;
        String sTitle = sExtProcType+" - Confirm Termination";
        String sMsg = "This will terminate the running "+sExtProcType+" session.";
        sMsg += "\n\nUncheck the \"Recycle Console\" button in the console toolbar if you";
        sMsg += " want to run multiple "+sExtProcType+" sessions at the same time.";
        if (UIUtil.openConfirmDialog(sTitle,sMsg))
        {
          try
          {
            aiLn[i].terminate();
          }
          catch (DebugException e)
          {
            return false;
          }
        }
        else return false;
      }
    return true;
  }
  
  /**
   * Convenience method to run a dLabPro, CGen, or another console process in
   * the console view.
   * 
   * @param iExe
   *          the executable file, may be <code>null</code> in which case
   *          <code>iScr</code> must <em>not</em> be <code>null</code> and the
   *          method will try to determine automatically which executable to
   *          launch
   * @param iScr
   *          the <code>.itp</code>, <code>.xtp</code>, or <code>.def</code>
   *          script file, may be <code>null</code> in which <code>iExe</code>
   *          must <em>not</em> be <code>null</code> 
   * @param sCmdLine
   *          the command line string, may be <code>null</code> or empty
   * @param iHome
   *          the start directory for the launch, may be <code>null</code> in
   *          which case the method will use the directory of <code>iScr</code>
   * @throws IllegalArgumentException if <ul>
   *         <li><code>iExe</code> and <code>iScr</code> are both
   *           <code>null</code></li>
   *         </ul>
   */
  public static IProcess launchDlabpro(File iExe, File iScr, String sCmdLine,
      File iHome)
  {
    return launchDlabpro(iExe,iScr,sCmdLine,iHome,null);
  }
  
  /**
   * Convenience method to run a dLabPro, CGen, or another console process in
   * the console view.
   * <p>Equivalent to {@link #launchDlabpro(File,File,String,File,ILaunch) 
   * launchDlabpro(iExe,iScr,sCmdLine,iHome,null)} (see there).</p>
   * 
   * @param iExe
   *          the executable file, may be <code>null</code> in which case
   *          <code>iScr</code> must <em>not</em> be <code>null</code> and the
   *          method will try to determine automatically which executable to
   *          launch
   * @param iScr
   *          the <code>.itp</code>, <code>.xtp</code>, or <code>.def</code>
   *          script file, may be <code>null</code> in which <code>iExe</code>
   *          must <em>not</em> be <code>null</code> 
   * @param sCmdLine
   *          the command line string, may be <code>null</code> or empty
   * @param iHome
   *          the start directory for the launch, may be <code>null</code> in
   *          which case the method will use the directory of <code>iScr</code>
   * @param iLaunch
   *          the launch to contribute the new process to (may be
   *          <code>null</code>) 
   * @throws IllegalArgumentException if <ul>
   *         <li><code>iExe</code> and <code>iScr</code> are both
   *           <code>null</code></li>
   *         </ul>
   */
  public static IProcess launchDlabpro(File iExe, File iScr, String sCmdLine,
      File iHome, ILaunch iLaunch)
  {
    ArrayList<String> lsCmdLine = LaunchUtil.parseCommandLine(sCmdLine);
    return launchDlabpro(iExe,iScr,lsCmdLine,iHome,iLaunch);
  }
  
  /**
   * Convenience method to run a dLabPro, CGen, or another console process in
   * the console view.
   * <p>Equivalent to {@link #launchDlabpro(File,File,ArrayList,File,ILaunch) 
   * launchDlabpro(iExe,iScr,lsCmdLine,iHome,null)} (see there).</p>
   * 
   * @param iExe
   *          the executable file, may be <code>null</code> in which case
   *          <code>iScr</code> must <em>not</em> be <code>null</code> and the
   *          method will try to determine automatically which executable to
   *          launch
   * @param iScr
   *          the <code>.itp</code>, <code>.xtp</code>, or <code>.def</code>
   *          script file, may be <code>null</code> in which <code>iExe</code>
   *          must <em>not</em> be <code>null</code> 
   * @param lsCmdLine
   *          the command line arguments, may be <code>null</code> or empty
   * @param iHome
   *          the start directory for the launch, may be <code>null</code> in
   *          which case the method will use the directory of <code>iScr</code>
   * @throws IllegalArgumentException if <ul>
   *         <li><code>iExe</code> and <code>iScr</code> are both
   *           <code>null</code></li>
   *         </ul>
   */
  public static IProcess launchDlabpro(File iExe, File iScr,
      ArrayList<String> lsCmdLine, File iHome)
  {
    return launchDlabpro(iExe,iScr,lsCmdLine,iHome,null);
  }
  
  /**
   * Convenience method to run a dLabPro, CGen, or another console process in
   * the console view.
   * 
   * @param iExe
   *          the executable file, may be <code>null</code> in which case
   *          <code>iScr</code> must <em>not</em> be <code>null</code> and the
   *          method will try to determine automatically which executable to
   *          launch
   * @param iScr
   *          the <code>.itp</code>, <code>.xtp</code>, or <code>.def</code>
   *          script file, may be <code>null</code> in which <code>iExe</code>
   *          must <em>not</em> be <code>null</code> 
   * @param lsCmdLine
   *          the command line arguments, may be <code>null</code> or empty
   * @param iHome
   *          the start directory for the launch, may be <code>null</code> in
   *          which case the method will use the directory of <code>iScr</code>
   * @param iLaunch
   *          the launch to contribute the new process to (may be
   *          <code>null</code>) 
   * @throws IllegalArgumentException if <ul>
   *         <li><code>iExe</code> and <code>iScr</code> are both
   *           <code>null</code></li>
   *         </ul>
   */
  public static IProcess launchDlabpro(File iExe, File iScr,
      ArrayList<String> lsCmdLine, File iHome, ILaunch iLaunch)
  {
    System.out.println("Launch command line executable");

    // Initialize
    if (iExe == null && iScr == null) throw new IllegalArgumentException();
    if (iScr != null && !iScr.exists())
    {
      UIUtil.showErrorDialog("Cannot launch script " + iScr.getAbsolutePath(),
          "The file does not exist.",null);
      return null;
    }
    if (iExe == null) iExe = getExeForScript(iScr); /* iScr is not null! */
    if (iExe == null)
    {
      UIUtil.showErrorDialog("Cannot determine execuatble for script "
          + iScr.getName(),
          "Script files must have one of the exensions itp, xtp, or def",null);
    }
    if (iHome == null && iScr != null)
    {
      System.out.println("- auto-discovering home path");
      iHome = iScr.getParentFile();
    }
    if (iHome != null && !iHome.exists())
    {
      System.out.println("  WARNING: working directory "
          + iHome.getAbsolutePath() + " does not exist!");
      iHome = null;
    }

    // Make command line and process attributes
    lsCmdLine = tweakCmdlForExe(lsCmdLine,iExe,0);
    ArrayList<String> lsECmdl = getPrefArgsForExe(iExe);
    ArrayList<String> lsSCmdl = lsCmdLine!=null ? lsCmdLine : new ArrayList<String>();
    ArrayList<String> lsCmdl = new ArrayList<String>();
    try
    {
      lsCmdl.add(iExe.getCanonicalPath());
      lsCmdl.addAll(lsECmdl);
      if (iScr != null) lsCmdl.add(iScr.getCanonicalPath());
      lsCmdl.addAll(lsSCmdl);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return null;
    }

    // Prelaunch sequence
    if (!clearToLaunch(makeProcAttr(null,ATTR_PROCESS_EXTTYPE,iExe,lsECmdl,iScr,lsSCmdl)))
    {
      System.out.println("- Not clear to launch! STOP.");
      return null;
    }

    // Create process
    System.out.println("- Working dir.: "
        + (iHome != null ? iHome.getAbsolutePath() : "(not specified)"));
    System.out.println("- Executable  : " + lsCmdl.get(0));
    System.out.println("- Command line:");
    for (int i = 1; i < lsCmdl.size(); i++)
      System.out.println("  |" + lsCmdl.get(i) + "|");

    Process iPr = null;
    try
    {
      iPr = Runtime.getRuntime().exec(lsCmdl.toArray(new String[]{}),null,iHome);
    }
    catch (IOException e)
    {
      UIUtil.showErrorDialog("Couldn't run interpreter",
          "Please check the interpreter path settings!",e);
    }
    catch (IllegalArgumentException e)
    {
      UIUtil.showErrorDialog("Couldn't run interpreter",
          "Please check the interpreter path settings!",e);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    // Launch
    ILaunchManager iLm = DebugPlugin.getDefault().getLaunchManager();
    ILaunch iLn = iLaunch;
    if (iLn==null) iLn = new Launch(null,ILaunchManager.RUN_MODE,null);
    IProcess iIpr = DebugPlugin.newProcess(iLn,iPr,"");
    makeProcAttr(iIpr,IProcess.ATTR_PROCESS_TYPE ,iExe,lsECmdl,iScr,lsSCmdl);
    makeProcAttr(iIpr,IProcess.ATTR_PROCESS_LABEL,iExe,lsECmdl,iScr,lsSCmdl);
    makeProcAttr(iIpr,IProcess.ATTR_CMDLINE      ,iExe,lsECmdl,iScr,lsSCmdl);
    makeProcAttr(iIpr,ATTR_PROCESS_EXTTYPE       ,iExe,lsECmdl,iScr,lsSCmdl);
    if (iLaunch==null)
    {
      iLn.addProcess(iIpr);
      iLm.addLaunch(iLn);
    }
    System.out.println("- Process attributes:");
    System.out.println("  - ATTR_PROCESS_TYPE    = \""+iIpr.getAttribute(IProcess.ATTR_PROCESS_TYPE )+"\"");
    System.out.println("  - ATTR_PROCESS_LABEL   = \""+iIpr.getAttribute(IProcess.ATTR_PROCESS_LABEL)+"\"");
    System.out.println("  - ATTR_CMDLINE         = \""+iIpr.getAttribute(IProcess.ATTR_CMDLINE      )+"\"");
    System.out.println("  - ATTR_PROCESS_EXTTYPE = \""+iIpr.getAttribute(ATTR_PROCESS_EXTTYPE       )+"\"");

    // Add a console (if none exists yet)
    IConsoleManager iCm = ConsolePlugin.getDefault().getConsoleManager();
    IConsole[] aiCn = iCm.getConsoles();
    ProcessConsole iDlpCns = null;
    int i = 0;
    for (i = 0; i < aiCn.length; i++)
      if (aiCn[i] instanceof ProcessConsole)
      {
        iDlpCns = (ProcessConsole)aiCn[i];
        if (iDlpCns.getProcess().equals(iIpr)) break;
      }
    if (i == aiCn.length)
    {
      iDlpCns = new ProcessConsole(iIpr,new ConsoleColorProvider());
      ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]
      { iDlpCns });
    }
    
    // Bring console to top
    IPreferenceStore iStore = DLabProPlugin.getDefault().getPreferenceStore();
    if (iStore.getBoolean(IPreferenceConstants.P_CNS_SHOW))
    {
      ConsolePlugin.getDefault().getConsoleManager().showConsoleView(iDlpCns);
    }

    return iIpr;
  }

  /**
   * Parses a command line string into its arguments.
   * @param sCmdl the command line string
   * @return a list of argument strings
   */
  public static ArrayList<String> parseCommandLine(String sCmdl)
  {
    boolean bInStr = false;
    ArrayList<String> iTok = new ArrayList<String>();
  
    if (sCmdl==null || sCmdl.length()==0) return iTok;
    for (int i=0, j=0; j<sCmdl.length(); j++)
    {
      char c = sCmdl.charAt(j);
      if (Character.isWhitespace(c) && !bInStr)
      {
        iTok.add(sCmdl.substring(i,j));
        while (j<sCmdl.length() && Character.isWhitespace(sCmdl.charAt(j))) j++;
        if (j==sCmdl.length()) break;
        i=j; j--;
      }
      else if (c=='\"') bInStr = !bInStr;
      if (j==sCmdl.length()-1) iTok.add(sCmdl.substring(i,j+1));
    }
    return iTok;
  }

  /**
   * Joins a command line to a string.
   * @param lsCmdl the list of argument strings 
   * @return the command line string
   */
  public static String joinCommandLine(ArrayList<String> lsCmdl)
  {
    String sCmdl = "";
    if (lsCmdl!=null)
      for (int i=0; i<lsCmdl.size(); i++)
        if (lsCmdl.get(i)!=null && lsCmdl.get(i).length()>0)
        {
          if (i>0) sCmdl += " ";
          sCmdl += lsCmdl.get(i);
        }
    return sCmdl;
  }
}
