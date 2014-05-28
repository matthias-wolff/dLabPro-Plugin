/*
 * Created on 13.06.2005
 * 
 * @author Xian
 */

package de.tudresden.ias.eclipse.dlabpro.launch;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.internal.ide.StringMatcher;



/**
 * @author Christian Feig
 * 
 */
public class DLabProConsoleLineTracker implements IConsoleLineTracker
{

  IConsole      console = null;
  StringMatcher fErrorMatcher;
  StringMatcher fWarningMatcher;
  StringMatcher fBreakPointMatcher;
  Pattern       fLineNumberMatcher;
  Pattern       fLineNumber;

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.console.IConsoleLineTracker#init(org.eclipse.debug.ui.console.IConsole)
   */
  public void init(IConsole console)
  {
    this.console       = console;
    fErrorMatcher      = new StringMatcher("*):*error*"      ,false,false);
    fWarningMatcher    = new StringMatcher("*):*warning*"    ,false,false);
    fBreakPointMatcher = new StringMatcher("*):*BREAK POINT*",false,false);
    fLineNumberMatcher = Pattern.compile("\\(\\d+\\):");
    fLineNumber        = Pattern.compile("\\d+");
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.console.IConsoleLineTracker#lineAppended(org.eclipse.jface.text.IRegion)
   */
  public void lineAppended(IRegion line)
  {
    try
    {
      int offset = line.getOffset();
      int length = line.getLength();
      String text = console.getDocument().get(offset, length);
      if (fErrorMatcher.match(text) || fWarningMatcher.match(text) || fBreakPointMatcher.match(text))
      {
        Matcher m = fLineNumberMatcher.matcher(text);
        if (m.find())
        {
          String lineNumber = text.substring(m.start(), m.end());
          String file = text.substring(0, m.start());

          Matcher m2 = fLineNumber.matcher(lineNumber);
          if (m2.find())
          {
            String lineNo = lineNumber.substring(m2.start(), m2.end());
            IHyperlink link = new DLabProConsoleHyperlink(lineNo, file, console);
            console.addLink(link, offset, m.end());
          }
        }
      }
    }
    catch (BadLocationException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.debug.ui.console.IConsoleLineTracker#dispose()
   */
  public void dispose()
  {
    fErrorMatcher = null;
    fLineNumberMatcher = null;
    console = null;
  }

}
