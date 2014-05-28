/*
 * Created on 11.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.CharacterChainScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.CommentScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.PreprocessorWithBracketsScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.PreprocessorWithLeadingDollarScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.SyntaxOffEolKeywordScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.SyntaxOffKeywordScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.util.ColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * 
 * @author Christian Feig
 * @see org.eclipse.jface.text.source.SourceViewerConfiguration
 */
public abstract class CommonSourceViewerConfiguration extends SourceViewerConfiguration implements
    IPreferenceConstants, IKeywordConstants
{

  private ColorManager       colorManager;
  private IPreferenceStore   preferenceStore;
  private Preferences        preferenceCoreStore;
  private PreferenceListener preferenceListener = new PreferenceListener();

  /**
   * Constructor
   * 
   * @param store -
   *          the {@link IPreferenceStore} to be used
   */
  public CommonSourceViewerConfiguration(IPreferenceStore store)
  {
    this(store, null);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTabWidth(org.eclipse.jface.text.source.ISourceViewer)
   */
  public int getTabWidth(ISourceViewer sourceViewer) {
	  return EditorsPlugin.getDefault().getPreferenceStore().getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
  }
  
  /**
   * Constructor
   * 
   * @param store -
   *          the {@link IPreferenceStore} to be used, must not be null
   * @param coreStore
   *          the {@link Preferences} to be used, can be null
   */
  public CommonSourceViewerConfiguration(IPreferenceStore store, Preferences coreStore)
  {
    preferenceStore = store;
    getPreferenceStore().addPropertyChangeListener(preferenceListener);
    preferenceCoreStore = coreStore;

    if (preferenceCoreStore != null) preferenceCoreStore
        .addPropertyChangeListener(preferenceListener);

  }

  /**
   * 
   * @param colorManager -
   *          the {@link ColorManager} to be used, must not be null
   * @param store -
   *          the {@link IPreferenceStore} to be used, must not be null
   * @return the used {@link AbstractScanner}
   */
  protected abstract AbstractScanner getCodeScanner(ColorManager colorManager,
      IPreferenceStore store);

  /**
   * 
   * @return the used {@link AbstractScanner}
   */
  private AbstractScanner getCodeScanner()
  {
    return getCodeScanner(getColorManager(), getPreferenceStore());
  }

  /**
   * this method configures the Damager and Repairer which manages syntax highlighting
   * 
   * @return returns the configured IPresentationReconciler
   */
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
  {

    PresentationReconciler reconciler = new PresentationReconciler();
    DefaultDamagerRepairer dr = null;

    // sets the default code scanner
    if (getCodeScanner() != null)
    {
      dr = new DefaultDamagerRepairer(getCodeScanner());
      reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
      reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }

    // paints the comment areas
    dr = new DefaultDamagerRepairer(new CommentScanner(getColorManager(), getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.P_CLR_COMMENT);
    reconciler.setRepairer(dr, IPreferenceConstants.P_CLR_COMMENT);

    // paints the preprocessor arguments of type ${...}
    dr = new DefaultDamagerRepairer(new PreprocessorWithBracketsScanner(getColorManager(),
        getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.PREPROCESSOR_WITH_BRACKETS);
    reconciler.setRepairer(dr, IPreferenceConstants.PREPROCESSOR_WITH_BRACKETS);

    // paints the preprocessor arguments of type $1..$n
    dr = new DefaultDamagerRepairer(new PreprocessorWithLeadingDollarScanner(getColorManager(),
        getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.PREPROCESSOR_WITH_LEADING$);
    reconciler.setRepairer(dr, IPreferenceConstants.PREPROCESSOR_WITH_LEADING$);

    // paints the characterchains
    dr = new DefaultDamagerRepairer(new CharacterChainScanner(getColorManager(),
        getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.P_CLR_STRING);
    reconciler.setRepairer(dr, IPreferenceConstants.P_CLR_STRING);

    // // paints the formel interpreter
    // dr = new DefaultDamagerRepairer(new
    // FormularScanner(getColorManager(), getPreferenceStore()));
    // reconciler.setDamager(dr,
    // CommonDocumentPartitionScanner.FORMELINTERPRETER);
    // reconciler.setRepairer(dr,
    // CommonDocumentPartitionScanner.FORMELINTERPRETER);

    // // paints the preprocessor arguments of type ${...}
    // dr = new DefaultDamagerRepairer(new PreprocessorWithBracketsScanner(
    // getColorManager(), getPreferenceStore()));
    // reconciler.setDamager(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_BRACKETS);
    // reconciler.setRepairer(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_BRACKETS);
    //
    // // paints the preprocessor arguments of type $1..$n
    // dr = new DefaultDamagerRepairer(new PreprocessorWithLeadingDollarScanner(
    // getColorManager(), getPreferenceStore()));
    // reconciler.setDamager(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_LEADING$);
    // reconciler.setRepairer(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_LEADING$);

    // paints the syntax-off partitions
    dr = new DefaultDamagerRepairer(new SyntaxOffKeywordScanner(getColorManager(),
        getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.SYNTAXOF_SEGMENT);
    reconciler.setRepairer(dr, IPreferenceConstants.SYNTAXOF_SEGMENT);

    dr = new DefaultDamagerRepairer(new SyntaxOffEolKeywordScanner(getColorManager(),
        getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.SYNTAXOFF_EOL);
    reconciler.setRepairer(dr, IPreferenceConstants.SYNTAXOFF_EOL);

    return reconciler;

  }

  /*
   * public IReconciler getReconciler(ISourceViewer sourceViewer){ RecipeReconcilingStrategy
   * strategy = new RecipeReconcilingStrategy(); MonoReconciler reconciler = new
   * MonoReconciler(strategy, false); reconciler.setDelay(500); return reconciler; }
   */

  /**
   * this method instantiates and returns the ColorManger to be used
   * 
   * @return the used color manager
   */
  public ColorManager getColorManager()
  {
    if (colorManager == null) colorManager = new ColorManager();
    return colorManager;
  }

  /**
   * this method instantiates and returns the preference store to be used
   * 
   * @return the preference store to be used
   */
  public IPreferenceStore getPreferenceStore()
  {
    if (preferenceStore == null) preferenceStore = DLabProPlugin.getDefault().getPreferenceStore();
    return preferenceStore;
  }

  /**
   * This class listens for preference store property change events
   * 
   * @author Christian Feig
   * 
   */
  private class PreferenceListener implements IPropertyChangeListener,
      Preferences.IPropertyChangeListener
  {
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event)
    {
      adaptToPreferenceChange(event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Preferences$IPropertyChangeListener#propertyChange(org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
     */
    public void propertyChange(Preferences.PropertyChangeEvent event)
    {
      adaptToPreferenceChange(new PropertyChangeEvent(event.getSource(), event.getProperty(), event
          .getOldValue(), event.getNewValue()));
    }

    /**
     * is called to adapt preference changes
     * 
     * @param event -
     *          the property change event
     */
    protected void adaptToPreferenceChange(PropertyChangeEvent event)
    {
      getCodeScanner().adaptToPreferenceChange(event);

    }

  }
}
