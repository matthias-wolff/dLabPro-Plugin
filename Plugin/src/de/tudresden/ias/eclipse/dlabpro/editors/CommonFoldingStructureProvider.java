
package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.swt.widgets.Display;

/**
 * this class provides common functionality to handle code folding.<br>
 * elements founded and added to the CommonModel by the parser will be handled.
 * 
 * @author Christian Feig
 * 
 */
public abstract class CommonFoldingStructureProvider
{
  private AbstractEditor   fEditor;

  private IDocument        fDocument;

  private IProgressMonitor fProgressMonitor;

  /**
   * Constuctor
   * 
   * @param editor -
   *          the editor to compute the folding structure for
   */
  public CommonFoldingStructureProvider(AbstractEditor editor)
  {
    fEditor = editor;
  }

  /**
   * 
   * @param progressMonitor
   */
  public void setProgressMonitor(IProgressMonitor progressMonitor)
  {
    fProgressMonitor = progressMonitor;
  }

  /**
   * 
   * @param document
   */
  public void setDocument(IDocument document)
  {
    fDocument = document;
  }

  protected abstract boolean isInitiallyFolded(CommonElement iElement);
  
  private void addFoldingRegions(Set<Position> regions, CommonElement[] elements, Map<Annotation,Position> map) throws BadLocationException
  {
    for (int i = 0; i < elements.length; i++)
    {
      CommonElement element = elements[i];
      try
      {
        int startLine = fDocument.getLineOfOffset(element.getOffset());
        int endLine = fDocument.getLineOfOffset(element.getOffset() + element.getLength());
        if (startLine >= 0 && startLine < endLine)
        {
          int start = fDocument.getLineOffset(startLine);
          int end = fDocument.getLineOffset(endLine) + fDocument.getLineLength(endLine);
          Position position = new Position(start, end - start);
          regions.add(position);
          if (isInitiallyFolded(element))
            map.put(new ProjectionAnnotation(true), position);
        }
      }
      catch (BadLocationException x)
      {
      }
      CommonElement[] children = element.getChildren();
      if (children != null) addFoldingRegions(regions, children, map);
    }
  }

  /**
   * Adds all children of CommonModel as folding regions and folds these regions
   * if <code>bDoFold</code> is <code>true</code>.
   * 
   * @param commonModel
   *          the document model
   * @param bDoFolding
   *          resets the folding         
   */
  public void updateFoldingRegions(CommonModel commonModel, boolean bDoFold)
  {
    try
    {
      ProjectionAnnotationModel model = (ProjectionAnnotationModel)fEditor
          .getAdapter(ProjectionAnnotationModel.class);
      if (model == null) return;

      Set<Position> currentRegions = new HashSet<Position>();
      Map<Annotation,Position> map = new HashMap<Annotation, Position>();
      addFoldingRegions(currentRegions, commonModel.getAllChildren(), map);
      if (bDoFold)
      {
        model.removeAllAnnotations();
        model.replaceAnnotations(null, map);
      }
      updateFoldingRegions(model, currentRegions);
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
  }

  protected IRegion[] computeProjectionRanges(CommonElement element)
  {
    List<Region> regions = new ArrayList<Region>();

    regions.add(new Region(element.getOffset(), element.getLength()));

    if (regions.size() > 0)
    {
      IRegion[] result = new IRegion[regions.size()];
      regions.toArray(result);
      return result;
    }
    return null;
  }

  private void updateFoldingRegions(ProjectionAnnotationModel model, Set<Position> currentRegions)
  {
    Annotation[] deletions = computeDifferences(model, currentRegions);

    Map<ProjectionAnnotation,Position> additionsMap = new HashMap<ProjectionAnnotation,Position>();
    for (Iterator<Position> iter = currentRegions.iterator(); iter.hasNext();)
      additionsMap.put(new ProjectionAnnotation(), iter.next());

    if ((deletions.length != 0 || additionsMap.size() != 0)
        && (fProgressMonitor == null || !fProgressMonitor.isCanceled())) model.modifyAnnotations(
        deletions, additionsMap, new Annotation[] {});
  }

  private Annotation[] computeDifferences(ProjectionAnnotationModel model, Set<Position> current)
  {
    List<Object> deletions = new ArrayList<Object>();
    for (Iterator<?> iter = model.getAnnotationIterator(); iter.hasNext();)
    {
      Object annotation = iter.next();
      if (annotation instanceof ProjectionAnnotation)
      {
        Position position = model.getPosition((Annotation)annotation);
        if (current.contains(position)) current.remove(position);
        else deletions.add(annotation);
      }
    }
    return (Annotation[])deletions.toArray(new Annotation[deletions.size()]);
  }
}
