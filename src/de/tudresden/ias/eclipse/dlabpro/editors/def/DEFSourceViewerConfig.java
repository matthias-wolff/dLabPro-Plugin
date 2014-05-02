/*
 * Created on 08.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors.def;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonSourceViewerConfiguration;
import de.tudresden.ias.eclipse.dlabpro.editors.IKeywordConstants;
import de.tudresden.ias.eclipse.dlabpro.editors.util.ColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * this class inherits the class <code>CommonCodeViewerConfiguration</code> and defines a def
 * specific SourceViewerConfiguration
 * 
 * @author Christian Feig
 * 
 */
public class DEFSourceViewerConfig extends CommonSourceViewerConfiguration implements
    IPreferenceConstants, IKeywordConstants
{

  private AbstractScanner scanner = null;
  DEFEditor               fEditor;

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonSourceViewerConfiguration#getCodeScanner(de.tudresden.ias.eclipse.dlabpro.editors.util.ColorManager,
   *      org.eclipse.jface.preference.IPreferenceStore)
   */
  protected AbstractScanner getCodeScanner(ColorManager colorManager, IPreferenceStore store)
  {
    if (scanner == null) scanner = new DefCodeScanner(colorManager, store);
    return scanner;
  }

  /**
   * Constructor
   * 
   * @param fEditor
   * @param store
   */
  public DEFSourceViewerConfig(DEFEditor fEditor, IPreferenceStore store)
  {
    super(store/* , CommonSourceViewerConfiguration.CODESCANNER_TYPE_DEF */);
    this.fEditor = fEditor;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
   */
  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
  {

    PresentationReconciler reconciler = (PresentationReconciler)super
        .getPresentationReconciler(sourceViewer);

    // DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new
    // SyntaxOffKeywordScanner(getColorManager(), getPreferenceStore()));
    // reconciler.setDamager(dr, CommonDocumentPartitionScanner.KEYWORD);
    // reconciler.setRepairer(dr, CommonDocumentPartitionScanner.KEYWORD);
    // DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new
    // PreprocessorWithLeadingDollarScanner(getColorManager(),
    // getPreferenceStore()));
    // reconciler.setDamager(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_LEADING$);
    // reconciler.setRepairer(dr,
    // CommonDocumentPartitionScanner.PREPROCESSOR_WITH_LEADING$);
    // dr = new DefaultDamagerRepairer(new
    // SyntaxOffKeywordScanner(getColorManager(), getPreferenceStore()));
    // reconciler.setDamager(dr, KEYWORD);
    // reconciler.setRepairer(dr, KEYWORD);

    return reconciler;

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
   */
  public IReconciler getReconciler(ISourceViewer sourceViewer)
  {
    DefReconcilingStrategy strategy = new DefReconcilingStrategy(fEditor);
    MonoReconciler reconciler = new MonoReconciler(strategy, false);
    reconciler.setProgressMonitor(new NullProgressMonitor());
    reconciler.setDelay(500);

    return reconciler;
  }

}
