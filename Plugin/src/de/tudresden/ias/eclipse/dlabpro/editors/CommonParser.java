
package de.tudresden.ias.eclipse.dlabpro.editors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IDocument;

abstract public class CommonParser
{
  abstract public CommonModel parse(IDocument document);

  /**
   * this method checks wether or not the content of the given string matches the given regex
   * 
   * @param string -
   *          the string to check
   * @param regex -
   *          the regex to check the string with
   * @return true if the regex matches the string,<br>
   *         false else
   */
  protected boolean stringContains(String string, String regex)
  {
    return getMatcherFor(string, regex).find();
  }

  /**
   * This method check wether or not the given text starts with the given expression disregarding
   * leading whitespaces.
   * 
   * @param text -
   *          the text to check in
   * @param string -
   *          the string to check for
   * @return true if the text starts with the string, false else
   */
  protected boolean textStartsWith(String text, String string)
  {
    return getMatcherFor(text, "\\s*" + string).find();
  }

  /**
   * this method creates and returns a {@link Matcher} for the given string and regex
   * 
   * @param string -
   *          the string to create the matcher for
   * @param regex -
   *          the regex to create the matcher for
   * @return {@link Matcher}
   */
  protected Matcher getMatcherFor(String string, String regex)
  {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(string);
    return matcher;
  }

  /**
   * checks wether or not the given text represents a comment (starting with #)
   * 
   * @param text -
   *          the text to check
   * @return true if the given text represents a comment,<br>
   *         false else
   */
  protected boolean textIsComment(String text)
  {
    return text.startsWith("#") || text.startsWith("COMMENT:");
  }

  /**
   * 
   * @param text
   * @param value
   * @return
   */
  protected boolean textIsInComment(String text, String value)
  {
    if (text.indexOf("#") >= 0) return text.indexOf(value) > text.indexOf("#");
    if (text.indexOf("COMMENT:") >= 0) return text.indexOf(value) > text.indexOf("COMMENT:");
    return false;
  }

  /**
   * This class represents a segment of the parsed text. Its used to store a detected object
   * temporarely.
   * 
   * @author Christian Feig
   * 
   */
  public class SegmentObject
  {
    String type = null;
    String name = null;
    int    id   = -1;

    /**
     * 
     * @param type -
     *          the type of the segment
     * @param name -
     *          the name of the object (maybe shown as in outline)
     * @param id -
     *          the id of the object
     */
    public SegmentObject(String type, String name, int id)
    {
      this.type = type;
      this.name = name;
      this.id = id;
    }

    /**
     * 
     * @param type -
     *          the type of the segment
     * @param name -
     *          the name of the object (maybe shown as in outline)
     */
    public SegmentObject(String type, String name)
    {
      this.type = type;
      this.name = name;
    }

    /**
     * 
     * @return the name of the segment
     */
    public String getName()
    {
      return name;
    }

    /**
     * 
     * @return the type of the segment
     */
    public String getType()
    {
      return type;
    }

    /**
     * 
     * @return the id of the segment
     */
    public int getId()
    {
      return id;
    }

    /**
     * 
     * @param the
     *          id
     */
    public void setId(int id)
    {
      this.id = id;
    }
  }
}
