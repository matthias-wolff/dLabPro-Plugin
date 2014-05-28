
package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ArrayList;

public class CommonModel extends CommonElement
{

  private CommonElement outlineParentElement      = null;

  private ArrayList<CommonElement> children                  = new ArrayList<CommonElement>();

  private ArrayList<CommonElement> ignoreOutlinePageElements = new ArrayList<CommonElement>();

  /**
   * 
   * @return all elements which are not used in outline page
   * @deprecated never used locally
   */
  public CommonElement[] getIgnoreOutlinePageElements()
  {
    return (CommonElement[])ignoreOutlinePageElements
        .toArray(new CommonElement[ignoreOutlinePageElements.size()]);
  }

  /**
   * this method returns all elements used in outline page. to get all children, the ignored ones to
   * use {@link #getAllChildren()}
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonElement#getChildren()
   * @see #getAllChildren()
   */
  public CommonElement[] getChildren()
  {
    ArrayList<CommonElement> result = new ArrayList<CommonElement>(1);
    if (outlineParentElement != null) result.add(outlineParentElement);
    if (children.size() > 0) result.addAll(children);
    return result.toArray(new CommonElement[result.size()]);
  }

  /**
   * 
   * @return all children, for example for code folding
   */
  public CommonElement[] getAllChildren()
  {
    ArrayList<CommonElement> result = new ArrayList<CommonElement>(3);
    if (children != null) result.addAll(children);
    if (outlineParentElement != null) result.add(outlineParentElement);
    if (ignoreOutlinePageElements != null) result.addAll(ignoreOutlinePageElements);
    return result.toArray(new CommonElement[result.size()]);
  }

  /**
   * Constructor
   * 
   */
  public CommonModel()
  {
    super(null, null, -1, -1, -1);
  }

  /**
   * 
   * @return the parent element of the outline page
   */
  public CommonElement getOutlineParentElement()
  {
    return outlineParentElement;
  }

  /**
   * sets the parent element of the outline page. this element should represent the class element or
   * something else.
   * 
   * @param outlineParentElement -
   *          the element to be set as outline page parent element
   */
  public void setOutlineParentElement(CommonElement outlineParentElement)
  {
    this.outlineParentElement = outlineParentElement;
  }

  /**
   * adds a element to internal list, which will not be used from outline page. this may be elements
   * which only are relevant for code folding.
   * 
   * @param element -
   *          the element to add to the outline page ignore list
   */
  public void addIgnoreOutlinePageElement(CommonElement element)
  {
    ignoreOutlinePageElements.add(element);
  }

  /**
   * 
   * @param element
   */
  public void addChildren(CommonElement element)
  {
    children.add(element);
  }

  public void removeChildren(CommonElement element)
  {
    children.remove(element);
  }
  
}
