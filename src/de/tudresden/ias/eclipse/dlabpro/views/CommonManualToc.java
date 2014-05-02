
package de.tudresden.ias.eclipse.dlabpro.views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;

import de.tudresden.ias.eclipse.dlabpro.DLabProPlugin;
import de.tudresden.ias.eclipse.dlabpro.views.toctree.TocNode;

public abstract class CommonManualToc

{

  private String docHome   = null;

  String         imagePath = null;

  /**
   * Constructor
   * 
   */
  public CommonManualToc()
  {
    super();

    String home = DLabProPlugin.getDefault().getPreferenceStore().getString(getPreferenceName());

    docHome = (home.equals("")) ? null : home;
  }

  /**
   * validates and corrects the homePath if needed
   * 
   * @return the valid home path
   */
  private String getValidHome()
  {
    if (docHome != null && !docHome.endsWith(File.separator)) docHome = docHome
        .concat(File.separator);

    return docHome;
  }

  public ReturnObject getInput(TocNode root)
  {
    return getInput(root, new File(getValidHome() + "toc.js"));
  }

  /**
   * creates the input object for the {@link TreeViewer}
   * 
   * @return the root Object (will be a {@link TocNode})
   */
  private ReturnObject getInput(TocNode root, File file)
  {
    Map map = new HashMap();
    ArrayList expandedElements = new ArrayList();

    if (root == null) root = new TocNode(null, null);

    if (getValidHome() != null && file != null)
    {

      if (file.exists())
      {
        try
        {
          FileReader reader = new FileReader(file);
          BufferedReader bufferedReader = new BufferedReader(reader);
          String line = bufferedReader.readLine();
          while ((line != null))
          {
            // validate that the line is not a commented line
            if (!line.startsWith("//"))
            {

              // parse

              Pattern pattern = Pattern.compile("SetImageList\\(\"{1}([a-zA-Z_/]+)\"{1}\\);");
              Matcher matcher = pattern.matcher(line);

              if (matcher.find() && (matcher.groupCount() > 0)) imagePath = matcher.group(1);

              String patternString = "InsertItem(";
              int start = line.indexOf(patternString);
              int end = line.lastIndexOf(");");
              if (start >= 0 && end > start)
              {
                start += patternString.length();
                String match = line.substring(start, end);
                String[] matchArray = match.split(",");
                boolean redo = true;
                while (redo)
                {
                  boolean doRedo = false;
                  for (int i = 0; i < matchArray.length; i++)
                  {
                    if (matchArray[i].startsWith("\"") && !matchArray[i].endsWith("\"")
                        && i + 1 <= matchArray.length)
                    {
                      matchArray[i] = matchArray[i].concat(",").concat(matchArray[i + 1]);
                      int dif = 0;
                      String[] tempArray = new String[matchArray.length - 1];
                      for (int j = 0; j < matchArray.length; j++)
                        if (j == i)
                        {
                          tempArray[j - dif] = matchArray[j].concat(",").concat(matchArray[j + 1]);
                          dif = 1;
                          j++;
                        }
                        else tempArray[j - dif] = matchArray[j];
                      matchArray = tempArray;
                      doRedo = true;
                      break;
                    }
                  }
                  redo = doRedo;
                }

                if (matchArray.length > 5)
                {
                  // if(matchArray.length < 8){
                  // is a toplevel node
                  int i2 = -1;
                  String nodeDefinitionName = null;

                  // get nodeDefinitionName if there exists one
                  while (start >= 0)
                  {
                    if (line.charAt(start) == '=')
                    {
                      start--;

                      while (line.charAt(start) == ' ')
                        start--;

                      i2 = start + 1;
                      start--;
                      while (!(line.charAt(start) == ' ') && start > 0)
                        start--;
                      nodeDefinitionName = line.substring(start, i2);
                      break;
                    }
                    start--;
                  }

                  // create node
                  TocNode node = null;
                  if (nodeDefinitionName != null) node = (TocNode)map.get(nodeDefinitionName);

                  if (node == null)
                  {
                    node = new TocNode(null, matchArray[0].substring(
                        matchArray[0].indexOf("\"") + 1, matchArray[0].lastIndexOf("\"")));
                    if (nodeDefinitionName != null) map.put(nodeDefinitionName, node);
                  }

                  node.setFIconCol(getValidHome() + imagePath + matchArray[2] + ".gif");
                  node.setFIconExp(getValidHome() + imagePath + matchArray[3] + ".gif");
                  node.setFToolTip(matchArray[1]);
                  node.setFDefault(matchArray[4]);
                  if (node.getFDefault().equals("1") || node.getFDefault().equals("JTIS_EXPANDED")) expandedElements
                      .add(node);
                  if (matchArray[5].equals("null")) node.setFLink(matchArray[5]);
                  else node.setFLink(getValidHome()
                      + matchArray[5].substring(matchArray[5].indexOf("\"") + 1, matchArray[5]
                          .lastIndexOf("\"")));

                  // check if parent node was defined
                  if (matchArray.length > 7)
                  {
                    // parent node defined so get it and add node to parent
                    TocNode parent = (TocNode)map.get(matchArray[7]);
                    if (parent == null)
                    {
                      parent = new TocNode(null, null);
                      map.put(matchArray[7], parent);
                    }
                    parent.addChild(node);
                  }

                  if (node.getFLink().toLowerCase().endsWith("home.html")) root.addChild(node);

                }
              }
            }
            line = bufferedReader.readLine();

          }
          bufferedReader.close();
        }
        catch (FileNotFoundException e)
        {
          e.printStackTrace();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

      }
      else root.addChild(new TocNode(root, "file does not exists"));
    }
    else root.addChild(new TocNode(root, getNoHomepathText()));
    ReturnObject object = new ReturnObject();
    object.setRoot(root);
    object.setExpandedElements(expandedElements);
    return object;
  }

  public void propertyChange(PropertyChangeEvent event)
  {
    if (!event.getNewValue().equals(event.getOldValue())) docHome = (String)((event.getNewValue()
        .equals("")) ? null : event.getNewValue());

  }

  /**
   * 
   * @return the identifier of the preference field
   */
  abstract protected String getPreferenceName();

  /**
   * 
   * @return the text to display when homepath not specified
   */
  abstract protected String getNoHomepathText();

  public class ReturnObject
  {
    TocNode root;

    List    expandedElements;

    public List getExpandedElements()
    {
      return expandedElements;
    }

    public void setExpandedElements(List expandedElements)
    {
      this.expandedElements = expandedElements;
    }

    public TocNode getRoot()
    {
      return root;
    }

    public void setRoot(TocNode root)
    {
      this.root = root;
    }

  }

}
