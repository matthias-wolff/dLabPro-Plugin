// dLabPro Plugin for Eclipse
// - VisEditor problem-with-display-data exception
// 

package de.tudresden.ias.eclipse.dlabpro.editors.vis.data;

/**
 * Exceptions of this class are thrown when there are any problems with the
 * display data.
 */
public class DataException extends Exception
{
  private String sMsg; 
  
  public DataException(String sMsg)
  {
    this.sMsg = sMsg;
  }
  
  public String getMessage()
  {
    return sMsg;
  }

  private static final long serialVersionUID = -4340348536924446080L;
}

// EOF
