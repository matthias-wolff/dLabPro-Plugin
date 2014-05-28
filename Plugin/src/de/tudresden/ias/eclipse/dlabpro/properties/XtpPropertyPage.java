package de.tudresden.ias.eclipse.dlabpro.properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;


/**
 * The dLabPro script property page.
 * 
 * @author Matthas Wolff
 */
public class XtpPropertyPage extends PropertyPage
{

  private static final String PATH_TITLE = "Path:";
  private XtpFilePropertiesEditor iEdi;
  
  public XtpPropertyPage()
  {
    super();
  }

  private void addFirstSection(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout(layout);

    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    composite.setLayoutData(data);

    // Label for path field
    Label pathLabel = new Label(composite, SWT.NONE);
    pathLabel.setText(PATH_TITLE);

    // Path text field
    Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
    pathValueText.setText(((IResource) getElement()).getFullPath().toString());
  }

  private void addSeparator(Composite parent)
  {
    Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    separator.setLayoutData(gridData);
  }

  private void addSecondSection(Composite parent)
  {    
    Composite iCps = new Composite(parent,SWT.NULL);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    iCps.setLayoutData(gridData);

    GridLayout iGl = new GridLayout(1,false);
    iGl.horizontalSpacing = 0;
    iGl.verticalSpacing = 0;
    iGl.marginWidth = 0;
    iGl.marginHeight = 0;
    iCps.setLayout(iGl);
    
    iEdi = new XtpFilePropertiesEditor(iCps,SWT.NONE,getXtpFileProperties(true));
    iEdi.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
    iEdi.setAutoResize(true);
    applyDialogFont(iEdi);

    Label iSep = new Label(iCps,SWT.NULL);
    iSep.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));

    iSep = new Label(iCps,SWT.SEPARATOR|SWT.HORIZONTAL);
    iSep.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
  }

  @Override
  protected Control createContents(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout();
    composite.setLayout(layout);
    GridData data = new GridData(GridData.FILL);
    data.grabExcessHorizontalSpace = true;
    composite.setLayoutData(data);

    addFirstSection(composite);
    addSeparator(composite);
    addSecondSection(composite);
    return composite;
  }
  
  @Override
  protected void performDefaults()
  {
    super.performDefaults();
    iEdi.setProperties(getXtpFileProperties(false));
  }

  @Override
  public boolean performOk()
  {
    XtpFileProperties iPrp = iEdi.getProperties();
    iPrp.store();
    return true;
  }

  protected XtpFileProperties getXtpFileProperties(boolean bPersistent)
  {
    try
    {
      return new XtpFileProperties((IFile)getElement(),bPersistent);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
  
}