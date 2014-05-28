/*
 * Created on 08.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors.def;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonDocumentProvider;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy;

/**
 * this class inherits the abstract class <code>AbstractEditor</code> and defines an editor for
 * def-scripts
 * 
 * @author Xian
 * 
 */
public class DEFEditor extends AbstractEditor
{

  // private ProjectionSupport fProjectionSupport;

  /**
   * Constructor
   * 
   */
  public DEFEditor()
  {
    super();
//    setPreferenceStore(DLabProPlugin.getDefault().getPreferenceStore());
    setSourceViewerConfiguration(new DEFSourceViewerConfig(this, DLabProPlugin.getDefault()
        .getPreferenceStore()));
    setDocumentProvider(new CommonDocumentProvider());
    

  }
  
  /*
   * (non-Javadoc)
   * @see de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor#initializeEditor()
   */
  protected void initializeEditor() {
	super.initializeEditor();
	setEditorContextMenuId("#DefEditorContext");
}

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor#getOutlinePage()
   */
  protected CommonOutlinePage getOutlinePage()
  {
    return new DefOutlinePage(this);
  }

  @Override
  public CommonReconcilingStrategy getReconcilingStrategy()
  {
    return new DefReconcilingStrategy(this);
  }


}
