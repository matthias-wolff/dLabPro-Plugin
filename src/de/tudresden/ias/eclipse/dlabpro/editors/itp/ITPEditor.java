/*
 * Created on 08.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonOutlinePage;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy;

/**
 * this class inherits the abstract class <code>AbstractEditor</code> and defines an editor for def-scripts
 * 
 * @author Xian
 * 
 */
public class ITPEditor extends AbstractEditor
{

  // private ProjectionSupport fProjectionSupport;

  /**
   * Constructor
   * 
   */
  public ITPEditor()
  {
    super();
    // configureInsertMode(SMART_INSERT, false);
    // setInsertMode(INSERT);
    // setPreferenceStore(DLabProPlugin.getDefault().getPreferenceStore());

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#initializeEditor()
   */
  protected void initializeEditor()
  {
    super.initializeEditor();
    setSourceViewerConfiguration(new ITPSourceViewerConfig(this,DLabProPlugin
        .getDefault().getPreferenceStore()));
    setDocumentProvider(new ItpDocumentProvider());
    setEditorContextMenuId("#ItpEditorContext");
  };

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.AbstractEditor#getOutlinePage()
   */
  protected CommonOutlinePage getOutlinePage()
  {
    return new ItpOutlinePage(this);
  }

  @Override
  public CommonReconcilingStrategy getReconcilingStrategy()
  {
    return new ItpReconcilingStrategy(this);
  }

}
