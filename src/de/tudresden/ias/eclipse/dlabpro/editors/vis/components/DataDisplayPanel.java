package de.tudresden.ias.eclipse.dlabpro.editors.vis.components;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;

import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.displays.AbstractDataDisplay;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.interfaces.IVerticalZoomSubject;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.listeners.VerticalActionListener;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.listeners.ZoomEvent;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.DummyRuler;
import de.tudresden.ias.eclipse.dlabpro.editors.vis.components.rulers.Ruler;

/**
 * This panel is used to display a vertical ruler and a DataDisplay
 * 
 * @author Stephan Larws
 * 
 */
public class DataDisplayPanel extends Composite implements IVerticalZoomSubject {
	
	private LinkedList mZoomListeners;

	private AbstractDataDisplay mDisplay;

	private Ruler mRuler;

	private Spacer mLeftSpacer;
	
	private Spacer mRightSpacer;
	
	private boolean mUseRightSideSpacer;
	
	private ScrollBar mVerticalScrollBar;
	
	private Color backgroundColor;

	public DataDisplayPanel(Composite parent) {
		super(parent, SWT.V_SCROLL);
		mVerticalScrollBar = getVerticalBar();
		mVerticalScrollBar.setVisible(false);
		mZoomListeners = new LinkedList();
		backgroundColor = parent.getBackground();
		setup();
	}

	public void addVerticalRuler(Ruler vr) {
		mRuler = vr;
		GridData iGd = new GridData(SWT.FILL,SWT.FILL,false,true);
		if (vr instanceof DummyRuler) iGd = new GridData(SWT.FILL);
		mRuler.setLayoutData(iGd);
		mRuler.moveAbove(null);
	}

	/**
	 * Adds a DataDisplay to this panel. If a display already exists this method overwrites
	 * the old display
	 * 
	 * @param dd the new DataDisplay to be added
	 */
	public void addDataDisplay(AbstractDataDisplay dd) {
		mDisplay = dd;
		mDisplay.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	public void addRightSpacer(int width) {
		mRightSpacer = new Spacer(this, width);
		mRightSpacer.setBackground(backgroundColor);
		mRightSpacer.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}

	/**
	 * Returns the display on this panel
	 * 
	 * @return		
	 * 			The display belonging to this panel
	 */
	public AbstractDataDisplay getDataDisplay() {
		return mDisplay;
	}

	/**
	 * @return		the vertical ruler belonging to this panel
	 */
	public Ruler getVerticalRuler() {
		return mRuler;
	}
	
	public final void addZoomListener(VerticalActionListener l) {
		mZoomListeners.add(l);
	}
		
	public final void fireVerticalZoomInEvent() {
		VerticalActionListener l;
		for(int i = 0; i < mZoomListeners.size(); i++) {
			l = (VerticalActionListener)mZoomListeners.get(i);
			l.handleVerticalZoomIn(new ZoomEvent(mVerticalScrollBar.getSize().x));
		}
	}
	
	public final void fireVerticalZoomOutEvent() {
//		VerticalActionListener l;
//		for(int i = 0; i < mZoomListeners.size(); i++) {
//			l = (VerticalActionListener)mZoomListeners.get(i);
//			ZoomEvent z = new ZoomEvent(mVerticalScrollBar.getSize().x);
//			if(!mRuler.isZoomedVertical())
//				z.setWidgetZoomState(false);
//			l.handleVerticalZoomOut(z);
//		}
	}
	
	public final void fireVerticalScrollEvent(int direction) {
		VerticalActionListener l;
		for(int i = 0; i < mZoomListeners.size(); i++) {
			l = (VerticalActionListener)mZoomListeners.get(i);
			l.handleVerticalScrollEvent(direction);
		}
	}	
	
	public final int getVerticalScrollBarWidth() {
		return mVerticalScrollBar.getSize().x - 1;
	}
	
	/**
	 * enables a spacer on the right side of this display as wide as width
	 * 
	 * @param width the width of the spacer
	 */
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
		mRightSpacer.setWidth(0);
		mRightSpacer.setVisibility(false);
		layout();
	}

	private final void setup() {
		GridLayout gl = new GridLayout(3, false);
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		setLayout(gl);
		
		mUseRightSideSpacer = true;
		mVerticalScrollBar.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) {
//				int max = mVerticalScrollBar.getMaximum();
//				int val = mVerticalScrollBar.getSelection();
//				int thumb = mVerticalScrollBar.getThumb();
//				
//				val = max - val - thumb;
//				
//				if(mDisplay instanceof IVerticalScrollable) {				
//					mRuler.scrollVertical(val, max);
//					((IVerticalScrollable)mDisplay).scrollVertical(val, max);
//				}
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {
		
			}
		
		});		
	}
	
}
