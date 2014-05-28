/* jLab type JlData
 * - jLab data table
 *
 * AUTHOR  : Matthias Wolff
 * UPDATE  : $Date: 2013-02-20 13:52:02 +0100 (Mi, 20 Feb 2013) $, $Author: wolff $
 *           $Revision: 182 $
 * PACKAGE : de.tudresden.ias.jlab.kernel
 * RCS-ID  : $Id: JlData.java 182 2013-02-20 12:52:02Z wolff $
 */

package de.tudresden.ias.jlab.kernel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * @author Matthias Wolff
 * @author Matthias Eichner
 */
class JlDataComp implements Serializable
{
  
  // Fields
  private static final long serialVersionUID = 5451199348706278262L;
  protected String          sName;
  protected Class<?>        cType;
  protected Object          iData;

  // Constructors
  protected JlDataComp()
  {
    sName = "";
    cType = null;
    iData = null;
  }

  JlDataComp(Class<?> cType, String sName, int nLength)
  {
    this.sName = sName;
    this.cType = cType;
    this.iData = nLength > 0 ? Array.newInstance(cType, nLength) : null;
  }

  // Getters an setters
  public Object getData()
  {
    return iData;
  }

  public int getLength()
  {
    return iData == null ? 0 : Array.getLength(iData);
  }

  public String getName()
  {
    return sName;
  }

  public void setName(String sName)
  {
    this.sName = sName;
  }

  public Class<?> getType()
  {
    return cType;
  }

  // Memory (re-)allocation
  public void allocate(int nLength)
  {
    if (nLength == getLength()) return; // That's ok
    if (nLength < 0) nLength = 0;

    int nCopy = getLength();
    if (nCopy > nLength) nCopy = nLength;

    Object iGhost = iData;
    iData = Array.newInstance(getType(), nLength);
    if (iGhost != null) System.arraycopy(iGhost, 0, iData, 0, nCopy);
  }
}

/**
 * jLab data table
 * 
 * @author Matthias Wolff
 * @author Matthias Eichner
 */

public class JlData extends JlObject
{
  private static final String DEL
  = "------------------------------------------------"
  + "------------------------------------------------";
  
  // Fields
  private static final long serialVersionUID = -4704444440684567982L;
  protected JlDataComp[]    aComps           = null;
  protected int             nLength          = 0;     // aka. nrec
  protected int             nCapacity        = 0;     // aka. maxrec
  
  // Record fields
  public double	rinc		= 0.0;
  public double	rwid		= 0.0;
  public double	rofs		= 0.0;
  public double noffset = 0.0;
  public String	runit		= "";
  
  // Component fields
  public double	cinc		= 0.0;
  public double cofs		= 0.0;
  public String cunit		= "";
  
  // Value fields
  public String vunit   = "";
  public double vinc    = 0.0;

  // Block fields
  public long   nblock  = 0;
  
  // Description fields
  public double descr0  = 0.0;
  public double descr1  = 0.0;
  public double descr2  = 0.0;
  public double descr3  = 0.0;
  public double descr4  = 0.0;
  public String rtext   = "";
  public String ftext   = "";
  public String vrtext  = "";
  
  // Constructors
  public JlData()
  {
  }

  public JlData(JlData idSrc)
  {
    copy(idSrc);
  }
  
  /**
   * Creates a new data instance from a double array.
   *  
   * @param aData
   *          The initialization data
   * @param sCompName
   *          The component name
   */
  public JlData(double[] aData, String sCompName)
  {
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class, 1);
    JlDataComp comp = new JlDataComp(double.class,sCompName,aData.length);
    comp.iData = aData;
    aComps[0] = comp;
    nCapacity = aData.length;
    nLength = aData.length;
  }
  
  /**
   * Creates a new data instance from a float array.
   * 
   * @param aData
   *          The initialization data
   * @param sCompName
   *          The component name
   */
  public JlData(float[] aData, String sCompName)
  {
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class, 1);
    JlDataComp comp = new JlDataComp(float.class,sCompName,aData.length);
    comp.iData = aData;
    aComps[0] = comp;
    nCapacity = aData.length;
    nLength = aData.length;
  }

  /**
   * Creates a new data instance from a short array.
   * 
   * @param aData
   *          The initialization data
   * @param sCompName
   *          The component name
   */
  public JlData(short[] aData, String sCompName)
  {
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class,1);
    JlDataComp comp = new JlDataComp(short.class,sCompName,aData.length);
    comp.iData = aData;
    aComps[0] = comp;
    nCapacity = aData.length;
    nLength = aData.length;
  }

  /**
   * Create JlData instance from array that contains a number of data vectors. nVectorLen defines
   * the length of the vectors. Each vector is stored in a new component. The last component is
   * filled with zeros if necessary.
   * 
   * FIXME: MWX 2001-01-22: This is contra-intuitive, vector components should be data components
   * and not data records!
   * 
   * @param aFloat
   *          float array containing the data
   * @param nVectorLen
   *          length of each vector in aFloat
   */
  public JlData(float[] aFloat, int nVectorLen)
  {

    if (aFloat.length <= 0) return;
    int nComps = aFloat.length / nVectorLen;
    float[] b = new float[nVectorLen];

    if (aFloat.length % nVectorLen != 0.0) nComps++;
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class, nComps);

    for (int i = 0; i < nComps; i++)
    {
      JlDataComp comp = new JlDataComp(float.class, "comp_" + i, nVectorLen);
      System.arraycopy(aFloat, i * nVectorLen, b, 0, aFloat.length < nVectorLen ? aFloat.length
          : nVectorLen);
      comp.iData = b.clone();
      aComps[i] = comp;
    }

    nCapacity = nVectorLen;
    nLength = nVectorLen;
  }

  public JlData(short[] aShort, int nVectorLen)
  {

    if (aShort.length <= 0) return;
    int nComps = aShort.length / nVectorLen;
    short[] b = new short[nVectorLen];

    if (aShort.length % nVectorLen != 0.0) nComps++;
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class, nComps);

    for (int i = 0; i < nComps; i++)
    {
      JlDataComp comp = new JlDataComp(short.class, "comp_" + i, nVectorLen);
      System.arraycopy(aShort, i * nVectorLen, b, 0, aShort.length < nVectorLen ? aShort.length
          : nVectorLen);
      comp.iData = b.clone();
      aComps[i] = comp;
    }

    nCapacity = nVectorLen;
    nLength = nVectorLen;
  }

  /**
   * Creates a new <code>JlData</code> instance from a formatted string.
   * <h4>Remarks</h4>
   * <ul>
   *   <li>The constructor does not try to parse the string items. All created
   *     components will be strings!</li>
   * </ul>
   * 
   * @param sText
   *          The formatted string
   * @param sVecDelim
   *          Set of characters delimiting vectors (lines) in
   *          <code>sText</code>, may be <code>null</code> if there should be
   *          only one vector in the data instance.
   * @param sCmpDelim
   *          Set of characters delimiting vector components (columns) in
   *          <code>sText</code>, may be <code>null</code> if there should be
   *          only one vector component in the data instance.
   */
  public JlData(String sText, String sVecDelim, String sCmpDelim)
  {
    
    if (sText==null || sText.length()==0) return;
    
    // Init from string
    String[] aV = JlObject.stringSplit(sText,sVecDelim);
    for (int nV=0; nV<aV.length; nV++)
    {
      String sR = aV[nV].trim();
      if (sR.length()==0) continue;
      String[] aC = JlObject.stringSplit(sR,sCmpDelim);
      for (int nC=0; nC<aC.length; nC++)
      {
        if (nC>=getDimension()) addComp(String.class,"S"+nC);
        allocate(nV+1); setNRecs(nV+1);
        sStore(aC[nC],nV,nC);
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    String sText = "";
    for (int nR=0; nR<getLength(); nR++)
    {
      for (int nC=0; nC<getDimension(); nC++)
        sText += sFetch(nR,nC) + "\t";
      if (nR<getLength()-1) sText += "\n";
    }
    return sText;
  }
  
  // Reset

  /**
   * Deallocates memory and destroys the data structure.
   */
  public void reset()
  {
    aComps    = null;
    nLength   = 0;
    nCapacity = 0;
    rinc      = 0.0;
    rwid      = 0.0;
    rofs      = 0.0;
    noffset   = 0.0;
    runit     = "";
    cinc      = 0.0;
    cofs      = 0.0;
    cunit     = "";
    vunit     = "";
    vinc      = 0.0;
    nblock    = 0;
    descr0    = 0.0;
    descr1    = 0.0;
    descr2    = 0.0;
    descr3    = 0.0;
    descr4    = 0.0;
    rtext     = "";
    ftext     = "";
    vrtext    = "";
  }

  // Component structure

  /**
   * Retrieves the data contents of a component. The returned value is a pointer to an array object
   * of the component's variable type.
   * <H2>Example</H2>
   * 
   * <PRE>
   * 
   * x.AddComp(class.int,"MyIntComp"); ... int[] comp0 = x.GetComp(0);
   * 
   * </PRE>
   * 
   * @param nComp
   *          Zero-based index of component
   * 
   * @see #getCompName(int) GetCompName
   * @see #getCompType(int) GetCompType
   */
  public Object getComp(int nComp)
  {
    if (aComps == null) return null;
    if (nComp < 0 || nComp > aComps.length) return null;
    return aComps[nComp].getData();
  }

  /**
   * Retrieves the name of a component.
   * 
   * @param nComp
   *          Zero-based index of component
   * @see #setCompName(int, String) setCompName
   * @see #getComp(int) getComp
   * @see #getCompType(int) getCompType
   */
  public String getCompName(int nComp)
  {
    if (aComps == null) return null;
    if (nComp < 0 || nComp > aComps.length) return null;
    return aComps[nComp].getName();
  }

  /**
   * Sets the name of a component
   * 
   * @param nComp
   *          Zero-based index of component
   * @param sName
   *          The new name
   * @see #getCompName(int) getCompName
   */
  public void setCompName(int nComp, String sName)
  {
    if (aComps == null) return;
    if (nComp < 0 || nComp > aComps.length) return;
    aComps[nComp].setName(sName);
  }
    
  /**
   * Returns the variable type of a component. The returned value is a pointer to a class object.
   * 
   * @example x.addComp(x.getCompType(0),"MyNewComp");
   * 
   * @see #getComp(int) getComp
   * @see #getCompName(int) getCompName
   */
  public Class<?> getCompType(int nComp)
  {
    if (aComps == null) return null;
    if (nComp < 0 || nComp > aComps.length) return null;
    return aComps[nComp].getType();
  }

  /**
   * Returns the number of components.
   */
  public int getDimension()
  {
    if (aComps == null) return 0;
    return aComps.length;
  }

  /**
   * Adds a component at the end of the component list.
   * 
   * @param cType
   *          Pointer to class object identifying the variable type of the component to be added.
   *          Any variable type is valid, however, it is not recommended to use class types for
   *          primitive types (e.g. use int instead if Integer).
   * @param sName
   *          Name of component.
   * @return the zero-based index of the newly added component
   * 
   * @example x.addComp(double.class,"MyDoubleComp");
   * 
   * @see #addNComps(Class,int) addNComps
   */
  public int addComp(Class<?> cType, String sName)
  {
    int nDim = aComps == null ? 0 : aComps.length;
    JlDataComp[] aGhost = aComps;
    aComps = (JlDataComp[])Array.newInstance(JlDataComp.class, nDim + 1);
    if (aGhost != null) System.arraycopy(aGhost, 0, aComps, 0, nDim);
    aComps[nDim] = new JlDataComp(cType, sName, nCapacity);
    return nDim;
  }

  /**
   * Adds <code>nCount</code> components at the end of the component list.
   * 
   * @param cType
   *          Pointer to class object identifying the variable type of the components to be added.
   *          Any variable type is valid, however, it is not recommended to use class types for
   *          primitive types (e.g. use int instead if Integer).
   * @param nCount
   *          Number of components to be added.
   * @return the zero-based index of the first newly added component
   * 
   * @example // Add 10 components of type double x.addNComps(double.class,10);
   * 
   * @see #addComp(Class,String) addComp
   */
  public int addNComps(Class<?> cType, int nCount)
  {
    int nFirst = getDimension();
    for (int i = 0; i < nCount; i++)
      addComp(cType, "comp_" + String.valueOf(i + nFirst));
    return nFirst;
  }

  /**
   * Finds a component by its name and returns the zero-based index.
   * 
   * @param sName
   *          The component name
   * @return the zero-based index of the first component named
   *         <code>sName</code> or -1 if no such component is found
   */
  public int findComp(String sName)
  {
    if (aComps==null) return -1;
    for (int nC=0; nC<aComps.length; nC++)
      if (aComps[nC].sName!=null && aComps[nC].sName.equals(sName))
        return nC;
    return -1;
  }
  
  /**
   * Copies the component structure
   * 
   * @param idSrc
   *          Source instance
   */
  public void scopy(JlData idSrc)
  {
    reset();
    if (idSrc==null) return;
    for (int nC=0; nC<idSrc.getDimension(); nC++)
      addComp(idSrc.getCompType(nC),idSrc.getCompName(nC));
  }
  
  /**
   * Copies all description fields from a source data instance
   * 
   * @param idSrc
   *          The source data instance
   */
  public void dcopy(JlData idSrc)
  {
    rinc      = idSrc.rinc;
    rwid      = idSrc.rwid;
    rofs      = idSrc.rofs;
    noffset   = idSrc.noffset;
    runit     = idSrc.runit;
    cinc      = idSrc.cinc;
    cofs      = idSrc.cofs;
    cunit     = idSrc.cunit;
    vunit     = idSrc.vunit;
    vinc      = idSrc.vinc;
    nblock    = idSrc.nblock;
    descr0    = idSrc.descr0;
    descr1    = idSrc.descr1;
    descr2    = idSrc.descr2;
    descr3    = idSrc.descr3;
    descr4    = idSrc.descr4;
    rtext     = idSrc.rtext;
    ftext     = idSrc.ftext;
    vrtext    = idSrc.vrtext;
  }
  
  // Record structure

  public int getLength()
  {
    return nLength;
  }

  public int getCapacity()
  {
    return nCapacity;
  }

  public void incNRecs(int nInc)
  {
    nLength += nInc;
    if (nLength < 0) nLength = 0;
    if (nLength > nCapacity) nLength = nCapacity;
  }

  public void setNRecs(int nNewLength)
  {
    nLength = nNewLength;
    if (nLength < 0) nLength = 0;
    if (nLength > nCapacity) nLength = nCapacity;
  }

  public void allocate(int nNewLength)
  {
    for (int nComp = 0; nComp < getDimension(); nComp++)
      aComps[nComp].allocate(nNewLength);

    nCapacity = nNewLength;
    if (nLength > nCapacity) nLength = nCapacity;
  }

  /**
   * Adds records to this data instance. Present data content will be preserved.
   * 
   * @param nNumber
   *          The number of records to add
   * @param nIncrement
   *          The reallocation increment
   * @return the zero-based index of the first new record
   */
  public int addRecs(int nNumber, int nIncrement)
  {
    if (nNumber   <0             ) return nLength;
    if (nIncrement<nNumber       ) nIncrement = nNumber;
    if (nLength+nNumber>nCapacity) allocate(nLength+nIncrement);
    int nRet = nLength;
    setNRecs(nLength+nNumber);
    return nRet;
}
  
  // Methods - Fetch / Store
  // These methods are designed to work "always", i.e. they complete
  // without throwing exceptions for illegal types, array index out of
  // bounds, illegal number formats etc.

  public Object fetch(int nRec, int nComp)
  {
    try
    {
      return Array.get(aComps[nComp].getData(), nRec);
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }

    return null;
  }

  public void store(Object oVal, int nRec, int nComp)
  {
    try
    {
      Array.set(aComps[nComp].getData(), nRec, oVal);
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }
    catch (IllegalArgumentException e)
    {
    }
  }

  public double dFetch(int nRec, int nComp)
  {
    try
    {
      return ((double[])aComps[nComp].getData())[nRec];
    }
    catch (Exception e) {}
    try
    {
      if (isStringType(getCompType(nComp))) try
      {
        return Double.valueOf(sFetch(nRec, nComp)).doubleValue();
      }
      catch (/*NumberFormat*/Exception e)
      {
        return Double.NaN;
      }

      return Array.getDouble(aComps[nComp].getData(), nRec);
    }
    catch (Exception e)
    {
    }

    return Double.NaN;
  }

  public void dStore(double nVal, int nRec, int nComp)
  {
    try
    {
      Object iComp = aComps[nComp].getData();

      if (getCompType(nComp) == byte.class) Array.setByte(iComp, nRec, (byte)nVal);
      else if (getCompType(nComp) == short.class) Array.setShort(iComp, nRec, (short)nVal);
      else if (getCompType(nComp) == char.class) Array.setChar(iComp, nRec, (char)nVal);
      else if (getCompType(nComp) == int.class) Array.setInt(iComp, nRec, (int)nVal);
      else if (getCompType(nComp) == long.class) Array.setLong(iComp, nRec, (long)nVal);
      else if (getCompType(nComp) == float.class) Array.setFloat(iComp, nRec, (float)nVal);
      else if (getCompType(nComp) == double.class) Array.setDouble(iComp, nRec, nVal);
      else if (getCompType(nComp) == String.class) Array.set(iComp, nRec, String.valueOf(nVal));
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }
    catch (IllegalArgumentException e)
    {
    }
  }

  public String sFetch(int nRec, int nComp)
  {
    try
    {
      if (isStringType(getCompType(nComp))) return (String)fetch(nRec, nComp);
      if (isNumericType(getCompType(nComp))) return String.valueOf(dFetch(nRec, nComp));
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      return null;
    }

    return null;
  }

  public void sStore(String sVal, int nRec, int nComp)
  {
    try
    {
      if (isStringType(getCompType(nComp))) store(sVal, nRec, nComp);
      if (isNumericType(getCompType(nComp))) try
      {
        dStore(Double.valueOf(sVal).doubleValue(), nRec, nComp);
      }
      catch (NumberFormatException e)
      {
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }
  }

  public JlObject iFetch(int nRec, int nComp)
  {
    try
    {
      if (IsJlInstanceType(getCompType(nComp))) return (JlObject)fetch(nRec, nComp);
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }
    return null;
  }

  public void iStore(JlObject iVal, int nRec, int nComp)
  {
    store(iVal, nRec, nComp);
  }

  // Operations

  public void addArrayToComp(Object array, Class<?> aType, int nComp)
  {
    if (nComp > aComps.length - 1) return;
    if (aType != aComps[nComp].getType()) return;

    int aLength = Array.getLength(array);
    int oLength = getLength();
    int nLength = aLength + oLength;

    if (getCapacity() < nLength) allocate(nLength);
    if (getCapacity() < nLength) return;

    if (aComps[nComp].getData() == null) return;
    try
    {
      System.arraycopy(array, 0, aComps[nComp].getData(), oLength, aLength);
      setNRecs(nLength);
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
    }
    catch (IllegalArgumentException e)
    {
    }
  }

  public void copy(JlData idSrc)
  {
    reset();
    cat(idSrc);
    dcopy(idSrc);
  }
  
  /**
   * Append contents of <code>idSrc</code> to this instance.
   * 
   * @param idSrc
   *          Instance to append
   */
  public void cat(JlData idSrc)
  {
    if (idSrc==null) return;
    cat(idSrc,0,idSrc.getLength());
  }
  
  /**
   * Append records of <code>idSrc</code> to this instance.
   * 
   * @param idSrc
   *          Instance to append
   * @param nFirst
   *          Zero-based index of first record to append
   * @param nCount
   *          Number of records to append
   */
  public void cat(JlData idSrc, int nFirst, int nCount)
  {
    cat(idSrc,nFirst,nCount,0);
  }
  
  /**
   * Append records of <code>idSrc</code> to this instance.
   * <p>TODO: Implement for mismatching in component structures</p>
   * 
   * @param idSrc
   *          Instance to append
   * @param nFirst
   *          Zero-based index of first record to append
   * @param nCount
   *          Number of records to append
   * @param nIncrement
   *          Capacity increment on memory reallocation
   */
  public void cat(JlData idSrc, int nFirst, int nCount, int nIncrement)
  {
    if (idSrc==null) return;
    if (idSrc.getDimension()==0 || idSrc.getLength()==0) return;

    if (this.getDimension()==0)
    {
      for (int i=0; i<idSrc.getDimension(); i++)
        this.addComp(idSrc.getCompType(i), idSrc.getCompName(i));
      this.dcopy(idSrc);
    }

    if (!compMatch(this,idSrc))
    {
      ERROR("Component structures do not match.");
      return;
    }
    
    if (nFirst<0) nFirst=0;
    if (nFirst+nCount>=idSrc.getLength()) nCount=idSrc.getLength()-nFirst;
    if (nCount<=0) return;

    int nStart = this.getLength();
    if (nStart+nCount>this.getCapacity())
    {
      if (nIncrement<nCount) nIncrement = nCount;
      this.allocate(nStart+nIncrement);
    }
    for (int i = 0; i < this.getDimension(); i++)
      System.arraycopy(idSrc.getComp(i),nFirst,aComps[i].iData, nStart,nCount);
    this.setNRecs(nStart+nCount);
  }
  
  /**
   * Returns a data instance containing <code>nCount</code> records from this
   * instance starting at record <code>nFirst</code>. 
   * 
   * @param nFirst
   *          Zero-based index of the first record to select
   * @param nCount
   *          Number of records to select
   * @return a new data instance containing the selection
   */
  public JlData selectRecs(int nFirst, int nCount)
  {
    JlData idDst = new JlData();
    idDst.dcopy(this);
    idDst.cat(this,nFirst,nCount);
    return idDst;
  }
  
  /**
   * Deletes records from this instance.
   * 
   * @param nFirst
   *          The zero-based index of the first record to delete.
   * @param nCount
   *          The number of records to be deleted
   */
  public void deleteRecs(int nFirst, int nCount)
  {
    JlData idAux = new JlData(this);
    reset();
    dcopy(idAux);
    cat(idAux,0,nFirst);
    cat(idAux,nFirst+nCount,idAux.getLength()/*i.e. all remaining*/);
  }
  
  /**
   * Joins all components of <code>idSrc</code> to this instance.
   * 
   * @param idSrc
   *          The source data instance.
   */
  public void join(JlData idSrc)
  {
    if (idSrc==null || idSrc.getDimension()==0) return;
    for (int nC=0; nC<idSrc.getDimension(); nC++) copyJoinComp(idSrc,nC);
  }

  /**
   * Copies one component from <code>idSrc</code> and joins it to this instance.
   * 
   * @param idSrc
   *          The source data instance
   * @param nComp
   *          The zero-based index of the component to copy
   */
  protected void copyJoinComp(JlData idSrc, int nComp)
  {
    if (idSrc==null) return;
    if (nComp<0 || nComp>=idSrc.getDimension()) return;

    this.addComp(idSrc.getCompType(nComp),idSrc.getCompName(nComp));
    allocate(Math.max(idSrc.getLength(),this.getLength()));
    setNRecs(getCapacity());
    System.arraycopy(idSrc.getComp(nComp),0,aComps[aComps.length-1].iData,0,
        idSrc.getLength());
  }
  
  /**
   * Returns a data instance containing <code>nCount</code> components from this
   * instance starting at component <code>nFirst</code>. 
   * 
   * @param nFirst
   *          Zero-based index of the first component to select
   * @param nCount
   *          Number of components to select
   * @return a new data instance containing the selection
   */
  public JlData selectComps(int nFirst, int nCount)
  {
    JlData idDst = new JlData();
    idDst.dcopy(this);
    for (int nC=0; nC<nCount; nC++)
      idDst.copyJoinComp(this,nC+nFirst);
    return idDst;
  }
  
  /**
   * Deletes components from this instance.
   * 
   * @param nFirst
   *          The zero-based index of the first component to delete.
   * @param nCount
   *          The number of components to be deleted
   */
  public void deleteComps(int nFirst, int nCount)
  {
    if (aComps==null) return;
    if (nFirst<0) nFirst = 0;
    if (nFirst+nCount>aComps.length) nCount = aComps.length-nFirst;
    
    JlDataComp[] aGhost = aComps;
    aComps = new JlDataComp[aGhost.length-nCount];
    for (int nCs=0, nCd=0; nCs<aGhost.length; nCs++)
      if (nCs<nFirst || nCs>=nFirst+nCount)
        aComps[nCd++]=aGhost[nCs];
  }
  
  /**
   * Checks if the component structure of two JlData instances match
   * 
   * @param iIn1
   *          input instance 1
   * @param iIn2
   *          input instance 2
   * @return true if component structure match, false otherwise
   */
  public boolean compMatch(JlData iIn1, JlData iIn2)
  {
    if (iIn1.getDimension() != iIn2.getDimension()) return false;

    for (int i = 0; i < iIn1.getDimension(); i++)
      if (iIn1.getCompType(i) != iIn2.getCompType(i)) return false;

    return true;
  }

  // Print and Debug
  protected int[] getColWidths()
  {
    int[] cw = new int[getDimension()];
    for (int i = 0; i < getDimension(); i++)
    {
      if (getCompType(i) == byte.class) cw[i] = -4;
      else if (getCompType(i) == short.class) cw[i] = -8;
      else if (getCompType(i) == char.class) cw[i] = -7;
      else if (getCompType(i) == int.class) cw[i] = -8;
      else if (getCompType(i) == long.class) cw[i] = -10;
      else if (getCompType(i) == float.class) cw[i] = -8;
      else if (getCompType(i) == double.class) cw[i] = -10;
      else if (getCompType(i) == String.class) cw[i] = 24;
      else cw[i] = 12;

      if (Math.abs(cw[i]) < getCompName(i).length())
      {
        int sign = (cw[i] < 0) ? -1 : 1;
        cw[i] = sign * getCompName(i).length();
      }
    }
    return cw;
  }

  public void descr()
  {    
    System.out.println("");
    System.out.println(DEL);
    System.out.println("Instance of class " + getClass().getName());
    System.out.println(DEL);
    System.out.println("");

    // Print fields
    System.out.println(String.format("  nrec    = %d",getLength())   );
    System.out.println(String.format("  maxrec  = %d",getCapacity()) );
    System.out.println(String.format("  dim     = %d",getDimension()));
    System.out.println(String.format("  rinc    = %f",rinc)          );
    System.out.println(String.format("  rwid    = %f",rwid)          );
    System.out.println(String.format("  rofs    = %f",rofs)          );
    System.out.println(String.format("  noffset = %f",noffset)       );
    System.out.println(String.format("  runit   = \"%s\"",runit)     );
    System.out.println(String.format("  cinc    = %f",cinc)          );
    System.out.println(String.format("  cofs    = %f",cofs)          );
    System.out.println(String.format("  cunit   = \"%s\"",cunit)     );
    System.out.println(String.format("  vunit   = \"%s\"",vunit)     );
    System.out.println(String.format("  vinc    = %f",vinc)          );
    System.out.println(String.format("  nblock  = %d",nblock)        );
    System.out.println(String.format("  descr0  = %f",descr0)        );
    System.out.println(String.format("  descr1  = %f",descr1)        );
    System.out.println(String.format("  descr2  = %f",descr2)        );
    System.out.println(String.format("  descr3  = %f",descr3)        );
    System.out.println(String.format("  descr4  = %f",descr4)        );
    System.out.println(String.format("  rtext   = \"%s\"",rtext)     );
    System.out.println(String.format("  ftext   = \"%s\"",ftext)     );
    System.out.println(String.format("  vrtext  = \"%s\"",vrtext)    );
    
    // Print Components
    System.out.println("");
    System.out.println(DEL);
    System.out.println(" Components");
    System.out.println("");
    if (getDimension() == 0) System.out.println("   [no components defined]");
    else
    {
      for (int i = 0; i < getDimension(); i++)
      {
        System.out.print(" ");
        System.out.print(padString("" + i, -3));
        System.out.print(": ");
        String sType = getCompType(i).toString();
        if (sType.endsWith("java.lang.String")) sType = "string";
        if (sType.endsWith(".JlData")) sType = "JlData";
        System.out.print(padString(sType, 7) + " ");
        System.out.println(getCompName(i));
        // if (JlConsole.PrintStop()) return false;
      }
    }

    System.out.println("");
    System.out.println(DEL);
    System.out.println("");
  }

  public void print()
  {
    // JlConsole.InitPrintStop();

    // Begin Table
    System.out.println("");
    System.out.println(DEL);
    System.out.println("Instance of class " + getClass().getName());
    System.out.println(DEL);
    System.out.println("");

    // General information
    System.out.println("");
    System.out.println(" Capacity : " + getCapacity());
    System.out.println(" Length   : " + getLength());
    System.out.println(" Dimension: " + getDimension());

    // Header
    int cw[] = getColWidths();
    System.out.println("");
    for (int i = 0; i < getDimension(); i++)
    {
      System.out.print(" ");
      System.out.print(padString(getCompName(i), cw[i]));
    }
    System.out.println("");
    System.out.println("");

    // Contents
    if (getLength() == 0) System.out.println("   [no data available]");
    else
    {
      for (int nRec = 0; nRec < getLength(); nRec++)
      {
        for (int nComp = 0; nComp < getDimension(); nComp++)
        {
          System.out.print(" ");
          try
          {
            System.out.print(padString(fetch(nRec, nComp).toString(), cw[nComp]));
          }
          catch (NullPointerException e)
          {
            System.out.print(padString("(null)", cw[nComp]));
          }
        }
        System.out.println("");
        // if (JlConsole.PrintStop()) return;
      }
    }

    // Finish Table
    System.out.println("");
    System.out.println(DEL);
    System.out.println("");
  }

  public void LOG(String sFilename)
  {
    try
    {
      JlDataFile.writeXmlFile(this,new File(sFilename));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}

/* EOF */
