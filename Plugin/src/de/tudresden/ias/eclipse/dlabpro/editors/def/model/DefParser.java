
package de.tudresden.ias.eclipse.dlabpro.editors.def.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import de.tudresden.ias.eclipse.dlabpro.editors.CommonElement;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonModel;
import de.tudresden.ias.eclipse.dlabpro.editors.CommonParser;
import de.tudresden.ias.eclipse.dlabpro.editors.IKeywordConstants;

public class DefParser extends CommonParser implements IKeywordConstants
{
  private IDocument fDocument;
  /**
   * Next line to read.
   */
  private int       fLine;
  private int       fLineCount;

  SegmentGroup      errors;
  SegmentGroup      notes;
  SegmentGroup      fields;
  SegmentGroup      options;
  SegmentGroup      methods;

  SegmentGroup      files;
  SegmentGroup      defines;
  // SegmentGroup reseIncludes;
  SegmentGroup      interfaceCodeSnippets;

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
      return parseDef();
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
  private CommonModel parseDef() throws BadLocationException
  {

    CommonModel defModel = new CommonModel();
    while (fLine < fLineCount)
    {
      IRegion region = fDocument.getLineInformation(fLine);
      String text = fDocument.get(region.getOffset(), region.getLength());
      fLine++;
      if (defModel.getOutlineParentElement() == null)
      {
        int id = -1;
        if (text.startsWith("CLASS:")) id = ClassSection.CLASS;
        if (text.startsWith("/cLib CLASS:")) id = ClassSection.cLib_CLASS;
        if (text.startsWith("/cLib PROJECT:")) id = ClassSection.cLib_CLASS;
        if (id >= 0)
        {
          String name = getClassSectionName(text);
          ClassSection classSection = new ClassSection(defModel, name, region.getOffset(), region
              .getLength(), id);
          CommonElement[] segments = parseForSegments(defModel, classSection);
          classSection.setSegments(segments);
          defModel.setOutlineParentElement(classSection);
          return defModel;
        }

      }
    }
    if (defModel.getOutlineParentElement() == null)
    {
      ClassSection classSection = new ClassSection(defModel, "", -1, -1,
          ClassSection.NO_CLASS_SECTION);
      CommonElement[] segments = parseForSegments(defModel, classSection);
      classSection.setSegments(segments);
      defModel.setOutlineParentElement(classSection);

    }
    return defModel;
  }

  /**
   * is called to parse for segments
   * 
   * @param defModel -
   *          the model to add found elements to
   * @param root -
   *          the root element to add elements to
   * @return
   */
  private CommonElement[] parseForSegments(CommonModel defModel, CommonElement root)
  {
    List segments = new ArrayList();
    errors = new SegmentGroup(root, "Errors and Warnings", -1, 1, SegmentGroup.TYPE_ERROR);
    notes = new SegmentGroup(root, "Notes", -1, 1, SegmentGroup.TYPE_NOTE);
    fields = new SegmentGroup(root, "Fields", -1, 1, SegmentGroup.TYPE_FIELD);
    options = new SegmentGroup(root, "Options", -1, 1, SegmentGroup.TYPE_OPTION);
    methods = new SegmentGroup(root, "Methods", -1, 1, SegmentGroup.TYPE_METHOD);

    files = new SegmentGroup(root, "C/C++ Source Files", -1, 1, SegmentGroup.TYPE_FILE);
    defines = new SegmentGroup(root, "Defines", -1, 1, SegmentGroup.TYPE_DEFINE);
    // reseIncludes = new SegmentGroup(root, "C/C++ Source Files:", -1, 1,
    // SegmentGroup.TYPE_RESEINCLUDES);
    interfaceCodeSnippets = new SegmentGroup(root, "Interface Code Snippets", -1, -1,
        SegmentGroup.TYPE_INTERFACE_CODE_SNIPPETS);

    fLine = 0;

    while (fLine < fLineCount)
    {
      boolean redo = false;
      try
      {
        IRegion region = fDocument.getLineInformation(fLine);
        String text = fDocument.get(region.getOffset(), region.getLength());
        // scanning for segments ending with END_CODE
        checkForInterfaceCodeSnippets(text, region, defModel);
        // scann for MAN: or CODE: segments
        checkForManAndCodeSegments(text, region, defModel);
        // scanning dor segments identified by fields like METHODS: etc.
        redo = checkForFieldSegments(text, region, defModel, segments);
        // scanning for ListItems like FILE: INCLUDE: PINCLUDE: and DEFINE:
        checkForListItems(defModel, text, region);
        if (!redo) fLine++;
      }
      catch (BadLocationException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    if (errors.getChildren().length > 0)
    {
      // Collections.sort(errors.getChildren());
      segments.add(0, errors);
    }
    if (notes.getChildren().length > 0)
    {
      segments.add(0, notes);
    }
    if (fields.getChildren().length > 0)
    {
      segments.add(fields);
    }
    if (options.getChildren().length > 0)
    {
      segments.add(options);
    }
    if (methods.getChildren().length > 0) segments.add(methods);
    if (defines.getChildren().length > 0) segments.add(defines);
    if (files.getChildren().length > 0) segments.add(files);
    // if (reseIncludes.getChildren().length > 0) segments.add(reseIncludes);
    if (interfaceCodeSnippets.getChildren().length > 0) segments.add(interfaceCodeSnippets);
    return (CommonElement[])segments.toArray(new CommonElement[segments.size()]);
  }

  /**
   * is called to parse for code snippets
   * 
   * @param text -
   *          the text to parse
   * @param region -
   *          the current linke region
   * @param defModel -
   *          the model to add elements to
   */
  private void checkForInterfaceCodeSnippets(String text, IRegion region, CommonModel defModel)
  {
    boolean breakWhile = false;
    try
    {
      SegmentObject segment = textContainsSegmentStartKeyWord(text);
      if (segment != null)
      {
        int offset = region.getOffset();
        int length = -1;
        while (!textContainsSegmentEndKeyWord(text) && !breakWhile)
        {
          // if(textContainslistKeyWord(text) != null |
          // textContainsSegmentKeyWord(text) != null){
          // breakWhile = true;
          // fLine--;
          // break;
          // }
          fLine++;
          if (fLine < fLineCount)
          {
            region = fDocument.getLineInformation(fLine);
            text = fDocument.get(region.getOffset(), region.getLength());
          }
          else break;
          if (text.trim().length() > 0 && !text.startsWith("#")) length = region.getOffset()
              + region.getLength() - offset;

          int i = checkForManAndCodeSegments(text, region, defModel);
          length = (i > 0) ? i + length : length;

          if (textContainsSegmentStartKeyWord(text) != null)
          {
            breakWhile = true;
            checkForInterfaceCodeSnippets(text, region, defModel);
            break;
          }

        }
        if (!breakWhile) interfaceCodeSnippets.addSegment(new SnippetSegment(defModel, segment
            .getType(), offset, length));
      }
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * is called to parse the given text for manual and code snippets
   * 
   * @param text -
   *          the text to parse
   * @param region -
   *          the current line region
   * @param defModel -
   *          the model to add found elements to
   * @return length of found segment or -1 if none found
   */
  private int checkForManAndCodeSegments(String text, IRegion region, CommonModel defModel)
  {
    // boolean breakWhile = false;
    try
    {
      SegmentObject segment = textContainsManOrCodeKeyWord(text);
      if (segment != null)
      {
        int offset = region.getOffset();
        int length = -1;
        while (!textContainsSegmentEndKeyWord(text) && fLine < fLineCount /*
                                                                           * && !breakWhile
                                                                           */)
        {
          // if(textContainslistKeyWord(text) != null |
          // textContainsSegmentKeyWord(text) != null){
          // breakWhile = true;
          // fLine--;
          // break;
          // }
          fLine++;
          region = fDocument.getLineInformation(fLine);
          text = fDocument.get(region.getOffset(), region.getLength());
          if (text.trim().length() > 0 && !text.startsWith("#")) length = region.getOffset()
              + region.getLength() - offset;
          // if(textContainsManOrCodeKeyWord(text) != null){
          // breakWhile = true;
          // checkForManAndCodeAppearence(text, region, defModel);
          // break;
          // }
          // if(textContainsSegmentStartKeyWord(text) != null){
          // breakWhile = true;
          // checkForInterfaceCodeSnippets(text, region, defModel);
          // break;
          // }

        }
        // if(!breakWhile)
        defModel.addIgnoreOutlinePageElement(new Segment(defModel, segment.getType(), offset,
            length));

        return length;
      }
    }
    catch (BadLocationException e)
    {
      e.printStackTrace();
    }
    return -1;
  }

  /**
   * is called to parse for field segments
   * 
   * @param text -
   *          the text to parse
   * @param region -
   *          the current line region
   * @param defModel -
   *          the model to add found elements to
   * @param segments -
   *          the segments to add found elements to
   * @return true if field segments found,<br>
   *         false else
   */
  private boolean checkForFieldSegments(String text, IRegion region, CommonModel defModel,
      List segments)
  {

    try
    {
      SegmentObject segment = textContainsSegmentKeyWord(text);
      if (segment != null)
      {
        int offset = region.getOffset();
        int length = -1;
        if (text.trim().length() > 0) length = region.getOffset() + region.getLength() - offset;
        fLine++;
        region = fDocument.getLineInformation(fLine);
        text = fDocument.get(region.getOffset(), region.getLength());

        while (textContainsManOrCodeKeyWord(text) != null
            | ((textContainsSegmentKeyWord(text) == null) && (textContainsSegmentStartKeyWord(text) == null)))
        {
          int regionTemp = region.getLength();

          if (text.trim().length() > 0 && !text.startsWith("#")) length = region.getOffset()
              + region.getLength() - offset;

          int i = checkForManAndCodeSegments(text, region, defModel);
          length = (i > 0) ? i + length - regionTemp : length;

          fLine++;
          if (fLine < fLineCount)
          {
            region = fDocument.getLineInformation(fLine);
            text = fDocument.get(region.getOffset(), region.getLength());
          }
          else break;

        }

        if (segment.getType().toLowerCase().startsWith("method")) methods
            .addSegment(new MethodElement(defModel, segment.getName(), offset, length));
        else if (segment.getType().toLowerCase().startsWith("field"))
        {
          if (fDocument.get(offset, length).indexOf("/hidden") >= 0) segment
              .setId(FieldSegment.PRIVATE);
          else if (fDocument.get(offset, length).indexOf("/noset") >= 0) segment
              .setId(FieldSegment.PROTECTED);
          else segment.setId(FieldSegment.PUBLIC);
          fields.addSegment(new FieldSegment(defModel, segment.getName(), offset, length, segment
              .getId()));
        }
        else if (segment.getType().toLowerCase().startsWith("option")) options
            .addSegment(new OptionSegment(defModel, segment.getName(), offset, length));
        else if (segment.getType().toLowerCase().startsWith("error"))
        {
          if (fDocument.get(offset, length).indexOf("LEVEL:") >= 0) segment
              .setId(ErrorSegment.WARNING);
          else segment.setId(ErrorSegment.ERROR);
          errors.addSegment(new ErrorSegment(defModel, segment.getName(), offset, length, segment
              .getId()));
        }
        else if (segment.getType().toLowerCase().startsWith("note")) notes
            .addSegment(new NoteSegment(defModel, segment.getName(), offset, length));
        else segments.add(new Segment(defModel, segment.getName(), offset, length));
        return true;
      }
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    return false;

  }

  /**
   * is called to parse the given text for list items
   * 
   * @param defModel -
   *          the model to add elements to
   * @param text -
   *          the text to parse
   * @param region -
   *          the current line region
   */
  private void checkForListItems(CommonModel defModel, String text, IRegion region)
  {
    try
    {
      SegmentObject segment = textContainslistKeyWord(text);
      if (segment != null)
      {
        int offset = region.getOffset();
        int length = -1;
        SegmentObject tempSegment = segment;

        while ((tempSegment != null) && fLine < fLineCount)
        {
          int lengthTemp = -1;

          region = fDocument.getLineInformation(fLine);
          text = fDocument.get(region.getOffset(), region.getLength());
          if (text.trim().length() > 0 && !text.startsWith("#")) lengthTemp = text.trim().length();

          if (tempSegment.getType().toLowerCase().startsWith("define")) defines
              .addSegment(new ListSegment(defModel, tempSegment.getName(), region.getOffset(),
                  lengthTemp, ListSegment.DEFINE));
          else if (tempSegment.getType().toLowerCase().startsWith("file")) files
              .addSegment(new ListSegment(defModel, tempSegment.getName(), region.getOffset(),
                  lengthTemp, ListSegment.FILE));
          else if (tempSegment.getType().toLowerCase().endsWith("include")) files
              .addSegment(new ListSegment(defModel, tempSegment.getName(), region.getOffset(),
                  lengthTemp, ListSegment.INCLUDE));

          fLine++;
          region = fDocument.getLineInformation(fLine);
          text = fDocument.get(region.getOffset(), region.getLength());
          if (text.trim().length() > 0 && !text.startsWith("#")) length = region.getOffset()
              + region.getLength() - offset;

          tempSegment = textContainslistKeyWord(text);
        }

        defModel
            .addIgnoreOutlinePageElement(new de.tudresden.ias.eclipse.dlabpro.editors.def.model.ListItem(
                defModel, segment.getType(), offset, length));
      }
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * this method checks wether the given text contains a list key word (identified by
   * {@link IKeywordConstants#LIST_KEYWORDS}) or not. if a list key word was detected, a
   * {@link SegmentObject} will be returned, <code>null</code> else.
   * 
   * @param text -
   *          the text to scan
   * @return a SegmentObject if a list key word was detected, <code>null</code> else
   */
  protected SegmentObject textContainslistKeyWord(String text)
  {
    if (!textIsComment(text)) for (int i = 0; i < IKeywordConstants.LIST_KEYWORDS.length; i++)
      if (stringContains(text, IKeywordConstants.LIST_KEYWORDS[i])) return splitText(text);

    return null;
  }

  /**
   * this method parses the given text and returns a SegmentObject which holds the type and the name
   * of the detected segment
   * 
   * @param text -
   *          the text to parse
   * @return a SegmentObject
   */
  protected SegmentObject splitText(String text)
  {
    SegmentObject segment = null;
    String[] split = text.split(":");
    if (split.length > 0) while (split[0].startsWith(" "))
      split[0] = split[0].replaceFirst(" ", "");

    if (split.length >= 2)
    {
      String description = split[1];
      for (int i = 2; i < split.length; i++)
        description = description.concat(":".concat(split[i]));
      while (description.startsWith(" "))
        description = description.replaceFirst(" ", "");
      description = description.split(" ")[0];
      description = description.replaceAll("\"", "");
      segment = new SegmentObject(split[0], description);
    }
    else if (split.length >= 1) segment = new SegmentObject(split[0], split[0]);

    return segment;
  }

  /**
   * this method checks wether the given text contains a manual or code keyword (MAN:, CODE: or
   * /primary CODE:) or not. if a segment keyword was detected, a {@link SegmentObject} will be
   * returned, <code>null</code> else.
   * 
   * @param text -
   *          the text to scan
   * @return a SegmentObject if a list key word was detected, <code>null</code> else
   */
  private SegmentObject textContainsManOrCodeKeyWord(String text)
  {
    if (!textIsComment(text))
    // for (int i = 0; i < SYNTAX_OFF_START_KEYWORDS.length; i++)
    if (stringContains(text, "MAN:"))
    {
      if (!textIsInComment(text, "MAN:")) return splitText(text);
    }
    if (text.equals("CODE:"))
    {
      if (!textIsInComment(text, "CODE:")) return splitText(text);
    }
    if (text.equals("/primary CODE:"))
    {
      if (!textIsInComment(text, "/primary CODE:")) return splitText(text);
    }
    return null;
  }

  /**
   * this method checks wether the given text contains a segment start key word (identified by
   * {@link IKeywordConstants#SEGMENT_START_KEYWORDS}) or not. if a segment start key word was
   * detected, a {@link SegmentObject} will be returned, <code>null</code> else.
   * 
   * @param text -
   *          the text to scan
   * @return a SegmentObject if a list key word was detected, <code>null</code> else
   */
  private SegmentObject textContainsSegmentStartKeyWord(String text)
  {
    if (!textIsComment(text)) for (int i = 0; i < SEGMENT_START_KEYWORDS.length; i++)
    {
      if (textStartsWith(text, SEGMENT_START_KEYWORDS[i])) if (!textIsInComment(text,
          SEGMENT_START_KEYWORDS[i])) return splitText(text);
    }
    return null;
  }

  /**
   * this method checks wether the given text contains a segment end key word (identified by
   * {@link IKeywordConstants#SEGMENT_END_KEYWORDS}) or not. if a segment end key word was
   * detected, <code>true</code> will be returned, <code>false</code> else.
   * 
   * @param text -
   *          the text to scan
   * @return true if a segment end key word was detected, <code>false</code> else
   */
  private boolean textContainsSegmentEndKeyWord(String text)
  {
    if (!textIsComment(text)) for (int i = 0; i < SEGMENT_END_KEYWORDS.length; i++)
      if (stringContains(text, SEGMENT_END_KEYWORDS[i])) return true;
    return false;
  }

  /**
   * this method checks wether the given text contains a segment key word (identified by
   * {@link IKeywordConstants#SINGLE_SEGMENT_KEYWORDS}) or not. if a segment key word was detected,
   * a {@link SegmentObject} will be returned, <code>null</code> else.
   * 
   * @param text -
   *          the text to scan
   * @return a SegmentObject if a list key word was detected, <code>null</code> else
   */
  private SegmentObject textContainsSegmentKeyWord(String text)
  {
    if (!textIsComment(text)) for (int i = 0; i < SINGLE_SEGMENT_KEYWORDS.length; i++)
      if (textStartsWith(text, SINGLE_SEGMENT_KEYWORDS[i])) return splitText(text);

    return null;
  }

  /**
   * this method parses the given text to get the class name
   * 
   * @param text -
   *          the text to get the name for
   * @return - the name of the class
   */
  private String getClassSectionName(String text)
  {
    // TODO nach name parsen
    return text;
  }

}
