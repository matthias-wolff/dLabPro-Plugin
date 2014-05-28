
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;

/**
 * This class is used to compare two instances of CommonElement
 * 
 * @author Christian Feig
 * 
 */
public class Comparator implements java.util.Comparator
{

  /*
   * (non-Javadoc)
   * 
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(Object o1, Object o2)
  {
    if (o1 instanceof CommonElement && o2 instanceof CommonElement)
    {
      CommonElement elem1 = (CommonElement)o1;
      CommonElement elem2 = (CommonElement)o2;
      return elem1.getName().compareTo(elem2.getName());
    }
    return 0;
  }
}
