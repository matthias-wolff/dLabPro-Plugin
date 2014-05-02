package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;


public class HorizontalRulerPanel extends Composite {

	private Color backgroundColor;
  
  private Ruler mRuler;

	private Spacer mLeftSpacer;

	private Spacer mRightSpacer;
	
	private boolean mUseRightSideSpacer;
	
	public HorizontalRulerPanel(Composite parent) {
		super(parent, SWT.NONE);
		backgroundColor = parent.getBackground();
		setup();
		
		mUseRightSideSpacer = true;
	}

	public void addHorizontalRuler(Ruler r) {
		mRuler = r;
		mRuler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mRightSpacer = new Spacer(this, 10);
		mRightSpacer.setBackground(backgroundColor);
		mRightSpacer.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		mRightSpacer.setVisibility(false);
	}

	public Ruler getHorizontalRuler() {
		return mRuler;
	}
	
	public final void useRightSideSpacer(int width) {
		if(mUseRightSideSpacer) {
			mRightSpacer.setWidth(width);
			mRightSpacer.setVisibility(true);
			layout();
		}
	}
	
	/**
	 * disables the spacer on the right side of this display
	 */
	public final void disableRightSideSpacer() {
		mUseRightSideSpacer = false;
		mRightSpacer.setWidth(0);
		mRightSpacer.setVisibility(false);
		layout();
	}

	private void setup() {
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		setLayout(gl);

		mLeftSpacer = new Spacer(this);
    mLeftSpacer.setBackground(backgroundColor);
		mLeftSpacer.setLayoutData(new GridData(SWT.FILL));
	}
}
