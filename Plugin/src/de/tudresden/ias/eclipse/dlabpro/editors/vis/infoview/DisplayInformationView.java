package de.tudresden.ias.eclipse.dlabpro.editors.vis.infoview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import de.tucottbus.kt.jlab.datadisplays.events.DisplayInfoMouseMoveEvent;
import de.tucottbus.kt.jlab.datadisplays.events.DisplayInfoScrollEvent;
import de.tucottbus.kt.jlab.datadisplays.interfaces.IDisplayInfoListener;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.editor.VisEditor;

/**
 * 
 * 
 * @author Stephan Larws
 *
 */
public class DisplayInformationView extends ViewPart implements
    IDisplayInfoListener
{
  private FormToolkit  iFmtk;     // The form toolkit
  private ScrolledForm iForm;     // The form
  private Section      iSecM;     // Section "Mouse Position"
  private Section      iSecD;     // Section "Display Range"
  private Label        iLabM[][]; // Labels in section "Mouse Position"
  private Label        iLabD[][]; // Labels in section "Display Range"
  private final int    COL_M = 7; // Number of columns of mouse position
  private final int    ROW_M = 4; // Number of rows of mouse position
  private final int    COL_D = 5; // Number of columns of display range
  private final int    ROW_D = 4; // Number of rows of display range

  /**
   * 
   */
  public DisplayInformationView()
  {
  }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
  {
	  GridLayout      iGl;
    GridData        iGd;
    TableWrapLayout iTwl;
    int             nR;
    int             nC;
	  
	  // Initialize the form
	  iFmtk = new FormToolkit(parent.getDisplay());
    iForm = iFmtk.createScrolledForm(parent);
    iGl = new GridLayout();
    iGl.numColumns = 1;
    iForm.getBody().setLayout(iGl);

    // Create section "Mouse Position"
    iSecM = iFmtk.createSection(iForm.getBody(),Section.TITLE_BAR|Section.TWISTIE|Section.EXPANDED);
    iGd = new GridData(GridData.FILL_HORIZONTAL);
    iSecM.setLayoutData(iGd);
    iSecM.setText("Mouse Position");
    iTwl = new TableWrapLayout();
    iTwl.numColumns = COL_M;
    Composite iCpsM = iFmtk.createComposite(iSecM);
    iCpsM.setLayout(iTwl);
    iLabM = new Label[ROW_M][COL_M];
    
    for (nR=0; nR<ROW_M; nR++)
      for (nC=0; nC<COL_M; nC++)
      {
        int nStyle = nC==4||nC==5?SWT.BOLD:SWT.NONE;
        iLabM[nR][nC] = iFmtk.createLabel(iCpsM, "", nStyle);
        iLabM[nR][nC].setLayoutData(new TableWrapData());    
      }
    for (nR=0; nR<ROW_M; nR++)
    {
      iLabM[nR][2].setAlignment(SWT.RIGHT);
      iLabM[nR][4].setAlignment(SWT.RIGHT);
    }
    iSecM.setClient(iCpsM);
    clearSecM(true);

    // Create section "Display Range"
    iSecD = iFmtk.createSection(iForm.getBody(),Section.TITLE_BAR|Section.TWISTIE|Section.EXPANDED);
    iGd = new GridData(GridData.FILL_HORIZONTAL);
    iSecD.setLayoutData(iGd);
    iSecD.setText("Display Range");
    iTwl = new TableWrapLayout();
    iTwl.numColumns = COL_D;
    Composite iCpsD = iFmtk.createComposite(iSecD);
    iCpsD.setLayout(iTwl);
    iLabD = new Label[ROW_D][COL_D];
    
    for (nR=0; nR<ROW_D; nR++)
      for (nC=0; nC<COL_D; nC++)
      {
        iLabD[nR][nC] = iFmtk.createLabel(iCpsD, "", SWT.NONE);
        iLabD[nR][nC].setLayoutData(new TableWrapData());    
      }
    for (nR=0; nR<ROW_D; nR++)
       iLabD[nR][2].setAlignment(SWT.RIGHT);
    iSecD.setClient(iCpsD);
    clearSecD(true);
  }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
  {
    iForm.setFocus();
  }

	public void informationChangedMouseMove(DisplayInfoMouseMoveEvent e)
  {
	  iLabM[1][2].setText(e.sXValA);
    iLabM[1][3].setText(e.sXUnit);
    iLabM[1][4].setText(e.sXValD);
    iLabM[1][5].setText(e.sXUnit);
    iLabM[1][5].setText(e.sYData.equals("")?"--":"("+e.sXData+")");

    iLabM[2][2].setText(e.sYValA);
    iLabM[2][3].setText(e.sYUnit);
    iLabM[2][4].setText(e.sYValD);
    iLabM[2][5].setText(e.sYUnit);
    iLabM[2][5].setText(e.sYData.equals("")?"--":"("+e.sYData+")");

    iLabM[3][4].setText(e.sZValD);
    iLabM[3][5].setText(e.sZUnit);

    iSecM.layout();
  }

	public void informationChangedScroll(DisplayInfoScrollEvent e)
  {
	  if (e==null) return;

    iLabD[1][0].setText("X" + (e.sXAxis.length() > 0 ? "/" + e.sXAxis : ""));
    iLabM[1][0].setText("X" + (e.sXAxis.length() > 0 ? "/" + e.sXAxis : ""));
    iLabD[1][2].setText(e.sXRngP);
    iLabD[1][3].setText(e.sXUnit);
    iLabD[1][4].setText(e.sXRngL);

    iLabD[2][0].setText("Y" + (e.sYAxis.length() > 0 ? "/" + e.sYAxis : ""));
    iLabM[2][0].setText("Y" + (e.sYAxis.length() > 0 ? "/" + e.sYAxis : ""));
    iLabD[2][2].setText(e.sYRngP);
    iLabD[2][3].setText(e.sYUnit);
    iLabD[2][4].setText(e.sYRngL);

    iLabD[3][0].setText("Z" + (e.sZAxis.length() > 0 ? "/" + e.sZAxis : ""));
    iLabM[3][0].setText("Z" + (e.sZAxis.length() > 0 ? "/" + e.sZAxis : ""));
    iLabD[3][2].setText(e.sZRngP);
    iLabD[3][3].setText(e.sZUnit);

    iSecM.layout();
    iSecD.layout();
  }

	public void clearSecM(boolean bDoLayout)
	{
    for (int nR=0; nR<ROW_M; nR++)
      for (int nC=0; nC<COL_M; nC++)
        iLabM[nR][nC].setText("");
    iLabM[0][2].setText("actual"); iLabM[0][4].setText("data");
    iLabM[1][0].setText("X"); iLabM[1][1].setText(":");
    iLabM[2][0].setText("Y"); iLabM[2][1].setText(":");
    iLabM[3][0].setText("Z"); iLabM[3][1].setText(":");
    if (bDoLayout) iSecM.layout();
	}

  public void clearSecD(boolean bDoLayout)
  {
    for (int nR=0; nR<ROW_D; nR++)
      for (int nC=0; nC<COL_D; nC++)
        iLabD[nR][nC].setText("");
    iLabD[0][2].setText("physical"); iLabD[0][4].setText("logical");
    iLabD[1][0].setText("X"); iLabD[1][1].setText(":");
    iLabD[2][0].setText("Y"); iLabD[2][1].setText(":");
    iLabD[3][0].setText("Z"); iLabD[3][1].setText(":");
    if (bDoLayout) iSecD.layout();
  }

  public void clearMouseInformation()
  {
    clearSecM(false);
    clearSecD(false);
  }
}