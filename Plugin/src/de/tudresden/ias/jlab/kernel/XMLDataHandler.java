package de.tudresden.ias.jlab.kernel;

import java.util.AbstractCollection;
import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML data handler for parsing XML streams of dLabPro data files. 
 * 
 * @author Stephan Larws, Matthias Wolff
 */
class XMLDataHandler extends DefaultHandler
{
  // Tag names
  static final String TAG_INSTANCE       = "INSTANCE";
  static final String TAG_FIELD          = "FIELD";
  static final String TAG_TABLE          = "TABLE";
  static final String TAG_COMP           = "COMP";
  static final String TAG_REC            = "REC";
  static final String TAG_CELL           = "CELL";

  // Attributes of <INSTANCE>
  static final String I_ATTR_TYPE        = "type";
  static final String I_ATTR_CLASS       = "class";

  // Values of <INSTANCE class="...">
  static final String I_CLASS_VAL_DATA   = "data";

  // Attributes of <FIELD>
  static final String F_ATTR_NAME        = "name";
  static final String F_ATTR_TYPE        = "type";
  static final String F_ATTR_ARRLEN      = "arrlen";

  // Values of <FIELD name="...">
  static final String F_NAME_VAL_NREC    = "nrec";
  static final String F_NAME_VAL_RINC    = "rinc";
  static final String F_NAME_VAL_RWID    = "rwid";
  static final String F_NAME_VAL_ROFS    = "rofs";
  static final String F_NAME_VAL_CUNIT   = "cunit";
  static final String F_NAME_VAL_RUNIT   = "runit";
  static final String F_NAME_VAL_CINC    = "cinc";
  static final String F_NAME_VAL_COFS    = "cofs";
  static final String F_NAME_VAL_VINC    = "vinc";
  static final String F_NAME_VAL_VUNIT   = "vunit";
  static final String F_NAME_VAL_NBLOCK  = "nblock";
  static final String F_NAME_VAL_DESCR0  = "descr0";
  static final String F_NAME_VAL_DESCR1  = "descr1";
  static final String F_NAME_VAL_DESCR2  = "descr2";
  static final String F_NAME_VAL_DESCR3  = "descr3";
  static final String F_NAME_VAL_DESCR4  = "descr4";
  static final String F_NAME_VAL_RTEXT   = "rtext";
  static final String F_NAME_VAL_FTEXT   = "ftext";
  static final String F_NAME_VAL_VRTEXT  = "vrtext";
  static final String F_NAME_VAL_NOFFSET = "noffset";
  static final String F_NAME_VAL_DIM     = "dim";

  // Attributes of <COMP>
  static final String C_ATTR_NAME            = "name";
  static final String C_ATTR_TYPE            = "type";
  static final String C_ATTR_VUNIT           = "vunit"; // RESERVED!
  static final String C_ATTR_VMAX            = "zmax";  // RESERVED!
  static final String C_ATTR_VMIN            = "zmin";  // RESERVED!

  // Values of <COMP type="...">
  static final String C_TYPE_VAL_UCHAR       = "unsigned char";
  static final String C_TYPE_VAL_CHAR        = "char";
  static final String C_TYPE_VAL_SHORT       = "short";
  static final String C_TYPE_VAL_USHORT      = "unsigned short";
  static final String C_TYPE_VAL_INT         = "int";
  static final String C_TYPE_VAL_UINT        = "unsigned int";
  static final String C_TYPE_VAL_LONG        = "long";
  static final String C_TYPE_VAL_ULONG       = "unsigned long";
  static final String C_TYPE_VAL_FLOAT       = "float";
  static final String C_TYPE_VAL_DOUBLE      = "double";
  static final String C_TYPE_VAL_BOOL        = "bool";
  static final String C_TYPE_VAL_COMPLEX     = "complex";
  
  // Errors
  static final String WRN_T_BAD      = "Unknown tag <%s>, ignored";
  static final String WRN_T_NOTIN    = "Parent of <%s> must be <%s>";
  static final String WRN_I_BADTYPE  = "Instance type must be \"data\"";
  static final String WRN_F_NONAME   = "Missing \"name\" attribute in <FIELD>";
  static final String WRN_F_NOTYPE   = "Missing \"type\" attribute in <FIELD>";
  static final String WRN_F_BADTYPE  = "Invalid value \"%s\" for attribute <FIELD type>";
  static final String WRN_F_NOVAL    = "Missing value (cdata) of <FIELD name=\"%s\">";
  static final String WRN_F_BADVAL   = "Invalid value (cdata) \"%s\" of <FIELD name=\"%s\">";
  static final String WRN_C_AFTERREC = "<COMP> afer <REC> ignored";
  static final String WRN_C_NONAME   = "Missing \"name\" attribute in <COMP>, assuming none";
  static final String WRN_C_NOTYPE   = "Missing \"type\" attribute in <COMP>, assuming double";
  static final String WRN_C_BADTYPE  = "Invalid value \"%s\" for attribute <COMP type>";
  static final String WRN_L_TOOMANY  = "Too many cells in record #%d";
  static final String WRN_L_TOOFEW   = "Too few cells in record #%d";
  static final String WRN_L_BADVAL   = "Invalid value \"%s\" for cells #%d in record #%d";
  
  // Fields
  JlData                    idData;
  Stack<XMLElement>         stack;
  Vector<SAXParseException> lWarnings;
  Locator                   locator;
  int                       nR;
  int                       nC;

  /**
   * Creates a new dLabPro data XML handler.
   */
  public XMLDataHandler()
  {
    stack     = new Stack<XMLDataHandler.XMLElement>();
    lWarnings = new Vector<SAXParseException>();
    idData    = new JlData();
    nR        = -1;
    nC        = -1;
  }

  // Overrides

  @Override
  public void startDocument() throws SAXException
  {
  }

  @Override
  public void endDocument() throws SAXException
  {
  }
  
  @Override
  public void startElement
  (
    String     uri,
    String     localName,
    String     qName,
    Attributes attributes
  ) throws SAXException 
  {
    String name = localName;
    if ("".equals(name)) name = qName;
    
    XMLElement element = new XMLElement(name,attributes);
    stack.push(element);
    if (stackTopIsIgnored()) return;
    if      (element.isINSTANCE()) handleINSTANCEstart(element);
    else if (element.isFIELD()   ) handleFIELDstart(element);
    else if (element.isTABLE()   ) handleTABLEstart(element);
    else if (element.isCOMP()    ) handleCOMPstart(element);
    else if (element.isREC()     ) handleRECstart(element);
    else if (element.isCELL()    ) handleCELLstart(element);
    else
    {
      warning(String.format(WRN_T_BAD,name));
      element.ignored = true;
    } 
  }

  @Override
  public void endElement(String uri, String localName, String qName)
  throws SAXException
  {
    String name = localName;
    if ("".equals(name)) name = qName;
    XMLElement element = stack.peek();
    assert(element.name.equals(name));
    
    if (!stackTopIsIgnored())
      try
      {
        if      (element.isINSTANCE()) handleINSTANCEend(element);
        else if (element.isFIELD()   ) handleFIELDend(element);
        else if (element.isTABLE()   ) handleTABLEend(element);
        else if (element.isCOMP()    ) handleCOMPend(element);
        else if (element.isREC()     ) handleRECend(element);
        else if (element.isCELL()    ) handleCELLend(element);
      }
      finally
      {
        stack.pop();
      }
  }

  @Override
  public void characters(char[] ch, int start, int length)
  throws SAXException
  {
    if (stack.peek().cdata==null)
      stack.peek().cdata = "";
    stack.peek().cdata += new String(ch, start, length);
  }

  @Override
  public void setDocumentLocator(Locator locator)
  {
    this.locator = locator;
  }
  
  public void error(SAXParseException e) throws SAXException
  {
    System.out.println(e.getClass().getSimpleName()+", "+e.getMessage());
    lWarnings.add(e);
  }

  @Override
  public void warning(SAXParseException e) throws SAXException
  {
    System.out.println(e.getClass().getSimpleName()+", "+e.getMessage());
    lWarnings.add(e);
  }

  // Tag handlers
  
  private void handleINSTANCEstart(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    for (int i = 0; i < element.attr.getLength(); i++)
    {
      String s = element.attr.getLocalName(i);
      if (s.equals("")) s = element.attr.getQName(i);

      if (I_ATTR_CLASS.equals(s))
      {
        String v = element.attr.getValue(i);
        if (!I_CLASS_VAL_DATA.equals(v))
          warning(WRN_I_BADTYPE);
      }
    }
  }
  
  private void handleINSTANCEend(XMLElement element) throws SAXException
  {
    // No op
  }

  private void handleFIELDstart(XMLElement element) throws SAXException
  {
    // No op
  }
  
  private void handleFIELDend(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_INSTANCE))
    {
      warning(String.format(WRN_T_NOTIN,element.name,TAG_INSTANCE));
      return;
    }

    String name = null;
    String type = null;
    String val  = element.cdata;
    for (int i = 0; i < element.attr.getLength(); i++)
    {
      String s = element.attr.getLocalName(i);
      if ("".equals(s)) s = element.attr.getQName(i);
      if      (F_ATTR_NAME.equals(s)) name = element.attr.getValue(i);
      else if (F_ATTR_TYPE.equals(s)) type = element.attr.getValue(i);
    }

    if (name==null) { warning(WRN_F_NONAME); return; }
    if (type==null) { warning(WRN_F_NOTYPE); return; }
    if (val ==null)
    { 
      if (type.equals("text") || type.startsWith("char"))
        val = "";
      else
      {
        warning(String.format(WRN_F_NOVAL,name));
        return;
      }
    }
    
    try
    {
      if      (F_NAME_VAL_RINC   .equals(name)) idData.rinc    = Double.valueOf(val); 
      else if (F_NAME_VAL_RWID   .equals(name)) idData.rwid    = Double.valueOf(val);
      else if (F_NAME_VAL_ROFS   .equals(name)) idData.rofs    = Double.valueOf(val);
      else if (F_NAME_VAL_NOFFSET.equals(name)) idData.noffset = Double.valueOf(val);
      else if (F_NAME_VAL_RUNIT  .equals(name)) idData.runit   = val;
      else if (F_NAME_VAL_CINC   .equals(name)) idData.cinc    = Double.valueOf(val);
      else if (F_NAME_VAL_COFS   .equals(name)) idData.cofs    = Double.valueOf(val);
      else if (F_NAME_VAL_CUNIT  .equals(name)) idData.cunit   = val; 
      else if (F_NAME_VAL_VUNIT  .equals(name)) idData.vunit   = val; 
      else if (F_NAME_VAL_VINC   .equals(name)) idData.vinc    = Double.valueOf(val);
      else if (F_NAME_VAL_NBLOCK .equals(name)) idData.nblock  = Long.valueOf(val);
      else if (F_NAME_VAL_DESCR0 .equals(name)) idData.descr0  = Double.valueOf(val);
      else if (F_NAME_VAL_DESCR1 .equals(name)) idData.descr1  = Double.valueOf(val);
      else if (F_NAME_VAL_DESCR2 .equals(name)) idData.descr2  = Double.valueOf(val);
      else if (F_NAME_VAL_DESCR3 .equals(name)) idData.descr3  = Double.valueOf(val);
      else if (F_NAME_VAL_DESCR4 .equals(name)) idData.descr4  = Double.valueOf(val);
      else if (F_NAME_VAL_RTEXT  .equals(name)) idData.rtext   = val; 
      else if (F_NAME_VAL_FTEXT  .equals(name)) idData.ftext   = val; 
      else if (F_NAME_VAL_VRTEXT .equals(name)) idData.vrtext  = val;
    }
    catch (NumberFormatException e)
    {
      warning(String.format(WRN_F_BADVAL,val,name));
    }
  }

  private void handleTABLEstart(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_INSTANCE))
    {
      warning(String.format(WRN_T_NOTIN,element.name,TAG_INSTANCE));
      element.ignored = true;
    }
  }
  
  private void handleTABLEend(XMLElement element) throws SAXException
  {
    // No op
  }

  private void handleCOMPstart(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_TABLE))
    {
      warning(String.format(WRN_T_NOTIN,element.name,TAG_TABLE));
      return;
    }
    if (idData.getLength()>0)
    {
      warning(WRN_C_AFTERREC);
      return;
    }

    String name = null;
    String type  = null;
    for (int i = 0; i < element.attr.getLength(); i++)
    {
      String s = element.attr.getLocalName(i);
      if ("".equals(s)) s = element.attr.getQName(i);
      if      (F_ATTR_NAME.equals(s)) name = element.attr.getValue(i);
      else if (F_ATTR_TYPE.equals(s)) type = element.attr.getValue(i);
    }

    if (name==null) { warning(WRN_C_NONAME); name="";       }
    if (type==null) { warning(WRN_C_NOTYPE); type="double"; }

    Class<?> c = null;
    if      (type.equals    (C_TYPE_VAL_UCHAR )) c = int.class;
    else if (type.startsWith(C_TYPE_VAL_CHAR  )) c = String.class;
    else if (type.equals    (C_TYPE_VAL_USHORT)) c = int.class;
    else if (type.equals    (C_TYPE_VAL_SHORT )) c = short.class;
    else if (type.equals    (C_TYPE_VAL_UINT  )) c = long.class;
    else if (type.equals    (C_TYPE_VAL_INT   )) c = int.class;
    else if (type.equals    (C_TYPE_VAL_ULONG )) c = long.class;
    else if (type.equals    (C_TYPE_VAL_LONG  )) c = long.class;
    else if (type.equals    (C_TYPE_VAL_FLOAT )) c = float.class;
    else if (type.equals    (C_TYPE_VAL_DOUBLE)) c = double.class;
    else if (type.equals    (C_TYPE_VAL_BOOL  )) c = boolean.class;
    // TODO: C_TYPE_COMPLEX!
    
    if (c==null)
    {
      warning(String.format(WRN_C_BADTYPE,type));
      c = double.class;
    }

    idData.addComp(c,name);
  }
  
  private void handleCOMPend(XMLElement element) throws SAXException
  {
    // No op
  }

  private void handleRECstart(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_TABLE))
    {
      warning(String.format(WRN_T_NOTIN,element.name,TAG_TABLE));
      element.ignored = true;
      return;
    }

    nR++;
    if (idData.getCapacity()<=nR)
      idData.allocate(nR+1000);
    idData.setNRecs(nR);
    nC = -1;
  }
  
  private void handleRECend(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_TABLE)) return;

    if (nC<idData.getDimension()-1)
      warning(String.format(WRN_L_TOOFEW,nR));
  }

  private void handleCELLstart(XMLElement element) throws SAXException
  {
    // No op
  }
  
  private void handleCELLend(XMLElement element) throws SAXException
  {
    if (!inTopInstance()) return;
    if (!parentElementIs(TAG_REC))
    {
      warning(String.format(WRN_T_NOTIN,element.name,TAG_REC));
      return;
    }
    
    nC++;
    if (nC>=idData.getDimension())
    {
      warning(String.format(WRN_L_TOOMANY,nR));
      return;
    }

    if (JlDataFile.isStringType(idData.getCompType(nC)))
      idData.store(element.cdata,nR,nC);
    else if (JlDataFile.isNumericType(idData.getCompType(nC)))
      try
      {
        idData.dStore(Double.valueOf(element.cdata).doubleValue(),nR,nC);
      }
      catch (NumberFormatException e)
      {
        warning(String.format(WRN_L_BADVAL,element.cdata,nR,nC));
      }
    // TODO: else if (JlDataFile.isComplexType(idData.getCompType(nC))) ...
  }

  // Auxiliary methods
  
  /**
   * Returns <code>true</code> if the stack top or any element below the stack
   * top is ignored.
   */
  private boolean stackTopIsIgnored()
  {
    for (int i=0; i<stack.size(); i++)
      if (stack.get(i).ignored) return true;
    return false;
  }
  
  /**
   * Determines if the current stack top is descendant of the topmost instance
   * tag. The method will return <code>false</code> if the current stack top
   * is descendant of a nested instance or no descendant of any instance.
   */
  private boolean inTopInstance()
  {
    boolean bInst = false;
    for (int i=0; i<stack.size(); i++)
      if (TAG_INSTANCE.equals(stack.get(i).name))
      {
        if (bInst) return false;
        bInst = true;
      }
    return bInst;
  }
  
  /**
   * Determines if the second stack element has the specified name.
   */
  private boolean parentElementIs(String name)
  {
    if (stack.size()<2) return false;
    if (name==null) return false;
    XMLElement parent = stack.get(stack.size()-2);
    return name.equals(parent.name);
  }
  
  /**
   * Adds a warning to the list of warnings.
   */
  private void warning(String msg)
  {
    lWarnings.add(new SAXParseException(msg,locator));
  }
  
  // Getters
  
  /**
   * Returns the list of warnings and errors that occurred when parsing the
   * XML input.
   */
  public AbstractCollection<SAXParseException> getWarnings() 
  {
    return lWarnings;
  }

  /**
   * Returns the {@link JlData} object obtained from the XML input. 
   */
  public JlData getJlData()
  {
    return idData;
  }
  
  // -- Nested Classes --
  
  /**
   * One XML element with attributes and character data, but without nested
   * elements.
   * 
   * @author Matthias Wolff
   */
  class XMLElement
  {
    String     name;
    Attributes attr;
    String     cdata;
    boolean    ignored;
    
    XMLElement(String name, Attributes attr)
    {
      this.name    = name;
      this.attr    = attr;
      this.cdata   = null;
      this.ignored = false;
    }

    boolean isINSTANCE() { return XMLDataHandler.TAG_INSTANCE.equals(name); }
    boolean isFIELD()    { return XMLDataHandler.TAG_FIELD   .equals(name); }
    boolean isTABLE()    { return XMLDataHandler.TAG_TABLE   .equals(name); }
    boolean isCOMP()     { return XMLDataHandler.TAG_COMP    .equals(name); }
    boolean isREC()      { return XMLDataHandler.TAG_REC     .equals(name); }
    boolean isCELL()     { return XMLDataHandler.TAG_CELL    .equals(name); }
  }

}