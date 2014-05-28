package de.tudresden.ias.eclipse.dlabpro.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

/**
 * Instances of this class associate with one .itp/.xtp file resource and manage
 * their persistent properties. 
 * 
 * @author Matthias Wolff and Christian Feig
 */
public class XtpFileProperties
{
  private static final String P_AUTOWORKDIR    = "AUTOWORKDIR";
  private static final String P_WORKDIR        = "WORKDIR";
  private static final String P_NARGS          = "NARGS";
  private static final String P_ARG            = "ARG";
  private static final String P_DONTASK        = "DONTASK";
  private static final String ARG_DEFAULT_HINT = "(argument not declared)";

  private boolean             bPersistent      = false;
  private IFile               iFile            = null;
  private boolean             bAutoWorkDir     = false;
  private boolean             bDontAsk         = false;
  private String              sWorkDir         = null;
  private ArrayList<String>   lsArgs           = null;
  private Map<String, String> mssArgsDescr     = null;
  private int                 nExtraArg        = -1;

  /**
   * Creates an XTP file property object. The constructor initialized the object
   * with the persistently stored properties of <code>iFile</code>. If a document
   * is committed, the constructor will scan for references to arguments
   * (<code>$n&lt;n&gt;</code>) and add empty values for all discovered
   * arguments. 
   * 
   * @param iFile
   *          the .xtp or .itp file resource
   * @param bPersistent
   *          if <code>true</code> the properties will be persistently stored
   *          for the file resource
   * @param bNoArgs
   *          if <code>true</code>, do not scan for argument declarations or
   *          references
   * @throws IllegalArgumentException if <code>iFile</code> is <code>null</code>
   */
  public XtpFileProperties(IFile iFile, boolean bPersistent, boolean bNoArgs)
  {
    this.bPersistent = bPersistent;
    this.iFile       = iFile;
    this.lsArgs      = new ArrayList<String>();
    load();
    if (!bNoArgs)
      scanArgs();
  }

  /**
   * Creates an XTP file property object. The constructor initialized the object
   * with the persistently stored properties of <code>iFile</code>. If a document
   * is committed, the constructor will scan for references to arguments
   * (<code>$n&lt;n&gt;</code>) and add empty values for all discovered
   * arguments. 
   * 
   * @param iFile
   *          the .xtp or .itp file resource
   * @param bPersistent
   *          if <code>true</code> the properties will be persistently stored
   *          for the file resource
   * @throws IllegalArgumentException if <code>iFile</code> is <code>null</code>
   */
  public XtpFileProperties(IFile iFile, boolean bPersistent)
  {
    this(iFile,bPersistent,false);
  }
  
  // -- Getters and setters --
  
  public boolean isPersistent()
  {
    return bPersistent;
  }
  
  /**
   * Returns the script file this object is associated with
   * 
   * @return the script file
   */
  public File getScriptFile()
  {
    if (iFile==null) return null;
    
    return iFile.getLocation().toFile();
  }
  
  /**
   * Returns the working directory persistently stored for the associated
   * .itp/.xtp file resource.
   * 
   * @return the path name or <code>null</code> if no working directory is
   * stored for the resource
   */
  public String getWorkDir()
  {
    return sWorkDir;
  }

  /**
   * Convenience method. Returns the working directory persistently stored for
   * the associated .itp/.xtp file resource as {@link java.lang.File}.
   * 
   * @return the file or <code>null</code> if no working directory is stored for
   * the resource
   */
  public File getWorkDirFile()
  {
    if (sWorkDir==null) return null;
    return new File(sWorkDir);
  }
  
  /**
   * Returns the script arguments persistently stored for the associated
   * .itp/.xtp file resource.
   * 
   * @return a vector of argument strings or an empty vector if no arguments are
   * stored for the resource
   */
  public ArrayList<String> getArgs()
  {
    return lsArgs;
  }

  /**
   * Returns the description of a script argument as defined in the script's
   * header. Descriptions for arguments can only be known if
   * <ul>
   *   <li>the constructor was called with a valid document, or</li>
   *   <li>{@link #scanDocument(IDocument)} was explicitly called.</li>
   * </ul>
   * 
   * @param nArg
   *          the argument index (starting with 1!)
   * @return the description or <code>null</code> if no description is known.
   */
  public String getArgDescription(int nArg)
  {
    if (mssArgsDescr==null) return null;
    return mssArgsDescr.get("$"+nArg);
  }
  
  /**
   * Determines if a script argument is "extra". Extra arguments are not
   * declared or referenced by the script. If an argument is extra can only be
   * known if
   * <ul>
   *   <li>the constructor was called with a valid document, or</li>
   *   <li>{@link #scanDocument(IDocument)} was explicitly called.</li>
   * </ul>
   * <p>If no document was seen, the method returns <code>false</code>.</p>
   * 
   * @param nArg
   *          the argument index (starting with 1!)
   * @return <code>true</code> if the argument is extra
   */
  public boolean isArgExtra(int nArg)
  {
    if (nExtraArg<0) return false;
    return nArg >= nExtraArg;
  }
  
  /**
   * Sets the working directory to be persistently stored for the associated
   * .itp/.xtp file resource.
   * <p><b>Please note:</b> the value will not be stored persistently until
   * {@link #store()} is invoked!</p>
   *  
   * @param sWorkDir
   *          the new working directory
   * @see XtpFileProperties#store store
   */
  public void setWorkDir(String sWorkDir)
  {
    this.sWorkDir = sWorkDir;
  }

  /**
   * Sets the script arguments to be persistently stored for the associated
   * .itp/.xtp file resource.
   * <p><b>Please note:</b> the value will not be stored persistently until
   * {@link #store()} is invoked!</p>
   *  
   * @param lsArgs
   *          the new script arguments
   * @see XtpFileProperties#store store
   */
  public void setArgs(ArrayList<String> lsArgs)
  {
    this.lsArgs = lsArgs!=null ? lsArgs : new ArrayList<String>();
  }
  
  /**
   * Determines if this object is lacking data and needs user input.
   * 
   * @return <code>true</code> if this object needs user input
   */
  public boolean needsUserInput()
  {
    return (lsArgs.size()>0 && !bDontAsk);
  }
  
  /**
   * Toggles the automatic guessing of the working directory of associated
   * .itp/.xtp file resource. If <code>true</code> the method will guess the
   * working directory and set {@link #sWorkDir} accordingly.
   * 
   * @param bAuto automatically guess the working directory
   */
  public void setAutoWorkDir(boolean bAuto)
  {
    bAutoWorkDir = bAuto;
    if (bAuto)
    {
      if (iFile==null) return;
      try
      {
        sWorkDir = iFile.getLocation().toFile().getCanonicalFile().getParent();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
  }
  
  /**
   * Returns if the working directory for running the associated .itp/.xtp file
   * resource is automatically determined.
   * 
   * @return <code>true</code> of the working directory is automatically
   *         determined, <code>false</code> otherwise.
   */
  public boolean isAutoWorkDir()
  {
    return bAutoWorkDir;
  }

  /**
   * Toggles the "Do not ask for properties on launch" behavior.
   * 
   * @param bDontAsk
   *          The new value.
   */
  public void setDontAsk(boolean bDontAsk)
  {
    this.bDontAsk = bDontAsk;
  }
  
  /**
   * Returns the "Do not ask for properties on launch" flag.
   */
  public boolean isDontAsk()
  {
    return this.bDontAsk;
  }
  
  // -- Persistent properties access --
  
  /**
   * Loads the persistent .itp/.xtp file properties for the associated resource. 
   */
  public void load()
  {
    if (!bPersistent) return;
    
    sWorkDir = getPersistentProperty(P_WORKDIR);
    String sAutoWorkDir = getPersistentProperty(P_AUTOWORKDIR);
    setAutoWorkDir(sAutoWorkDir==null || Boolean.valueOf(sAutoWorkDir));
    String sDontAsk = getPersistentProperty(P_DONTASK);
    setDontAsk(sDontAsk!=null && Boolean.valueOf(sDontAsk));
    lsArgs = new ArrayList<String>();
    int nArgs = 0;
    try
    {
      nArgs = Integer.parseInt(getPersistentProperty(P_NARGS));
    }
    catch (NumberFormatException e)
    {
      // Silently ignore it
    }
    for (int i=1; i<=nArgs; i++)
      lsArgs.add(getPersistentProperty(P_ARG+i));
  }
  
  /**
   * Stores the persistent .itp/.xtp file properties for the associated
   * resource.
   */
  public void store()
  {
    if (!bPersistent) return;

    clear();
    setPersistentProperty(P_WORKDIR    ,sWorkDir                     );
    setPersistentProperty(P_AUTOWORKDIR,String.valueOf(bAutoWorkDir) );
    setPersistentProperty(P_DONTASK    ,String.valueOf(bDontAsk)     );
    setPersistentProperty(P_NARGS      ,String.valueOf(lsArgs.size()));
    for (int i=1; i<=lsArgs.size(); i++)
      setPersistentProperty(P_ARG+i,lsArgs.get(i-1));
  }

  /**
   * Removes all persistent properties previously stored by this class from the
   * associated .xtp/.itp file resource. 
   */
  public void clear()
  {
    if (!bPersistent || iFile==null || !iFile.exists()) return;

    try
    {
      Set<?> iKeys = iFile.getPersistentProperties().keySet();
      Iterator<?> i = iKeys.iterator();
      while (i.hasNext())
      {
        QualifiedName iKey = (QualifiedName)i.next();
        if (iKey.getQualifier().equals(DLabProPlugin.PLUGIN_NAME))
          iFile.setPersistentProperty(iKey,null);
      }
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
    
  }

  /**
   * Convenience method. Gets one persistent property of the associated resource.
   * 
   * @param sKey
   *          the key
   * @return the value or <code>null</code> if no such property exists
   */
  private String getPersistentProperty(String sKey)
  {
    if (iFile==null || !iFile.exists()) return null;
    
    try
    {
      QualifiedName iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey);
      return iFile.getPersistentProperty(iQn);
    }
    catch (CoreException e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
  /**
   * Convenience method. Sets one persistent property of the associated resource.
   * 
   * @param sKey
   *          the key
   * @param sValue
   *          the value
   */
  private void setPersistentProperty(String sKey, String sValue)
  {
    if (iFile==null || !iFile.exists()) return;
    
    QualifiedName iQn = new QualifiedName(DLabProPlugin.PLUGIN_NAME,sKey);
    try
    {
      iFile.setPersistentProperty(iQn,sValue);
    }
    catch (CoreException e)
    {
      e.printStackTrace();
    }
  }

  // -- Document scanning --
  
  /**
   * Scans the itp/xtp file for declaration of and references to arguments 
   * (<code>$n&lt;n&gt;</code>). The method will add all discovered arguments
   * to the list of script arguments maintained by this object.
   */
  public void scanArgs()
  {
    nExtraArg = -1;
    if (iFile==null) return;
    try
    {
      Document doc = WorkbenchUtil.getXtpDocumentFromIFile(iFile);
      mssArgsDescr = new HashMap<String,String>();
      scanDocumentHead(doc);
      scanDocumentBody(doc);
      for (int i=lsArgs.size(); i<mssArgsDescr.size(); i++) lsArgs.add("");
      nExtraArg = mssArgsDescr.size()+1;
    }
    catch (Exception e)
    {
    }
  }

  /**
   * Computes the document partitions.
   * 
   * @param iDoc
   *           the document
   * @return an array regions
   */
  private ITypedRegion[] computeDocumentPartitioning(IDocument iDoc)
  {
    ITypedRegion[] regions = null;
    try
    {
      regions = iDoc.computePartitioning(0, iDoc.getLength());
    }
    catch (BadLocationException e1)
    {
      // e1.printStackTrace();
    }
    return regions;
  }

  /**
   * Scans the document head for argument declarations
   * 
   * @param doc
   * @return
   */
  private void scanDocumentHead(IDocument doc)
  {
    mssArgsDescr.clear();
    
    ITypedRegion[] regions = computeDocumentPartitioning(doc);
    List<String> list = new ArrayList<String>();
    for (int i = 0; i < regions.length; i++)
    {
      if (regions[i].getType().equals(IPreferenceConstants.P_CLR_COMMENT)) try
      {
        String value = doc.get(regions[i].getOffset(), regions[i].getLength());
        list.add(value);
      }
      catch (BadLocationException e)
      {
        e.printStackTrace();
      }
    }

    int i = 0;
    boolean argumentsFound = false;
    while (!argumentsFound)
    {
      if (i < list.size())
      {
        String[] strings = ((String)list.get(i)).split(" ");
        for (int j = 0; j < strings.length; j++)
        {
          if (strings[j].toLowerCase().startsWith("arguments")) argumentsFound = true;
        }
        i++;
      }
      else break;
    }
    if (argumentsFound)
    {
      boolean breakWhile = false;
      String workString = null;
      String[] workStringSplitted = null;
      String key = null;
      while (!breakWhile && i < list.size())
      {
        workString = (String)list.get(i);
        workStringSplitted = workString.split(":");
        if (!workStringSplitted[0].equals(workString))
        {
          for (int j = 0; j < workStringSplitted.length; j++)
          {
            String[] tempArray = workStringSplitted[j].split("\\$");
            if (!tempArray[0].equals(workStringSplitted[j]))
            {
              // $1..$n found
              key = "$" + tempArray[1];
              mssArgsDescr.put(key,ARG_DEFAULT_HINT);
            }
            else
            {
              // Description of previous argument found
              if (key != null)
              {
                mssArgsDescr.put(key,tempArray[0]);
                key = null;
              }
            }
          }
        }
        else breakWhile = true;
        i++;
      }
    }
 }

  /**
   * this method parses the document for preprocessor usages
   * 
   * @param doc
   * @param prop
   */
  private void scanDocumentBody(IDocument doc)
  {
    ITypedRegion[] regions = computeDocumentPartitioning(doc);
    for (int i = 0; i < regions.length; i++)
      if (regions[i].getType().equals(IPreferenceConstants.PREPROCESSOR_WITH_LEADING$)) try
      {
        String value = doc.get(regions[i].getOffset(), regions[i].getLength());
        if (mssArgsDescr.get(value) == null)
        {
          mssArgsDescr.put(value,ARG_DEFAULT_HINT);
        }
      }
      catch (BadLocationException e2)
      {
        // e2.printStackTrace();
      }
      else
      {
        // TODO Kommentare werden erstmal ausgelassen
        if (!regions[i].getType().equals(IPreferenceConstants.P_CLR_COMMENT))
        {
          String text = null;
          try
          {
            text = doc.get(regions[i].getOffset(), regions[i].getLength());
          }
          catch (BadLocationException e)
          {
            e.printStackTrace();
          }
          if (text != null)
          {
            Pattern pattern = Pattern.compile("\\${1}\\d{1,2}");
            Matcher matcher = pattern.matcher(text);
            while (matcher.find())
            {
              String match = matcher.group();
              if (mssArgsDescr.get(match) == null)
              {
                mssArgsDescr.put(match,ARG_DEFAULT_HINT);
              }
            }
          }
        }
      }
  }
  
  // -- DEBUGGIN' --
  
  /**
   * Dumps the contents of this object to stdout.
   */
  public void dump()
  {
    if (bPersistent) System.out.print("Persistent properties of file ");
    else             System.out.print("Properties of file ");
    System.out.println(iFile!=null?iFile.getFullPath():"(null)");
    System.out.println("- Working dir.: "+getWorkDir());
    System.out.println("- Arguments   : "+lsArgs.size());
    for (int i=0; i<lsArgs.size(); i++)
    {
      String sDescr = getArgDescription(i+1);
      if (sDescr==null) sDescr = "?";
      System.out.print("  $"+(i+1)+"=\""+lsArgs.get(i)+"\" - "+sDescr.trim());
      System.out.println(isArgExtra(i+1)?" *":"");
    }
  }
  
}
