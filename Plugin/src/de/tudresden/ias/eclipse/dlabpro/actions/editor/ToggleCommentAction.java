/*
 * Created on 06.09.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.actions.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.utils.WorkbenchUtil;

/**
 * this class inherits abstract class <code>DlabProAction</code> and provides the functionality
 * for toggling comments
 * 
 * @author Xian
 * 
 */
public class ToggleCommentAction implements IEditorActionDelegate
{

  // evtl. mal mit erben von TextEditorAction versuchen
  // protected ToggleCommentAction(ResourceBundle bundle, String prefix,
  // ITextEditor editor) {
  // super(bundle, prefix, editor);
  // }

  public void setActiveEditor(IAction action, IEditorPart targetEditor)
  {
  }

  /**
   * @param action
   * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
   */
  public void run(IAction action)
  {

    IDocument doc = WorkbenchUtil.getEditorIDocument();

    IEditorPart ed = DLabProPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().getActiveEditor();

    if (ed instanceof AbstractDecoratedTextEditor)
    {
      ISelectionProvider selectionProvider = ((AbstractDecoratedTextEditor)ed)
          .getSelectionProvider();
      ISelection iSelection = selectionProvider.getSelection();

      if (iSelection instanceof TextSelection)
      {
        TextSelection textSelection = (TextSelection)iSelection;
        int startLine = textSelection.getStartLine();
        int endLine = textSelection.getEndLine();

        RegionObject[] regionObjects = getRegionsOfInterest(doc, startLine, endLine);

        if (checkRegionsForNotCommented(regionObjects))
        {
          for (int i = regionObjects.length - 1; i >= 0; i--)
          {
            RegionObject object = regionObjects[i];
            try
            {
              doc.replace(object.getRegion().getOffset(), object.getRegion().getLength(), "#"
                  + object.getText());
            }
            catch (BadLocationException e)
            {
              e.printStackTrace();
            }
          }
          selectionProvider.setSelection(new TextSelection(doc, textSelection.getOffset() + 1,
              textSelection.getLength() + endLine - startLine));
        }

        else
        {
          for (int i = regionObjects.length - 1; i >= 0; i--)
          {
            RegionObject object = regionObjects[i];
            try
            {
              doc.replace(object.getRegion().getOffset(), object.getRegion().getLength(), object
                  .getText().substring(1));
            }
            catch (BadLocationException e)
            {
              e.printStackTrace();
            }
          }
          selectionProvider.setSelection(new TextSelection(doc, textSelection.getOffset() - 1,
              textSelection.getLength() - endLine + startLine));
        }

      }
    }

  }

  /**
   * this method gets the content of the given range
   * 
   * @param doc -
   *          the document which shall be handled
   * @param startLine -
   *          the start line
   * @param endLine -
   *          the end line
   * @return an array of RegionObjects which contains the offset and the length of the each line
   *         segment
   */
  private RegionObject[] getRegionsOfInterest(IDocument doc, int startLine, int endLine)
  {
    List<RegionObject> list = new ArrayList<RegionObject>();
    IRegion region = null;
    for (int i = startLine; i <= endLine; i++)
    {

      try
      {
        region = doc.getLineInformation(i);

        if (region != null) list.add(new RegionObject(doc.get(region.getOffset(), region
            .getLength()), i, region));

      }
      catch (BadLocationException e)
      {
        e.printStackTrace();
      }
      region = null;
    }
    RegionObject[] regionsArray = new RegionObject[list.size()];
    for (int i = 0; i < list.size(); i++)
      if (list.get(i) instanceof RegionObject) regionsArray[i] = (RegionObject)list.get(i);
    return regionsArray;
  }

  /**
   * this method checks wether the given regionObjects contain already commented lines
   * 
   * @param regionObjects
   * @return <b>true</b> if less one line is already a comment<br>
   *         <b>false</b> if no line is a comment
   */
  private boolean checkRegionsForNotCommented(RegionObject[] regionObjects)
  {

    for (int i = 0; i < regionObjects.length; i++)
    {
      if (!regionObjects[i].getText().startsWith("#")) return true;
    }
    return false;
  }

  /**
   * this class stores line informations; for each line one RegionObject will be created
   * 
   * @author Xian
   * 
   */
  private class RegionObject
  {
    private String  text   = null;
    private int     line   = -1;
    private IRegion region = null;

    public RegionObject(String text, int line, IRegion region)
    {
      this.text = text;
      this.line = line;
      this.region = region;

    }

    /**
     * 
     * @return IRegion
     */
    public IRegion getRegion()
    {
      return region;
    }

    /**
     * 
     * @return the line number
     */
    @SuppressWarnings("unused")
    public int getLine()
    {
      return line;
    }

    /**
     * 
     * @return the line content
     */
    public String getText()
    {
      return text;
    }
  }

  public void selectionChanged(IAction action, ISelection selection)
  {
    // TODO Auto-generated method stub
    
  }

}
