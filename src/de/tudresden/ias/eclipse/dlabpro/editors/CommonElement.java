
package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ArrayList;

/**
 * This class represents the abstract superclass of elements representing DLabPro document parts.
 * The class is used as a kind of Composite pattern.
 * 
 * @author Christian Feig
 * 
 */
public abstract class CommonElement
{
  protected static CommonElement[] NO_CHILDREN = new CommonElement[0];

  private CommonElement            fParent;
  private String                   fName;
  private int                      fOffset;
  private int                      fLength;
  private int                      fType;

  /**
   * Constructor
   * 
   * @param parent -
   *          the parent element
   * @param name -
   *          the name of the element (maybe represents the text to show, the title or something
   *          else)
   * @param offset -
   *          the offset of text range
   * @param length -
   *          the length of text range
   * @param type -
   *          the type text range
   */
  public CommonElement(CommonElement parent, String name, int offset, int length, int type)
  {
    fParent = parent;
    fName = name;
    fOffset = offset;
    fLength = length;
    fType = type;
  }

  /**
   * 
   * @return the parent element
   */
  public CommonElement getParent()
  {
    return fParent;
  }

  public abstract CommonElement[] getChildren();

  /**
   * 
   * @return the name of the element
   */
  public String getName()
  {
    return fName;
  }

  /**
   * 
   * @return the offset
   */
  public int getOffset()
  {
    return fOffset;
  }

  /**
   * 
   * @return the length
   */
  public int getLength()
  {
    return fLength;
  }

  /**
   * 
   * @return the type
   */
  public int getType()
  {
    return fType;
  }

  /**
   * 
   * @param length -
   *          sets the length of text elements range
   */
  protected void setLength(int length)
  {
    fLength = length;
  }

  public ArrayList<CommonElement> getAllChildren(boolean bNoLeafs)
  {
    ArrayList<CommonElement> list = new ArrayList<CommonElement>();
    if (getChildren()==null) return list;
    
    for (CommonElement child : getChildren())
    {
      if (child.getChildren().length>0 || !bNoLeafs)
        list.add(child);
      list.addAll(child.getAllChildren(bNoLeafs));
    }
    
    return list;
  }
  
  public boolean equalsByQualifiedName(CommonElement other)
  {
    String s1 = getQualifiedName();
    String s2 = other.getQualifiedName();
    return s1.equals(s2);
  }

  public String getQualifiedName()
  {
    String sQn = "";
    for (CommonElement e = this; e!=null; e=e.fParent)
    {
      if (sQn.length()>0) sQn = "#"+sQn;
      sQn = e.fName + sQn;
    }
    return sQn;
  }
  

}
