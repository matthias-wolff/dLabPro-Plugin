package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.VIS;

public class Spacer extends Canvas {
	
	private int mWidth;
	private boolean mVisibility;

	public Spacer(Composite parent) {
		super(parent, SWT.NONE);
		mWidth = -1;
		mVisibility = true;
	}

	public Spacer(Composite parent, int width) {
		super(parent, SWT.NONE);
		mWidth = width;
		mVisibility = false;
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		int x, y;
		if(!mVisibility) {
			return new Point(0, 0);
		}
		if(mWidth == -1) {
			x = VIS.OTHERDIMENSION_X;
			y = VIS.OTHERDIMENSION_Y;
		}
		else {
			x = mWidth;
			y = hHint;
		}
		return new Point(x, y);
	}
	
	public void setVisibility(boolean v) {
		mVisibility = v;
	}
	
	public void setWidth(int width) {
		mWidth = width;
	}
}
