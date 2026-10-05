package com.alexandria.view.components.user_guide;

import java.util.List;

import com.alexandria.view.components.shared.modal.Modal;
import com.alexandria.view.components.shared.document.highlight.TxtHighlight;
import com.alexandria.view.components.shared.document.highlight.TxtTextLayout;
import com.alexandria.view.components.shared.tour.Tour;
import com.alexandria.view.components.side_navbar.SideNavbar;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal.SourceType;
import com.alexandria.view.router.Route;
import com.alexandria.view.router.ViewRouter;
import com.alexandria.view.screens.AnalyseScreen;
import com.alexandria.view.screens.ArchiveScreen;
import com.alexandria.view.screens.CompareScreen;
import com.alexandria.utils.UserGuideSettings;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UserGuideTour {

    public static final EventType<Event> OPEN_ANALYSIS_EVENT = new EventType<>(Event.ANY, "OPEN_ANALYSIS");
    public static final EventType<Event> CLOSE_ANALYSIS_EVENT = new EventType<>(Event.ANY, "CLOSE_ANALYSIS");
    public static final EventType<Event> OPEN_COMPARISON_EVENT = new EventType<>(Event.ANY, "OPEN_COMPARISON");
    public static final EventType<Event> CLOSE_COMPARISON_EVENT = new EventType<>(Event.ANY, "CLOSE_COMPARISON");
    public static final EventType<Event> CLOSE_ARCHIVE_EVENT = new EventType<>(Event.ANY, "CLOSE_ARCHIVE");

    private final Tour tour;
    private final SideNavbar sideNavbar;
    private final ViewRouter viewRouter;
    private final Modal modal;
    private final NewProjectModal newProjectModal;

    private boolean analysisOpen;
    private boolean comparisonOpen;
    private Path quotationPreview;

    public UserGuideTour(
            Tour tour,
            SideNavbar sideNavbar,
            ViewRouter viewRouter,
            Modal modal,
            NewProjectModal newProjectModal) {
        this.tour = tour;
        this.sideNavbar = sideNavbar;
        this.viewRouter = viewRouter;
        this.modal = modal;
        this.newProjectModal = newProjectModal;
    }

    public void start() {
        tour.start(steps(), this::endTour);
    }

    private List<Tour.Step> steps() {
        return List.of(
                new Tour.Step(
                        "Welcome to Alexandria!",
                        "Alexandria is a statistical text analysis tool for language researchers. "
                                + "It can help you explore texts through word frequencies, term searches, "
                                + "key paragraphs, quotations, and comparisons.\n\n"
                                + "Take a quick tour through the application to get familiar with its main "
                                + "features. You can return to this tutorial at any time from the User Guide page.",
                        sideNavbar::getBrandSection,
                        () -> navigateTo(Route.LIBRARY),
                        false,
                        new Tour.StepOptions(null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "Your Library",
                        "This is the first page of the application. If you already have saved projects, you can find them here. Open any project to continue working with it.",
                        () -> viewRouter,
                        () -> navigateTo(Route.LIBRARY),
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("library"))),
                new Tour.Step(
                        "Create a project",
                        "If you do not have a project yet, or want to create a new one, click + New Project in the sidebar.",
                        sideNavbar::getNewProjectButton,
                        modal::hide,
                        true),
                new Tour.Step(
                        "Choose how to add your text",
                        "Upload a PDF or TXT file, or choose Paste Text to enter the text manually.",
                        newProjectModal::getSourceToggle,
                        this::showProjectForm,
                        true),
                new Tour.Step(
                        "Name your project",
                        "Add a clear project name so you can recognise it later in Library.",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("title"),
                        null,
                        true),
                new Tour.Step(
                        "Choose a file name",
                        "This name is optional. Leave it blank to use the original file name.",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("fileName"),
                        null,
                        true),
                new Tour.Step(
                        "Add your text",
                        "Paste or write the text you want to analyse in this field.",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("content"),
                        null,
                        true),
                new Tour.Step(
                        "Choose how to work with your text",
                        "Choose Analyse to work with one text, or Compare to work with several texts.",
                        () -> newProjectModal.getDestinationToggle(SourceType.PASTE),
                        null,
                        true),
                new Tour.Step(
                        "Create your project",
                        "When you are ready, click Create Project to add your text and open it in the selected workspace.",
                        () -> newProjectModal.getPasteForm().getForm().getSubmitButton(),
                        null,
                        true),
                new Tour.Step(
                        "Read your text",
                        "Your text opens in the reader. Use the controls below to zoom in or out and move between pages while you analyse it.",
                        () -> analyseScreen().getDocumentView(),
                        this::openAnalysis,
                        false),
                new Tour.Step(
                        "Create a quotation",
                        "Select a passage you want to keep to make it a quotation. This helps you collect important excerpts while you read. You need to be signed in to save quotations to your account.",
                        () -> analyseScreen().getDocumentView(),
                        () -> {
                            openAnalysis();
                            showQuotationPreview();
                        },
                        true,
                        new Tour.StepOptions(0.5)),
                new Tour.Step(
                        "View your quotations",
                        "Click Quotations to review the passages you have saved as quotations.",
                        () -> analyseScreen().getHeader().getQuotationsButton(),
                        null,
                        true,
                        new Tour.StepOptions(
                                null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "Analyze your text",
                        "The panel on the right brings together the analysis results for your text. Alexandria counts words and their uses to create these results. Here you can review frequent words, analyse a word or phrase, and find key paragraphs.",
                        () -> analyseScreen().getStatisticsSidebar(),
                        this::clearQuotationPreview,
                        true),
                new Tour.Step(
                        "Review frequent words",
                        "This list shows the five words used most often in the text. Common words such as “the” and “and” are ignored. Click on a word to see its occurrences, frequency, nearby words, and every place where it appears.",
                        () -> analyseScreen().getTextTermFrequencyPanel(),
                        null,
                        true),
                new Tour.Step(
                        "Search and track terms",
                        "Search for a word or phrase in the current text. Use Fuzzy to find approximate matches, Case sensitive to match capitalisation, Whole word to exclude partial matches, or Stop words to exclude common words. Your searches appear in Tracked Words, where you can reopen a term, move through its matches, or remove it from the list.",
                        () -> analyseScreen().getSearchView(),
                        null,
                        true),
                new Tour.Step(
                        "Review key paragraphs",
                        "Key Paragraphs identifies sentences that contain words used frequently across the text. This can help you notice repeated topics. Use Go to button to open the relevant page in the reader.",
                        () -> analyseScreen().getTextContextPanel(),
                        null,
                        true),
                new Tour.Step(
                        "Save your analysis",
                        "Use Save Findings button to keep the current results of a text analysis, term search, or comparison in Archive. You need to be signed in to save findings.",
                        () -> analyseScreen().getHeader().getSaveButton(),
                        this::openAnalysis,
                        true),
                new Tour.Step(
                        "Compare texts",
                        "Choose at least two texts to compare. Each text can be a PDF, TXT file, or pasted text, and you can combine different formats in one comparison. The selected texts are shown side by side.",
                        () -> compareScreen().getDocumentView(),
                        this::openComparison,
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("compare"))),
                new Tour.Step(
                        "Review the comparison",
                        "The similarity score in the header summarises how alike the word-frequency profiles of the two texts are.",
                        () -> compareScreen().getHeader(),
                        null,
                        true,
                        new Tour.StepOptions(null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "Review shared words",
                        "Most frequent shared words lists vocabulary found in both texts and shows each word's count in Doc A and Doc B. Select a word to search for it in both documents.",
                        () -> compareScreen().getComparisonSidePanel().getCommonWordsPanel(),
                        null,
                        true),
                new Tour.Step(
                        "Search both texts",
                        "Search for a word or phrase across both documents. Fuzzy finds approximate matches, Case sensitive matches capitalisation, Whole word excludes partial matches, and Stop words excludes common words. Tracked Words keeps separate result rows for Doc A and Doc B.",
                        () -> compareScreen().getComparisonSidePanel().getSearchView(),
                        null,
                        true),
                new Tour.Step(
                        "Review similar paragraphs",
                        "Key Paragraphs shows the strongest matching passages from the two documents. Select a passage preview to open its location in the corresponding reader.",
                        () -> compareScreen().getComparisonSidePanel().getSimilarParagraphsPanel(),
                        null,
                        true),
                new Tour.Step(
                        "Save comparison findings",
                        "Use Save Findings to store the comparison and any tracked search terms in Archive. You need to be signed in, and both texts must be saved in your Library.",
                        () -> compareScreen().getHeader().getSaveButton(),
                        null,
                        true),
                new Tour.Step(
                        "Archive",
                        "Open Archive to view the the results of a text analysis, term search, or comparison you saved while working with your texts.",
                        () -> viewRouter,
                        this::showArchivePreview,
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("archive"))),
                new Tour.Step(
                        "Settings",
                        "Open Settings to manage application preferences.",
                        () -> viewRouter,
                        () -> navigateTo(Route.SETTINGS),
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("settings"))),
                new Tour.Step(
                        "Profile",
                        "Use Profile page to create an account, sign in, or edit your profile if you are already signed in.",
                        () -> viewRouter,
                        () -> navigateTo(Route.PROFILE),
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("profile"))));
    }

    private void openAnalysis() {
        modal.hide();
        analysisOpen = true;
        tour.fireEvent(new Event(OPEN_ANALYSIS_EVENT));
    }

    private void openComparison() {
        modal.hide();
        comparisonOpen = true;
        tour.fireEvent(new Event(OPEN_COMPARISON_EVENT));
    }

    private void showProjectForm() {
        newProjectModal.reset();
        modal.show(newProjectModal);
        newProjectModal.getSourceToggle().getButton(1).fire();

        var form = newProjectModal.getPasteForm().getForm();
        form.setValue("title", UserGuideTourData.PROJECT_TITLE);
        form.setValue("fileName", UserGuideTourData.FILE_NAME);
        form.setValue("content", UserGuideTourData.TEXT);
    }

    private void showArchivePreview() {
        navigateTo(Route.ARCHIVE);
        ArchiveScreen archiveScreen = (ArchiveScreen) Route.ARCHIVE.createScreen();
        Platform.runLater(() -> {
            archiveScreen.setTextAnalyses(UserGuideTourData.archiveExamples());
            archiveScreen.setTermAnalyses(List.of());
        });
    }

    private void showQuotationPreview() {
        clearQuotationPreview();

        Platform.runLater(() -> {
            Node node = analyseScreen().getDocumentView().lookup(".document-page-text");
            if (!(node instanceof TextFlow flow) || flow.getChildren().isEmpty()) {
                return;
            }

            Text text = (Text) flow.getChildren().get(0);
            int start = text.getText().indexOf(UserGuideTourData.QUOTATION);
            if (start < 0) {
                return;
            }

            quotationPreview = TxtTextLayout.shapeFor(
                    flow,
                    start,
                    start + UserGuideTourData.QUOTATION.length(),
                    TxtHighlight.QUOTATION_STYLE_CLASS);

            Point2D point = analyseScreen().sceneToLocal(flow.localToScene(0, 0));
            quotationPreview.setTranslateX(point.getX());
            quotationPreview.setTranslateY(point.getY());
            analyseScreen().getChildren().add(quotationPreview);
        });
    }

    private void clearQuotationPreview() {
        if (quotationPreview != null) {
            analyseScreen().getChildren().remove(quotationPreview);
            quotationPreview = null;
        }
    }

    private void endTour() {
        UserGuideSettings.setShownOnStartup(false);
		
        clearQuotationPreview();
        tour.fireEvent(new Event(CLOSE_ARCHIVE_EVENT));
        if (analysisOpen) {
            tour.fireEvent(new Event(CLOSE_ANALYSIS_EVENT));
            analysisOpen = false;
        }
        if (comparisonOpen) {
            tour.fireEvent(new Event(CLOSE_COMPARISON_EVENT));
            comparisonOpen = false;
        }
        newProjectModal.reset();
        modal.hide();
        navigateTo(Route.LIBRARY);
    }

    private AnalyseScreen analyseScreen() {
        return (AnalyseScreen) Route.ANALYZE.createScreen();
    }

    private CompareScreen compareScreen() {
        return (CompareScreen) Route.COMPARE.createScreen();
    }

    private void navigateTo(Route route) {
        viewRouter.navigateTo(route);
        sideNavbar.selectItem(route.id());
    }
}
