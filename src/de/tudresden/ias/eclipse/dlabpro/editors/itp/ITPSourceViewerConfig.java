/*
 * Created on 08.03.2005
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */

package de.tudresden.ias.eclipse.dlabpro.editors.itp;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;

import de.tudresden.ias.eclipse.dlabpro.editors.AbstractScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonCodeScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonReconcilingStrategy;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonSourceViewerConfiguration;
import de.tudresden.ias.eclipse.dlabpro.editors.scanner.FormularScanner;
import de.tudresden.ias.eclipse.dlabpro.editors.util.ColorManager;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * @author Xian
 * 
 */
public class ITPSourceViewerConfig extends CommonSourceViewerConfiguration
{

  private AbstractScanner scanner = null;
  ITPEditor               fEditor;

  protected AbstractScanner getCodeScanner(ColorManager colorManager, IPreferenceStore store)
  {
    if (scanner == null) scanner = new CommonCodeScanner(colorManager, store);
    return scanner;
  }

  /**
   * @param store
   */
  public ITPSourceViewerConfig(ITPEditor fEditor, IPreferenceStore store)
  {
    super(store/* , CommonSourceViewerConfiguration.CODESCANNER_TYPE_ITP */);
    this.fEditor = fEditor;
  }

  /**
   * @param store
   * @param coreStore
   */
//  public ITPSourceViewerConfig(IPreferenceStore store, Preferences coreStore)
//  {
//    super(store, coreStore/*
//                           * , CommonSourceViewerConfiguration.CODESCANNER_TYPE_ITP
//                           */);
//  }

  public IReconciler getReconciler(ISourceViewer sourceViewer)
  {
    CommonReconcilingStrategy strategy = new ItpReconcilingStrategy(fEditor);
    MonoReconciler reconciler = new MonoReconciler(strategy, false);
    reconciler.setProgressMonitor(new NullProgressMonitor());
    reconciler.setDelay(500);

    return reconciler;
  }

  public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer)
  {
    PresentationReconciler reconciler = (PresentationReconciler)super
        .getPresentationReconciler(sourceViewer);
    DefaultDamagerRepairer dr = null;
    // paints the formel interpreter
    dr = new DefaultDamagerRepairer(new FormularScanner(getColorManager(), getPreferenceStore()));
    reconciler.setDamager(dr, IPreferenceConstants.P_CLR_FORMULA);
    reconciler.setRepairer(dr, IPreferenceConstants.P_CLR_FORMULA);
    return reconciler;
  }
}
