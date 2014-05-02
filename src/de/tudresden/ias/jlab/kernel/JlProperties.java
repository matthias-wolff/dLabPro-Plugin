package de.tudresden.ias.jlab.kernel;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class JlProperties extends java.util.Properties
{

  private static final long serialVersionUID = -8618161458391906241L;
  
  /**
   * A list to store exceptions in (to be supplied through {@link #setExceptionList(Vector)}
   */
  private Vector<Exception> vExceptions;

  /**
   * Creates a new jLab properties object.
   */
  public JlProperties()
  {
    super();
  }

  /**
   * Creates a new jLab properties object and initializes it from a properties
   * file.
   *
   * @param sFilename
   *          The file name
   * @throws IOException
   *          on file errors
   */
  public JlProperties(String sFilename) throws IOException
  {
    super();
    loadFromPropFile(sFilename);
  }
  
  // -- Getters and setters --
  
  /**
   * Sets a new list to store exceptions in.
   * 
   * @param vExceptions
   *          The exception list.
   */
  public void setExceptionList(Vector<Exception> vExceptions)
  {
    this.vExceptions = vExceptions;
  }
  
  // -- Storing and loading of properties files --
  
  /**
   * Loads this properties object from a properties file
   * 
   * @param sFilename
   *          The file name
   * @throws IOException
   *          on file errors
   */
  public void loadFromPropFile(String sFilename)
  throws IOException
  {
    FileReader iFrd = new FileReader(sFilename);
    load(iFrd);
    iFrd.close();
  }
  
  /**
   * Stores this properties object into a properties file. 
   * 
   * @param sFilename
   *          The file name
   * @param sComments
   *          A description of the property list (may be <code>NULL</code>)
   * @throws IOException
   *          on file errors
   */
  public void storeToPropFile(String sFilename, String sComments)
  throws IOException
  {
    FileWriter iFwr = new FileWriter(sFilename);
    store(iFwr,sComments);
    iFwr.close();
  }

  // -- Property setters and getters --
  
  /**
   * Retrieves the integer value of a property.
   * 
   * @param sKey
   *          The key
   * @param nDefault
   *          The default value to return if no property with the key
   *          <code>sKey</code> is found or the value cannot be converted into
   *          an integer
   * @param bAutoStore
   *          If <code>true</code> and <code>sKey</code> does not exist, the
   *          key will be stored in the properties with the default value. 
   * @return the integer value
   */
  public int getInt(String sKey, int nDefault, boolean bAutoStore)
  {
    String sDefault = String.valueOf(nDefault);
    if (!containsKey(sKey))
    {
      if (vExceptions!=null)
        vExceptions.add(new Exception("Key \""+sKey+"\" not found."));
      if (bAutoStore) setProperty(sKey,sDefault);
    }
    String sVal = getProperty(sKey,sDefault);
    try
    {
      return Integer.valueOf(sVal);
    }
    catch (NumberFormatException e)
    {
      if (vExceptions!=null) vExceptions.add(e);
      JlObject.WARNING("Parse error retrieving int property "+
          sKey+"=\""+sVal+"\". Using default value ("+nDefault+").");
      return nDefault;
    }
  }
  
  /**
   * Retrieves the float value of a property.
   * 
   * @param sKey
   *          The key
   * @param nDefault
   *          The default value to return if no property with the key
   *          <code>sKey</code> is found or the value cannot be converted into
   *          a double
   * @param bAutoStore
   *          If <code>true</code> and <code>sKey</code> does not exist, the
   *          key will be stored in the properties with the default value. 
   * @return the double value
   */
  public float getFloat(String sKey, float nDefault, boolean bAutoStore)
  {
    String sDefault = String.valueOf(nDefault);
    if (!containsKey(sKey))
    {
      if (vExceptions!=null)
        vExceptions.add(new Exception("Key \""+sKey+"\" not found."));
      if (bAutoStore) setProperty(sKey,sDefault);
    }
    String sVal = getProperty(sKey,sDefault);
    try
    {
      return Float.valueOf(sVal);
    }
    catch (NumberFormatException e)
    {
      if (vExceptions!=null) vExceptions.add(e);
      JlObject.WARNING("Parse error retrieving double property "+
          sKey+"=\""+sVal+"\". Using default value ("+nDefault+").");
      return nDefault;
    }
  }

  /**
   * Retrieves the string value of a property.
   * 
   * @param sKey
   *          The key
   * @param sDefault
   *          The default value to return if no property with the key
   *          <code>sKey</code> is found
   * @param bAutoStore
   *          If <code>true</code> and <code>sKey</code> does not exist, the
   *          key will be stored in the properties with the default value. 
   * @return The value 
   */
  public String getString(String sKey, String sDefault, boolean bAutoStore)
  {
    if (!containsKey(sKey))
    {
      if (vExceptions!=null)
        vExceptions.add(new Exception("Key \""+sKey+"\" not found."));    
      if (bAutoStore) setProperty(sKey,sDefault);
    }
    return getProperty(sKey,sDefault);
  }
  
}
