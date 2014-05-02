
package de.tudresden.ias.eclipse.dlabpro.perspectives;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import de.tudresden.ias.eclipse.dlabpro.JavaUI;
import de.tudresden.ias.eclipse.dlabpro.views.ManualContentView;
import de.tudresden.ias.eclipse.dlabpro.views.UASRManualTocView;

/**
 * @author Xian
 * 
 */
public class DLabProPerspective implements IPerspectiveFactory
{

  public static final String ID_PROGRESS_VIEW = "org.eclipse.ui.views.ProgressView";     // see bug
  // 63563
  // //$NON-NLS-1$
  /**
   * Id of the new Search view (value <code>"org.eclipse.search.ui.views.SearchView"</code>).
   */
  public static final String SEARCH_VIEW_ID   = "org.eclipse.search.ui.views.SearchView"; //$NON-NLS-1$

  public void createInitialLayout(IPageLayout layout)
  {
    String editorArea = layout.getEditorArea();

    IFolderLayout folder = layout.createFolder("left", IPageLayout.LEFT, (float)0.25, editorArea); //$NON-NLS-1$
    folder.addView(JavaUI.ID_PACKAGES);
    // folder.addView(JavaUI.ID_TYPE_HIERARCHY);
    folder.addView(UASRManualTocView.ID);
    folder.addPlaceholder(IPageLayout.ID_RES_NAV);

    IFolderLayout outputfolder = layout.createFolder(
        "bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); //$NON-NLS-1$
    outputfolder.addView(IPageLayout.ID_PROBLEM_VIEW);
    outputfolder.addView(JavaUI.ID_JAVADOC_VIEW);
    outputfolder.addView(JavaUI.ID_SOURCE_VIEW);
    outputfolder.addPlaceholder(SEARCH_VIEW_ID);
    outputfolder.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
    outputfolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
    outputfolder.addPlaceholder(ID_PROGRESS_VIEW);
    outputfolder.addView(ManualContentView.ID);

    layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, (float)0.75, editorArea);

    layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
    layout.addActionSet(JavaUI.ID_ACTION_SET);
    layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
    layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

    // views - java
    layout.addShowViewShortcut(JavaUI.ID_PACKAGES);
    // layout.addShowViewShortcut(JavaUI.ID_TYPE_HIERARCHY);
    layout.addShowViewShortcut(JavaUI.ID_SOURCE_VIEW);
    layout.addShowViewShortcut(JavaUI.ID_JAVADOC_VIEW);

    // views - search
    layout.addShowViewShortcut(SEARCH_VIEW_ID);

    // views - debugging
    layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

    // views - standard workbench
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
    layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);

    // new actions - Java project creation wizard
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
    // defineActions(layout);
    // defineLayout(layout);

  }

  // private void defineActions(IPageLayout layout){
  // // Add "new wizards".
  // layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
  // layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
  //
  // // Add "show views".
  // layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
  // layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
  // layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
  // layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
  // layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
  //
  // }
  // private void defineLayout(IPageLayout layout){
  // // Editors are placed for free.
  // String editorArea = layout.getEditorArea();
  //      
  // // Place navigator and outline to left of
  // // editor area.
  //        
  // IFolderLayout left =
  // layout.createFolder("left", IPageLayout.LEFT, (float) 0.25, editorArea);
  // left.addView(IPageLayout.ID_RES_NAV);
  // IFolderLayout right =
  // layout.createFolder("right", IPageLayout.RIGHT, (float) 0.75, editorArea);
  // right.addView(IPageLayout.ID_OUTLINE);
  // // IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.8,
  // editorArea);
  // // //bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);
  // // bottom.addView(DEF_CONSOLEVIEW);
  //       
  //
  // }

}
