package co.edu.unal.colswe.changescribe.core;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.RenameDetector;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import changescribe.core.preferences.PreferenceConstants;
import co.edu.unal.colswe.changescribe.core.ast.ProjectInformation;
import co.edu.unal.colswe.changescribe.core.commitsignature.InformationDialog;
import co.edu.unal.colswe.changescribe.core.commitsignature.SignatureCanvas;
import co.edu.unal.colswe.changescribe.core.decorator.ProblemLabelDecorator;
import co.edu.unal.colswe.changescribe.core.editor.JavaViewer;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile;
import co.edu.unal.colswe.changescribe.core.git.ChangedFile.TypeChange;
import co.edu.unal.colswe.changescribe.core.listener.SummarizeVersionChangesListener;
import co.edu.unal.colswe.changescribe.core.stereotype.taxonomy.MethodStereotype;
import co.edu.unal.colswe.changescribe.core.util.UIPreferences;
import edu.stanford.nlp.util.Sets;

public class DescribeVersionsDialog extends TitleAreaDialog {
    private StyledText text;

    private Git git;

    private IJavaProject selection;

    private JavaViewer editor;

    private ListSelectionDialog listSelectionDialog;

    private FormToolkit toolkit;

    private Section filesSection;

    private CachedCheckboxTreeViewer filesViewer;

    private Set<ChangedFile> items;

    private TreeMap<MethodStereotype, Integer> signatureMap;

    private SignatureCanvas signatureCanvas;

    private Text previousVersionText;

    private Text currentVersionText;

    private static final String DIALOG_SETTINGS_SECTION_NAME = Activator.getDefault() + ".COMMIT_DIALOG_SECTION"; //$NON-NLS-1$

    private SashForm sashForm;

    private Composite messageAndPersonArea;

    private String commitCurrentID = "";

    private String commitPreviousID = "";

    private Shell shellTmp;

    public DescribeVersionsDialog(Shell shell, Git git, IJavaProject selection) {
        super(shell);
        shellTmp = shell;

        this.git = git;
        this.setSelection(selection);
        this.setHelpAvailable(false);

        Activator.getDefault().getPreferenceStore().addPropertyChangeListener(new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE.equals(event.getProperty())) {
                    if (getShell() != null) {
                        getShell().redraw();
                        getShell().layout();
                        refreshView();
                    }
                }
            }
        });
    }

    public void refreshView() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openInformation(getShell(), "Information", "You must close the window for the changes to take effect");
            }
        });
    }

    static class CommitFileContentProvider extends BaseWorkbenchContentProvider {
        @SuppressWarnings("rawtypes")
        @Override
        public Object[] getElements(Object element) {
            if (element instanceof Object[])
                return (Object[]) element;
            if (element instanceof Collection)
                return ((Collection) element).toArray();
            return new Object[0];
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return false;
        }
    }

    static class CommitPathLabelProvider extends ColumnLabelProvider {

        @Override
        public String getText(Object obj) {
            return ((ChangedFile) obj).getPath();
        }

        @Override
        public String getToolTipText(Object element) {
            return ((ChangedFile) element).getPath();
        }

    }

    static class CommitStatusLabelProvider extends BaseLabelProvider implements
            IStyledLabelProvider {

        private Image DEFAULT = PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_FILE);

        private ResourceManager resourceManager = new LocalResourceManager(
                JFaceResources.getResources());

        private Image getEditorImage(ChangedFile item) {
            Image image = DEFAULT;
            String name = new Path(item.getPath()).lastSegment();
            if (name != null) {
                ImageDescriptor descriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(name);
                image = (Image) this.resourceManager.get(descriptor);
            }
            return image;
        }

        private Image getDecoratedImage(Image base, ImageDescriptor decorator) {
            DecorationOverlayIcon decorated = new DecorationOverlayIcon(base,
                    decorator, IDecoration.BOTTOM_RIGHT);
            return (Image) this.resourceManager.get(decorated);
        }

        @Override
        public StyledString getStyledText(Object element) {
            return new StyledString();
        }

        @Override
        public Image getImage(Object element) {
            ChangedFile item = (ChangedFile) element;
            ImageDescriptor decorator = null;
            // Image other = null;
            Image finalOther = null;
            switch (item.getTypeChange()) {
            case UNTRACKED:
                decorator = UIIcons.OVR_UNTRACKED;
                break;
            case ADDED:
            case ADDED_INDEX_DIFF:
                decorator = UIIcons.OVR_STAGED_ADD;
                break;
            case REMOVED:
            case REMOVED_NOT_STAGED:
            case REMOVED_UNTRACKED:
                decorator = UIIcons.OVR_STAGED_REMOVE;
                break;
            /*
             * case UNTRACKED_FOLDERS: other = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER); DecorationOverlayIcon decorated = new
             * DecorationOverlayIcon(other, UIIcons.OVR_UNTRACKED, IDecoration.BOTTOM_RIGHT); finalOther = (Image) this.resourceManager.get(decorated); break;
             */
            default:
                break;
            }

            if (decorator != null) {
                finalOther = getDecoratedImage(getEditorImage(item), decorator);
            } else if (finalOther == null) {
                finalOther = getEditorImage(item);
            }
            return finalOther;
        }

        @Override
        public void dispose() {
            resourceManager.dispose();
            super.dispose();
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        toolkit = new FormToolkit(parent.getDisplay());
        parent.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                toolkit.dispose();
            }
        });
        return super.createContents(parent);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        toolkit.adapt(parent, false, false);

        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.OK_LABEL, false);
        updateMessage();
    }

    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        IDialogSettings settings = Activator.getDefault().getDialogSettings();
        IDialogSettings section = settings.getSection(DIALOG_SETTINGS_SECTION_NAME);
        if (section == null)
            section = settings.addNewSection(DIALOG_SETTINGS_SECTION_NAME);
        return section;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            cancelPressed();
    }

    /**
     * Add message drop down toolbar item
     *
     * @param parent
     * @return toolbar
     */
    protected ToolBar addMessageDropDown(Composite parent) {
        final ToolBar dropDownBar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        final ToolItem dropDownItem = new ToolItem(dropDownBar, SWT.PUSH);
        dropDownItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage("IMG_LCL_RENDERED_VIEW_MENU")); //$NON-NLS-1$
        final Menu menu = new Menu(dropDownBar);
        dropDownItem.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                menu.dispose();
            }
        });
        MenuItem preferencesItem = new MenuItem(menu, SWT.PUSH);
        preferencesItem.setText("Configure link");
        preferencesItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String[] pages = new String[] { UIPreferences.PAGE_COMMIT_PREFERENCES_SUMMARY };
                Activator.getDefault().getDialogSettings();
                PreferencesUtil.createPreferenceDialogOn(getShell(), pages[0],
                        pages, null).open();
            }

        });
        dropDownItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Rectangle b = dropDownItem.getBounds();
                Point p = dropDownItem.getParent().toDisplay(
                        new Point(b.x, b.y + b.height));
                menu.setLocation(p.x, p.y);
                menu.setVisible(true);
            }

        });
        return dropDownBar;
    }

    protected String validateCommit() {
        if (previousVersionText.getText().length() == 0) {
            return "Empty or invalid older commit id";
        }

        if (currentVersionText.getText().length() == 0) {
            return "Empty or invalid newer commit id";
        }

        return "";

    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        parent.getShell().setText("Commit changes");

        container = toolkit.createComposite(container);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
        toolkit.paintBordersFor(container);
        GridLayoutFactory.swtDefaults().applyTo(container);

        sashForm = new SashForm(container, SWT.VERTICAL | SWT.FILL);
        toolkit.adapt(sashForm, true, true);
        sashForm.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
        createMessageAndPersonArea(sashForm);
        filesSection = createFileSection(sashForm);
        sashForm.setWeights(new int[] { 50, 50 });

        applyDialogFont(container);
        container.pack();
        setTitle("Commit Changes");
        setMessage("Commit message", IMessageProvider.INFORMATION);

        filesViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                updateMessage();
            }
        });

        updateFileSectionText();
        return container;
    }

    private Composite createMessageAndPersonArea(Composite container) {

        messageAndPersonArea = toolkit.createComposite(container);
        GridDataFactory.fillDefaults().grab(true, true)
                .applyTo(messageAndPersonArea);
        GridLayoutFactory.swtDefaults().margins(0, 0).spacing(0, 0)
                .applyTo(messageAndPersonArea);

        Section messageSection = toolkit.createSection(messageAndPersonArea, ExpandableComposite.TITLE_BAR | ExpandableComposite.CLIENT_INDENT);
        messageSection.setText("Commit message");
        Composite messageArea = toolkit.createComposite(messageSection);
        GridLayoutFactory.fillDefaults().spacing(0, 0).extendedMargins(2, 2, 2, 2).applyTo(messageArea);
        toolkit.paintBordersFor(messageArea);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(messageSection);
        GridLayoutFactory.swtDefaults().applyTo(messageSection);

        Composite headerArea = new Composite(messageSection, SWT.NONE);
        GridLayoutFactory.fillDefaults().spacing(0, 0).numColumns(2).applyTo(headerArea);

        ToolBar messageToolbar = new ToolBar(headerArea, SWT.FLAT | SWT.HORIZONTAL);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(true, false).applyTo(messageToolbar);
        addMessageDropDown(headerArea);

        messageSection.setTextClient(headerArea);

        // /////////////////

        Composite personArea = toolkit.createComposite(messageAndPersonArea);
        toolkit.paintBordersFor(personArea);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(personArea);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(personArea);

        toolkit.createLabel(personArea, "Older Commit ID: ").setForeground(toolkit.getColors().getColor(IFormColors.TB_TOGGLE));
        setAuthorText(toolkit.createText(personArea, null));
        getAuthorText().setText(commitPreviousID);
        getAuthorText().setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
        getAuthorText().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        toolkit.createLabel(personArea, "Newer Commit ID: ").setForeground(toolkit.getColors().getColor(IFormColors.TB_TOGGLE));
        setCommitterText(toolkit.createText(personArea, null));
        getCommitterText().setText(commitCurrentID);
        getCommitterText().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

        // /////////////////////

        JavaViewer viewer = new JavaViewer();
        viewer.setShell(getShell());
        viewer.setComposite(messageAndPersonArea);
        viewer.createStyledText();
        int minHeight = 114;
        Point size = container.getSize();
        viewer.getText().setLayoutData(GridDataFactory.fillDefaults()
                .grab(true, true).hint(size).minSize(size.x - 30, minHeight)
                .align(SWT.FILL, SWT.FILL).create());
        setEditor(viewer);
        messageSection.setClient(messageArea);

        // ///////////////////

        boolean visible = Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_COMMIT_SIGNATURE_ACTIVE);

        signatureCanvas = new SignatureCanvas(signatureMap, messageAndPersonArea, getShell());
        signatureCanvas.createContents();
        if (visible) {
            signatureCanvas.getCanvas().setLayoutData(GridDataFactory.fillDefaults()
                    .grab(true, true).hint(size).minSize(size.x, 90)
                    .align(SWT.FILL, SWT.FILL).create());
        } else {
            signatureCanvas.getCanvas().setLayoutData(GridDataFactory.fillDefaults()
                    .grab(true, true).hint(size).minSize(size.x, 0)
                    .align(SWT.FILL, SWT.FILL).create());
        }

        signatureCanvas.getCanvas().setVisible(visible);

        return messageAndPersonArea;
    }

    public void updateSignatureCanvas() {
        if (signatureMap != null) {
            signatureCanvas.setSignatureMap(signatureMap);
            signatureCanvas.redraw();
        }
    }

    /**
     * Create a help button with the help icon on it. No action is associated with the button
     * 
     * @param container
     *            parent container
     * @return created button
     */
    private ToolItem createHelpButton(ToolBar filesToolbar) {
        ToolItem help = new ToolItem(filesToolbar, SWT.PUSH);
        Image img = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_LCL_LINKTO_HELP);
        help.setImage(img);
        help.setToolTipText("Help");
        return help;
    }

    private Section createFileSection(Composite container) {
        Section filesSection = toolkit.createSection(container,
                ExpandableComposite.TITLE_BAR
                        | ExpandableComposite.CLIENT_INDENT);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(filesSection);
        Composite filesArea = toolkit.createComposite(filesSection);
        filesSection.setClient(filesArea);
        toolkit.paintBordersFor(filesArea);
        GridLayoutFactory.fillDefaults().extendedMargins(2, 2, 2, 2).applyTo(filesArea);

        ToolBar filesToolbar = new ToolBar(filesSection, SWT.FLAT);

        filesSection.setTextClient(filesToolbar);

        PatternFilter patternFilter = new PatternFilter() {
            @Override
            protected boolean isLeafMatch(Viewer viewer, Object element) {
                if (element instanceof ChangedFile) {
                    ChangedFile commitItem = (ChangedFile) element;
                    return wordMatches(commitItem.getPath());
                }
                return super.isLeafMatch(viewer, element);
            }
        };
        patternFilter.setIncludeLeadingWildcard(true);
        FilteredCheckboxTree resourcesTreeComposite = new FilteredCheckboxTree(filesArea, toolkit, SWT.FULL_SELECTION, patternFilter);
        Tree resourcesTree = resourcesTreeComposite.getViewer().getTree();
        resourcesTree.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
        resourcesTreeComposite.setLayoutData(GridDataFactory.fillDefaults().hint(600, 230).grab(true, true).create());

        resourcesTree.setHeaderVisible(true);
        TreeColumn statCol = new TreeColumn(resourcesTree, SWT.LEFT);
        statCol.setText("Status");
        statCol.setWidth(150);

        TreeColumn resourceCol = new TreeColumn(resourcesTree, SWT.LEFT);
        resourceCol.setText("Path");
        resourceCol.setWidth(415);

        filesViewer = resourcesTreeComposite.getCheckboxTreeViewer();
        new TreeViewerColumn(filesViewer, statCol).setLabelProvider(createStatusLabelProvider());
        new TreeViewerColumn(filesViewer, resourceCol).setLabelProvider(new CommitPathLabelProvider());
        ColumnViewerToolTipSupport.enableFor(filesViewer);
        filesViewer.setContentProvider(new CommitFileContentProvider());
        filesViewer.setUseHashlookup(true);
        if (items != null) {
            filesViewer.setInput(items.toArray());
        }

        filesViewer.addCheckStateListener(new ICheckStateListener() {

            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                updateFileSectionText();
            }
        });

        ToolItem help = createHelpButton(filesToolbar);
        help.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                InformationDialog dialog = new InformationDialog(getShell());
                dialog.create();
                dialog.open();
            }
        });

        ToolItem computeModificationsItem = new ToolItem(filesToolbar, SWT.PUSH);
        Image describeImage2 = UIIcons.ANNOTATE.createImage();
        computeModificationsItem.setImage(describeImage2);
        computeModificationsItem.setToolTipText("Compute modifications");
        computeModificationsItem.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                if (!"".equals(validateCommit())) {
                    MessageDialog.openWarning(getShell(), "Error", validateCommit());
                } else {
                    computeModifications();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {

            }
        });

        ToolItem describeChangesItem = new ToolItem(filesToolbar, SWT.PUSH);
        Image describeImage = UIIcons.ANNOTATE.createImage();
        describeChangesItem.setImage(describeImage);
        describeChangesItem.setToolTipText("Describe changes");
        describeChangesItem.addSelectionListener(new SummarizeVersionChangesListener(this));

        ToolItem checkAllItem = new ToolItem(filesToolbar, SWT.PUSH);
        Image checkImage = UIIcons.CHECK_ALL.createImage();
        checkAllItem.setImage(checkImage);
        checkAllItem.setToolTipText("Select All");
        checkAllItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                filesViewer.setAllChecked(true);
                updateFileSectionText();
                updateMessage();
            }

        });

        ToolItem uncheckAllItem = new ToolItem(filesToolbar, SWT.PUSH);
        Image uncheckImage = UIIcons.UNCHECK_ALL.createImage();
        uncheckAllItem.setImage(uncheckImage);
        uncheckAllItem.setToolTipText("Deselect All");
        uncheckAllItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                filesViewer.setAllChecked(false);
                updateFileSectionText();
                updateMessage();
            }

        });

        statCol.pack();
        resourceCol.pack();
        return filesSection;
    }

    private void computeModifications() {
        ObjectId currentId = null;
        try {
            currentId = git.getRepository()
                    .resolve(getCommitterText().getText() + "^{tree}");

            ObjectId oldId = git.getRepository().resolve(
                    getAuthorText().getText() + "^{tree}");

            ObjectReader reader = git.getRepository().newObjectReader();

            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();

            if (oldId != null || currentId != null) {
                oldTreeIter.reset(reader, oldId);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                newTreeIter.reset(reader, currentId);

                List<DiffEntry> diffs = git.diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
                Set<ChangedFile> differences = new TreeSet<ChangedFile>();
                String projectName = ProjectInformation.getProject(ProjectInformation.getSelectedProject()).getName();

                // rename detector
                TreeWalk tw = new TreeWalk(git.getRepository());
                tw.setRecursive(true);
                tw.addTree(oldTreeIter);
                tw.addTree(newTreeIter);

                RenameDetector rd = new RenameDetector(git.getRepository());
                rd.addAll(diffs);

                List<DiffEntry> lde = rd.compute(tw.getObjectReader(), null);

                List<DiffEntry> finalChanges = cleanRenamed(diffs, lde);

                for (DiffEntry diffEntry : finalChanges) {

                    ChangedFile changedFile = new ChangedFile();

                    String name = (diffEntry.getNewPath() != null) ? getFileName(diffEntry.getNewPath()) : getFileName(diffEntry.getOldPath());

                    changedFile.setName(name);
                    changedFile.setAbsolutePath(diffEntry.getNewPath() != null ? projectName + "/" + diffEntry.getNewPath() : projectName + "/" + diffEntry.getOldPath());
                    changedFile.setPath(changedFile.getAbsolutePath());
                    if ("RENAME".equals(diffEntry.getChangeType().name())) {
                        changedFile.setRenamedPath(diffEntry.getOldPath());
                        changedFile.setRenamed(true);
                        changedFile.setChangeType(TypeChange.ADDED.name());
                        changedFile.setTypeChange(TypeChange.ADDED);
                    } else if ("MODIFY".equals(diffEntry.getChangeType().name())) {
                        changedFile.setRenamed(false);
                        changedFile.setChangeType(TypeChange.MODIFIED.name());
                        changedFile.setTypeChange(TypeChange.MODIFIED);
                    } else if ("ADD".equals(diffEntry.getChangeType().name())) {
                        changedFile.setRenamed(false);
                        changedFile.setChangeType(TypeChange.ADDED.name());
                        changedFile.setTypeChange(TypeChange.ADDED);
                    } else if ("REMOVE".equals(diffEntry.getChangeType().name()) || "DELETE".equals(diffEntry.getChangeType().name())) {
                        changedFile.setRenamed(false);
                        changedFile.setChangeType(TypeChange.REMOVED.name());
                        changedFile.setTypeChange(TypeChange.REMOVED);

                        changedFile.setAbsolutePath(projectName + "/" + diffEntry.getOldPath());
                        changedFile.setPath(changedFile.getAbsolutePath());
                    }

                    if (changedFile.getTypeChange() != null) {
                        differences.add(changedFile);
                    }

                    System.out.println(changedFile.toString());
                }

                for (DiffEntry de : lde) {
                    if (de.getScore() >= rd.getRenameScore()) {
                        System.out.println("file: " + de.getOldPath() + " copied/moved to: " + de.getNewPath());
                    }
                }
                Sets.intersection(new HashSet<DiffEntry>(diffs), new HashSet<DiffEntry>(lde));

                listSelectionDialog = new ListSelectionDialog(this.shellTmp.getShell(), differences,
                        new ArrayContentProvider(),
                        new LabelProvider(), "Changes");

                items = differences;

                if (items != null) {
                    filesViewer.setInput(items.toArray());
                }
            } else {
                MessageDialog.openWarning(getShell(), "Error", "The older or newer commit id is not valid!");
            }

        } catch (RevisionSyntaxException | IOException | GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private List<DiffEntry> cleanRenamed(List<DiffEntry> diffs, List<DiffEntry> lde) {
        List<DiffEntry> finalList = new ArrayList<>(diffs);
        int i = 0;
        for (DiffEntry diffEntry : diffs) {
            DiffEntry verifyRenamed = verifyRenamed(diffEntry, lde);
            if (verifyRenamed != null) {
                if (!finalList.contains(verifyRenamed)) {
                    diffEntry = verifyRenamed;
                    finalList.set(i, verifyRenamed);
                } else {
                    // finalList.remove(diffEntry);
                }

            }
            i++;
        }

        return finalList;
    }

    private DiffEntry verifyRenamed(DiffEntry diffEntry, List<DiffEntry> lde) {
        DiffEntry renamedEntry = null;
        for (DiffEntry diffEntryRenamed : lde) {
            if (diffEntryRenamed.getScore() >= 90) {
                if ("ADD".equals(diffEntry.getChangeType().name())) {
                    if (diffEntry.getNewPath().equals(diffEntryRenamed.getNewPath())) {
                        renamedEntry = diffEntryRenamed;
                        break;
                    }
                } else if ("REMOVE".equals(diffEntry.getChangeType().name())) {
                    if (diffEntry.getOldPath().equals(diffEntryRenamed.getOldPath())) {
                        renamedEntry = diffEntryRenamed;
                        break;
                    }
                } else if ("RENAME".equals(diffEntry.getChangeType().name())) {
                    if (diffEntry.getOldPath().equals(diffEntryRenamed.getOldPath())) {
                        renamedEntry = diffEntryRenamed;
                        break;
                    }
                }
            }

        }
        return renamedEntry;
    }

    public static String getFileName(String absolutePath) {
        return absolutePath.substring(absolutePath.lastIndexOf("/") + 1, absolutePath.length());
    }

    private void updateFileSectionText() {
        filesSection.setText(MessageFormat.format("Modified files (selected {0} of {1})",
                Integer.valueOf(filesViewer.getCheckedElements().length),
                Integer.valueOf(filesViewer.getTree().getItemCount())));
    }

    private static CellLabelProvider createStatusLabelProvider() {
        CommitStatusLabelProvider baseProvider = new CommitStatusLabelProvider();
        ProblemLabelDecorator decorator = new ProblemLabelDecorator(null);
        return new DecoratingStyledCellLabelProvider(baseProvider, decorator, null) {
            @Override
            public String getToolTipText(Object element) {
                return ((ChangedFile) element).getChangeType();
            }
        };
    }

    public void updateMessage() {

        String message = null;
        int type = IMessageProvider.NONE;

        String commitMsg = getEditor().getText().getText().toString();
        if (commitMsg == null || commitMsg.trim().length() == 0) {
            message = "Empty message";
            type = IMessageProvider.INFORMATION;
        } else if (!isCommitWithoutFilesAllowed()) {
            message = "No files selected";
            type = IMessageProvider.INFORMATION;
        } else {
            // CommitStatus status = commitMessageComponent.getStatus();
            // message = status.getMessage();
            // type = status.getMessageType();
        }

        setMessage(message, type);
    }

    private boolean isCommitWithoutFilesAllowed() {
        if (filesViewer.getCheckedElements().length > 0) {
            return true;
        } else {
            return false;
        }
    }

    public ChangedFile[] getSelectedFiles() {
        return Arrays.copyOf(filesViewer.getCheckedElements(), filesViewer.getCheckedElements().length, ChangedFile[].class);
    }

    public void initStyles() {
        Color orange, blue;
        orange = new Color(getShell().getDisplay(), 255, 127, 0);
        blue = getShell().getDisplay().getSystemColor(SWT.COLOR_BLUE);

        Font font = new Font(getShell().getDisplay(), "Courier", 10, SWT.NORMAL);
        getText().setFont(font);

        StyleRange range1 = new StyleRange(0, 4, orange, null);
        range1.fontStyle = SWT.BOLD;
        getText().setStyleRange(range1);

        StyleRange range2 = new StyleRange(5, 2, blue, null);
        range2.background = getShell().getDisplay().getSystemColor(SWT.COLOR_YELLOW);

        getText().setStyleRange(range2);

    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }

    public StyledText getText() {
        return text;
    }

    public void setText(StyledText text) {
        this.text = text;
    }

    public IJavaProject getSelection() {
        return selection;
    }

    public void setSelection(IJavaProject selection) {
        this.selection = selection;
    }

    public JavaViewer getEditor() {
        return editor;
    }

    public void setEditor(JavaViewer editor) {
        this.editor = editor;
    }

    public ListSelectionDialog getListSelectionDialog() {
        return listSelectionDialog;
    }

    public void setListSelectionDialog(ListSelectionDialog listSelectionDialog) {
        this.listSelectionDialog = listSelectionDialog;
    }

    public TreeMap<MethodStereotype, Integer> getSignatureMap() {
        return signatureMap;
    }

    public void setSignatureMap(TreeMap<MethodStereotype, Integer> signatureMap) {
        this.signatureMap = signatureMap;
    }

    public Text getAuthorText() {
        return previousVersionText;
    }

    public void setAuthorText(Text authorText) {
        this.previousVersionText = authorText;
    }

    public Text getCommitterText() {
        return currentVersionText;
    }

    public void setCommitterText(Text committerText) {
        this.currentVersionText = committerText;
    }
}
