
package de.tudresden.ias.eclipse.dlabpro.views.toctree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import de.tudresden.ias.eclipse.dlabpro.editors.def.model.Comparator;

public class TocNode
{

  protected TocNode[] NO_CHILDREN = new TocNode[0];

  private TocNode     fParent;

  private String      fName;

  private String      fLink;

  private String      fIconCol;

  private String      fIconExp;

  private String      fToolTip;

  private String      fDefault;

  Vector              childs      = null;

  public TocNode(TocNode parent, String name)
  {
    fParent = parent;
    fName = name;
  }

  public TocNode getParent()
  {
    return fParent;
  }

  public void setParent(TocNode parent)
  {
    this.fParent = parent;
  }

  public TocNode[] getChildren()
  {
    TocNode[] elementArray = null;
    if (childs != null)
    {
      List l = Arrays.asList(childs.toArray());
      Collections.sort(l, new Comparator());
      elementArray = (TocNode[])l.toArray(new TocNode[l.size()]);
    }

    return elementArray == null ? NO_CHILDREN : elementArray;
  }

  public String getName()
  {
    return fName;
  }

  public void setName(String name)
  {
    this.fName = name;
  }

  public void addChild(TocNode child)
  {
    if (childs == null) childs = new Vector();
    childs.add(child);
  }

  public String getFIconCol()
  {
    return fIconCol;
  }

  public void setFIconCol(String iconCol)
  {
    fIconCol = iconCol;
  }

  public String getFIconExp()
  {
    return fIconExp;
  }

  public void setFIconExp(String iconExp)
  {
    fIconExp = iconExp;
  }

  public String getFLink()
  {
    return fLink;
  }

  public void setFLink(String link)
  {
    fLink = link;
  }

  public String getFName()
  {
    return fName;
  }

  public void setFName(String name)
  {
    fName = name;
  }

  public String getFToolTip()
  {
    return fToolTip;
  }

  public void setFToolTip(String toolTip)
  {
    fToolTip = toolTip;
  }

  public String getFDefault()
  {
    return fDefault;
  }

  public void setFDefault(String default1)
  {
    fDefault = default1;
  }

}
