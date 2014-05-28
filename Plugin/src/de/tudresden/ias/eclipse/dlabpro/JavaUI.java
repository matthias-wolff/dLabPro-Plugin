
package de.tudresden.ias.eclipse.dlabpro;

public interface JavaUI
{
  /**
   * The id of the Java plugin (value <code>"org.eclipse.jdt.ui"</code>).
   */
  public static final String ID_PLUGIN                      = "org.eclipse.jdt.ui";                             //$NON-NLS-1$

  /**
   * The id of the Java perspective (value <code>"org.eclipse.jdt.ui.JavaPerspective"</code>).
   */
  public static final String ID_PERSPECTIVE                 = "org.eclipse.jdt.ui.JavaPerspective";             //$NON-NLS-1$

  /**
   * The id of the Java hierarchy perspective (value
   * <code>"org.eclipse.jdt.ui.JavaHierarchyPerspective"</code>).
   */
  public static final String ID_HIERARCHYPERSPECTIVE        = "org.eclipse.jdt.ui.JavaHierarchyPerspective";    //$NON-NLS-1$

  /**
   * The id of the Java action set (value <code>"org.eclipse.jdt.ui.JavaActionSet"</code>).
   */
  public static final String ID_ACTION_SET                  = "org.eclipse.jdt.ui.JavaActionSet";               //$NON-NLS-1$

  /**
   * The id of the Java Element Creation action set (value
   * <code>"org.eclipse.jdt.ui.JavaElementCreationActionSet"</code>).
   * 
   * @since 2.0
   */
  public static final String ID_ELEMENT_CREATION_ACTION_SET = "org.eclipse.jdt.ui.JavaElementCreationActionSet"; //$NON-NLS-1$

  /**
   * The id of the Java Coding action set (value <code>"org.eclipse.jdt.ui.CodingActionSet"</code>).
   * 
   * @since 2.0
   */
  public static final String ID_CODING_ACTION_SET           = "org.eclipse.jdt.ui.CodingActionSet";             //$NON-NLS-1$

  /**
   * The id of the Java action set for open actions (value
   * <code>"org.eclipse.jdt.ui.A_OpenActionSet"</code>).
   * 
   * @since 2.0
   */
  public static final String ID_OPEN_ACTION_SET             = "org.eclipse.jdt.ui.A_OpenActionSet";             //$NON-NLS-1$

  /**
   * The id of the Java Search action set (value <code>org.eclipse.jdt.ui.SearchActionSet"</code>).
   * 
   * @since 2.0
   */
  public static final String ID_SEARCH_ACTION_SET           = "org.eclipse.jdt.ui.SearchActionSet";             //$NON-NLS-1$

  /**
   * The editor part id of the editor that presents Java compilation units (value
   * <code>"org.eclipse.jdt.ui.CompilationUnitEditor"</code>).
   */
  public static final String ID_CU_EDITOR                   = "org.eclipse.jdt.ui.CompilationUnitEditor";       //$NON-NLS-1$

  /**
   * The editor part id of the editor that presents Java binary class files (value
   * <code>"org.eclipse.jdt.ui.ClassFileEditor"</code>).
   */
  public static final String ID_CF_EDITOR                   = "org.eclipse.jdt.ui.ClassFileEditor";             //$NON-NLS-1$

  /**
   * The editor part id of the code snippet editor (value
   * <code>"org.eclipse.jdt.ui.SnippetEditor"</code>).
   */
  public static final String ID_SNIPPET_EDITOR              = "org.eclipse.jdt.ui.SnippetEditor";               //$NON-NLS-1$

  /**
   * The view part id of the Packages view (value <code>"org.eclipse.jdt.ui.PackageExplorer"</code>).
   * <p>
   * When this id is used to access a view part with <code>IWorkbenchPage.findView</code> or
   * <code>showView</code>, the returned <code>IViewPart</code> can be safely cast to an
   * <code>IPackagesViewPart</code>.
   * </p>
   * 
   * @see IPackagesViewPart
   * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
   * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
   */
  public static final String ID_PACKAGES                    = "org.eclipse.jdt.ui.PackageExplorer";             //$NON-NLS-1$

  /**
   * The view part id of the type hierarchy part (value
   * <code>"org.eclipse.jdt.ui.TypeHierarchy"</code>).
   * <p>
   * When this id is used to access a view part with <code>IWorkbenchPage.findView</code> or
   * <code>showView</code>, the returned <code>IViewPart</code> can be safely cast to an
   * <code>ITypeHierarchyViewPart</code>.
   * </p>
   * 
   * @see ITypeHierarchyViewPart
   * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
   * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
   */
  public static final String ID_TYPE_HIERARCHY              = "org.eclipse.jdt.ui.TypeHierarchy";               //$NON-NLS-1$

  /**
   * The view part id of the source (declaration) view (value
   * <code>"org.eclipse.jdt.ui.SourceView"</code>).
   * 
   * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
   * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
   * @since 3.0
   */
  public static final String ID_SOURCE_VIEW                 = "org.eclipse.jdt.ui.SourceView";                  //$NON-NLS-1$

  /**
   * The view part id of the Javadoc view (value <code>"org.eclipse.jdt.ui.JavadocView"</code>).
   * 
   * @see org.eclipse.ui.IWorkbenchPage#findView(java.lang.String)
   * @see org.eclipse.ui.IWorkbenchPage#showView(java.lang.String)
   * @since 3.0
   */
  public static final String ID_JAVADOC_VIEW                = "org.eclipse.jdt.ui.JavadocView";                 //$NON-NLS-1$

  /**
   * The id of the Java Browsing Perspective (value
   * <code>"org.eclipse.jdt.ui.JavaBrowsingPerspective"</code>).
   * 
   * @since 2.0
   */
  public static String       ID_BROWSING_PERSPECTIVE        = "org.eclipse.jdt.ui.JavaBrowsingPerspective";     //$NON-NLS-1$

  /**
   * The view part id of the Java Browsing Projects view (value
   * <code>"org.eclipse.jdt.ui.ProjectsView"</code>).
   * 
   * @since 2.0
   */
  public static String       ID_PROJECTS_VIEW               = "org.eclipse.jdt.ui.ProjectsView";                //$NON-NLS-1$

  /**
   * The view part id of the Java Browsing Packages view (value
   * <code>"org.eclipse.jdt.ui.PackagesView"</code>).
   * 
   * @since 2.0
   */
  public static String       ID_PACKAGES_VIEW               = "org.eclipse.jdt.ui.PackagesView";                //$NON-NLS-1$

  /**
   * The view part id of the Java Browsing Types view (value
   * <code>"org.eclipse.jdt.ui.TypesView"</code>).
   * 
   * @since 2.0
   */
  public static String       ID_TYPES_VIEW                  = "org.eclipse.jdt.ui.TypesView";                   //$NON-NLS-1$

  /**
   * The view part id of the Java Browsing Members view (value
   * <code>"org.eclipse.jdt.ui.MembersView"</code>).
   * 
   * @since 2.0
   */
  public static String       ID_MEMBERS_VIEW                = "org.eclipse.jdt.ui.MembersView";                 //$NON-NLS-1$

  /**
   * The class org.eclipse.debug.core.model.IProcess allows attaching String properties to
   * processes. The Java UI contributes a property page for IProcess that will show the contents of
   * the property with this key. The intent of this property is to show the command line a process
   * was launched with.
   * 
   * @deprecated
   */
  public final static String ATTR_CMDLINE                   = "org.eclipse.jdt.ui.launcher.cmdLine";            //$NON-NLS-1$
}
