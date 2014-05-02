
package de.tudresden.ias.eclipse.dlabpro.editors.itp.model;

import java.util.Vector;
import java.util.regex.Matcher;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonModel;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonParser;

public class ItpParser extends CommonParser
{

  private IDocument fDocument;
  private ItpElement iIncludeNode;
  private ItpElement iFunctionNode;
  

  /**
   * Next line to read.
   */
  private int       fLine;

  private int       fLineCount;

  /*
   * (non-Javadoc)
   * 
   * @see de.tudresden.ias.eclipse.dlabpro.editors.CommonParser#parse(org.eclipse.jface.text.IDocument)
   */
  public CommonModel parse(IDocument document)
  {
    try
    {
      fDocument = document;
      fLine = 0;
      fLineCount = fDocument.getNumberOfLines();
      return parseItp();
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * is called to start parsing
   * 
   * @return a model which contains the detected elements
   * @throws BadLocationException
   */
  private CommonModel parseItp() throws BadLocationException
  {
    CommonModel itpModel = new CommonModel();
    iIncludeNode  = new ItpElement(itpModel, "includes", -1, -1, ItpElement.TYPE_INCLUDES);
    iFunctionNode = new ItpElement(itpModel, "functions", -1, -1, ItpElement.TYPE_FUNCTION);
    itpModel.addChildren(iIncludeNode);
    itpModel.addChildren(iFunctionNode);
    parseForFunctions(itpModel);
    if (iIncludeNode.getChildren().length==0) itpModel.removeChildren(iIncludeNode);
    return itpModel;
  }

  int endBracketPosition   = -1;

  int startBracketPosition = -1;

  /**
   * is called to parse for function sections
   * 
   * @param root -
   *          the root element to add found elements to
   */
  private void parseForFunctions(CommonModel root)
  {
    // ItpElement rootElement = new ItpElement(root, "name", -1, -1,
    // ItpElement.TYPE_ROOT);

    while (fLine < fLineCount)
    {
      bracketCounter = 0;
      parseForJavaDoc(root);
      // scanning for ListItems like FILE: INCLUDE: PINCLUDE: and DEFINE:
      // MWX: checkForListItems(root);
      parseForInclude(root);
      parseForFunctionsRecursively(iFunctionNode);
      fLine++;
    }
    // root.setOutlineParentElement(rootElement);

  }

  int bracketCounter = 0;

  /**
   * is called to parse for javadoc sections
   * 
   * @param root
   *          the root element to add found elements to
   */
  private void parseForJavaDoc(CommonModel root)
  {

    int start = -1;
    int length = -1;
    try
    {
      IRegion region = fDocument.getLineInformation(fLine);
      String text = fDocument.get(region.getOffset(), region.getLength());
      start = region.getOffset();
      if (textJavaDoc(text)) while (fLine < fLineCount)
      {
        length = region.getOffset() + region.getLength() - start;
        region = fDocument.getLineInformation(fLine);
        text = fDocument.get(region.getOffset(), region.getLength());

        if (!textJavaDoc(text))
        {
          if (textContainsFunction(text))
          {
            root.addIgnoreOutlinePageElement(new ItpElement(root, "javadoc", start, length,
                ItpElement.TYPE_JAVADOC));
          }
          break;
        }
        fLine++;

      }
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * parses recursively for functions
   * 
   * @param root -
   *          the element to add found elements to
   */
  private void parseForFunctionsRecursively(ItpElement root)
  {
    if (fLine<0 || fLine>=fLineCount) return;
    int start = -1;
    String name = "";
    try
    {
      IRegion region = fDocument.getLineInformation(fLine);
      String text = fDocument.get(region.getOffset(), region.getLength());
      Matcher matcher = getMatcherFor(text, "function\\s+.+\\({1}.*\\){1}");
      if (matcher.find() && !textIsComment(text))
      {
        name = getFunctionName(text);
        int end = matcher.end();
        matcher = getMatcherFor(text, "\\{");
        if (matcher.find(end))
        {
          start = region.getOffset();
        }
        else
        {
          fLine++;
          region = fDocument.getLineInformation(fLine);
          text = fDocument.get(region.getOffset(), region.getLength());
          if (stringContains(text, "\\{")) start = fDocument.getLineInformation(fLine - 1)
              .getOffset();
        }

      }
      if (start > -1)
      {
        int counter = bracketCounter;
        ItpElement itpElement = new ItpElement(root, name, start, -1, ItpElement.TYPE_FUNCTION);

        while (fLine < fLineCount)
        {
          region = fDocument.getLineInformation(fLine);
          text = fDocument.get(region.getOffset(), region.getLength());
          matcher = getMatcherFor(text, "\\{");

          int startMatch = 0;
          while (matcher.find(startMatch))
          {
            startMatch = matcher.end();
            bracketCounter++;
          }

          matcher = getMatcherFor(text, "\\}");
          startMatch = 0;
          while (matcher.find(startMatch))
          {
            startMatch = matcher.end();
            bracketCounter--;
          }

          if (bracketCounter <= counter)
          {
            itpElement.setLength(region.getOffset() + region.getLength() - start);
            root.addChildElement(itpElement);
            fLine++;
            parseForFunctionsRecursively(root);
            break;
          }
          fLine++;
          parseForFunctionsRecursively(itpElement);
        }
      }

    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }

  }

  /**
   * Parses the current line of the document for occurrences of the "include" method and adds a node
   * to the outliner for each include.
   * 
   * @param root
   *          The outliner parent node of includes
   */
  private void parseForInclude(CommonModel root)
  {
    IRegion region = null;
    Vector<String> vsTok = null;
    int nTok = 0;
    try
    {
      region = fDocument.getLineInformation(fLine);
      vsTok = dlpTokenizeLine(fDocument.get(region.getOffset(), region.getLength()));
      for (nTok = 0; nTok < vsTok.size(); nTok++)
        if (nTok > 0)
        {
          String sTok = (String)vsTok.elementAt(nTok);
          if (sTok.equals("include"))
          {
            iIncludeNode.addChildElement(new ItpElement(iIncludeNode, (String)vsTok.elementAt(0), region
                .getOffset(), region.getLength(), ItpElement.TYPE_INCLUDES));
          }
        }
    }
    catch (BadLocationException e1)
    {
      e1.printStackTrace();
    }
  }

  /**
   * Tokenizes one line of dLabPro (.?tp) source code.
   * 
   * @param sText
   *          The line of source code
   * @return a vector of token strings
   */
  private Vector<String> dlpTokenizeLine(String sText)
  {
    Vector<String> vsTok = new Vector<String>();
    int i = 0;
    boolean bEscape = false;
    boolean bInStr = false;

    sText = sText.trim();
    for (i = 0; i < sText.length(); i++)
    {
      if (bEscape)
      {
        bEscape = false;
        continue;
      }
      char tx = sText.charAt(i);
      if (tx == '\\')
      {
        bEscape = true;
        continue;
      }
      if (tx == '\"')
      {
        bInStr = !bInStr;
        continue;
      }
      if (!bInStr && (tx == '#' || tx == ' ' || tx == '\t' || tx == ';'))
      {
        vsTok.add(sText.substring(0, i));
        if (tx == ';') vsTok.add(";");
        sText = sText.substring(i + 1).trim();
        i = 0;
        if (tx == '#') break;
      }
      if (!bInStr && i == sText.length() - 1) vsTok.add(sText.substring(0));
    }
    return vsTok;
  }

  protected SegmentObject splitText(String text)
  {
    SegmentObject segment = null;
    // String[] split = text.split(";");
    Matcher matcher = getMatcherFor(text, "\".*\"");
    if (matcher.find()) segment = new SegmentObject("include", matcher.group().replaceAll("\"", ""));

    return segment;
  }

  /**
   * checks wether or not the given text is of type javadoc (startin with ##)
   * 
   * @param text -
   *          the text to check
   * @return true if the text represents a javadoc,<br>
   *         false else
   */
  private boolean textJavaDoc(String text)
  {
    return stringContains(text, "#{2}");
  }

  /**
   * check wether or not the given text contains a function or not
   * 
   * @param text -
   *          the text to check for
   * @return true if the text contains a function,<br>
   *         false else
   */
  private boolean textContainsFunction(String text)
  {
    return stringContains(text, "function\\s+.+\\({1}.*\\){1}");
  }

  /**
   * this method gets the name of a function embedded in the given text
   * 
   * @param text -
   *          the text which contains the function
   * @return the name of the function
   */
  private String getFunctionName(String text)
  {
    int start = text.toLowerCase().lastIndexOf("function") + 9;
    int end = text.indexOf(")") + 1;
    String string = text.substring(start, end);
    if (string != "") while (string.startsWith(" ") || string.charAt(0) == '\t')
      string = string.substring(1, string.length());

    return string;
  }

}
