/*
 * Created on 12.03.2005
 * 
 */

package de.tudresden.ias.eclipse.dlabpro.editors;

/*
 * (c) Copyright IBM Corp. 2000, 2001. All Rights Reserved.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManager;
import de.tudresden.ias.eclipse.dlabpro.editors.util.IColorManagerExtension;
import de.tudresden.ias.eclipse.dlabpro.preferences.IPreferenceConstants;

/**
 * Initialized with a color manager and a preference store, its subclasses are only responsible for
 * providing a list of preference keys based on which tokens are generated and to use this tokens to
 * define the rules controlling this scanner.
 * 
 */
public abstract class AbstractScanner extends BufferedRuleBasedScanner
{

  private static List      tokenProperties = new ArrayList();

  private IColorManager    fColorManager;
  private IPreferenceStore fPreferenceStore;

  private Map              fTokenMap       = new HashMap();
  private String[]         fPropertyNamesColor;
  // private String[] fPropertyNamesStyle;

  private IToken           fDefaultToken;

  /**
   * Adds a tokenProperty to the tokenProperties List.
   */
  protected void addTokenProperty(String tokenProperty)
  {
    tokenProperties.add(tokenProperty);
  }

  /**
   * Creates the list of rules controlling the specific scanner.
   */
  abstract protected List createRules();

  /**
   * Constructor Creates an abstract Java scanner.
   */
  public AbstractScanner(IColorManager manager, IPreferenceStore store)
  {
    super();
    fColorManager = manager;
    fPreferenceStore = store;
    initTokenProperties();
    setDefaultReturnToken(Token.UNDEFINED);
    initialize();
  }

  /**
   * This abstract method has to be implemented by the inheribating classes. It is called before the
   * <code>initialize()</code> method. Its nessecary to make this class work correctly. The method
   * indicates whose tokens shall be handled. It should add the tokenProperties which indicates
   * whose tokens shall be handled. Use the <code>addTokenProperty()</code> method to do this.
   * 
   */
  protected abstract void initTokenProperties();

  /**
   * Creates an abstract Java scanner.
   */
  // public AbstractScanner(IColorManager manager, IPreferenceStore store, int
  // bufsize) {
  // super(bufsize);
  // fColorManager= manager;
  // fPreferenceStore= store;
  // }
  /**
   * Must be called after the constructor has been called.
   */
  public final void initialize()
  {
    fPropertyNamesColor = new String[tokenProperties.size()];
    tokenProperties.toArray(fPropertyNamesColor);

    int length = fPropertyNamesColor.length;
    // fPropertyNamesStyle= new String[length];
    for (int i = 0; i < length; i++)
    {
      // fPropertyNamesStyle[i]= fPropertyNamesColor[i] + "_bold"; //$NON-NLS-1$
      addToken(fPropertyNamesColor[i]/* , fPropertyNamesStyle[i] */);
    }

    initializeRules();
  }

  private void addToken(String colorKey/* , String styleKey */)
  {
    RGB rgb = PreferenceConverter.getColor(fPreferenceStore, colorKey);
    if (fColorManager instanceof IColorManagerExtension)
    {
      IColorManagerExtension ext = (IColorManagerExtension)fColorManager;
      ext.unbindColor(colorKey);
      ext.bindColor(colorKey, rgb);
    }

    boolean bold = fPreferenceStore.getBoolean(colorKey + IPreferenceConstants.P_SUFFIX_BOLD);
    boolean italic = fPreferenceStore.getBoolean(colorKey + IPreferenceConstants.P_SUFFIX_ITALIC);
    int style = SWT.NORMAL;
    if (bold) style |= SWT.BOLD;
    if (italic) style |= SWT.ITALIC;

    // fTokenMap.put(colorKey, new Token(new
    // TextAttribute(fColorManager.getColor(colorKey), null, bold ? SWT.BOLD :
    // italic ? SWT.ITALIC : SWT.NORMAL)));
    fTokenMap.put(colorKey, new Token(new TextAttribute(fColorManager.getColor(colorKey), null,
        style)));
  }

  /**
   * returns the Token related to the key
   * 
   * @param key
   * @return Token
   */
  protected Token getToken(String key)
  {
    return (Token)fTokenMap.get(key);
  }

  private void initializeRules()
  {
    List rules = createRules();
    if (rules != null)
    {
      IRule[] result = new IRule[rules.size()];
      rules.toArray(result);
      setRules(result);
    }
  }

  private int indexOf(String property)
  {
    if (property != null)
    {
      int length = fPropertyNamesColor.length;
      for (int i = 0; i < length; i++)
      {
        if (fPropertyNamesColor[i].startsWith(property))
        // if (property.equals(fPropertyNamesColor[i]) ||
        // property.equals(fPropertyNamesColor[i]+IPreferenceConstants.BOLD_EXTENSION)
        // ||
        // property.equals(fPropertyNamesColor[i]+IPreferenceConstants.ITALIC_EXTENSION))
        return i;
      }
    }
    return -1;
  }

  public boolean affectsBehavior(PropertyChangeEvent event)
  {
    return indexOf(event.getProperty()) >= 0;
  }

  /**
   * is called when property changed
   * 
   * @param event -
   *          the PropertyChangedEvent
   */
  public void adaptToPreferenceChange(PropertyChangeEvent event)
  {
    String p = event.getProperty();
    int index = indexOf(p);
    Token token = getToken(fPropertyNamesColor[index]);
    if (fPropertyNamesColor[index].equals(p)) adaptToColorChange(token, event);
    else adaptToStyleChange(token, event);
  }

  /**
   * is called when colors changed
   * 
   * @param token
   * @param event
   */
  private void adaptToColorChange(Token token, PropertyChangeEvent event)
  {
    RGB rgb = null;

    Object value = event.getNewValue();
    if (value instanceof RGB) rgb = (RGB)value;
    else if (value instanceof String)
    {
      rgb = StringConverter.asRGB((String)value);
    }

    if (rgb != null)
    {

      String property = event.getProperty();

      if (fColorManager instanceof IColorManagerExtension)
      {
        IColorManagerExtension ext = (IColorManagerExtension)fColorManager;
        ext.unbindColor(property);
        ext.bindColor(property, rgb);
      }

      Object data = token.getData();
      if (data instanceof TextAttribute)
      {
        TextAttribute oldAttr = (TextAttribute)data;
        token.setData(new TextAttribute(fColorManager.getColor(property), oldAttr.getBackground(),
            oldAttr.getStyle()));
      }
    }
  }

  // private void adaptToStyleChange(Token token, PropertyChangeEvent event) {
  // boolean propNewValue= false;
  // Object value= event.getNewValue();
  // if (value instanceof Boolean)
  // propNewValue= ((Boolean) value).booleanValue();
  // else if (value instanceof String) {
  // String s= (String) value;
  // if (IPreferenceStore.TRUE.equals(s))
  // propNewValue= true;
  // else if (IPreferenceStore.FALSE.equals(s))
  // propNewValue= false;
  // }
  //		
  // boolean bold = false;
  // boolean italic = false;
  // if(event.getProperty().endsWith(IPreferenceConstants.BOLD_EXTENSION))
  // bold = propNewValue;
  // if(event.getProperty().endsWith(IPreferenceConstants.ITALIC_EXTENSION))
  // italic = propNewValue;
  //		
  // Object data= token.getData();
  // if (data instanceof TextAttribute) {
  // TextAttribute oldAttr= (TextAttribute) data;
  // boolean isBold = (oldAttr.getStyle() == SWT.BOLD);
  // if (isBold != propNewValue)
  // token.setData(new TextAttribute(oldAttr.getForeground(),
  // oldAttr.getBackground(), bold ? SWT.BOLD : italic ? SWT.ITALIC :
  // SWT.NORMAL));
  // }
  // }

  /**
   * is called when style changed
   */
  private void adaptToStyleChange(Token token, PropertyChangeEvent event)
  {
    boolean propNewValue = false;
    Object value = event.getNewValue();
    if (value instanceof Boolean) propNewValue = ((Boolean)value).booleanValue();
    else if (value instanceof String)
    {
      String s = (String)value;
      if (IPreferenceStore.TRUE.equals(s)) propNewValue = true;
      else if (IPreferenceStore.FALSE.equals(s)) propNewValue = false;
    }

    Object data = token.getData();
    if (data instanceof TextAttribute)
    {
      TextAttribute oldAttr = (TextAttribute)data;
      int style = SWT.NORMAL;
      if (event.getProperty().endsWith(IPreferenceConstants.P_SUFFIX_BOLD) && propNewValue) style |= SWT.BOLD;
      if (event.getProperty().endsWith(IPreferenceConstants.P_SUFFIX_ITALIC) && propNewValue) style |= SWT.ITALIC;

      token.setData(new TextAttribute(oldAttr.getForeground(), oldAttr.getBackground(), style));
    }
  }

  /**
   * Returns the next token in the document.
   * 
   * @return the next token in the document
   */
  public IToken nextToken()
  {

    IToken token;

    while (true)
    {

      fTokenOffset = fOffset;
      fColumn = UNDEFINED;

      for (int i = 0; i < fRules.length; i++)
      {
        token = (fRules[i].evaluate(this));
        if (!token.isUndefined()) return token;
      }
      if (read() == EOF) return Token.EOF;
      else return fDefaultToken;
    }
  }

  /**
   * sets the defaultReturnToken
   */
  public void setDefaultReturnToken(IToken token)
  {
    fDefaultToken = token;
  }
}
