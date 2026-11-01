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
                        "tour.welcomeTitle",
                        "tour.welcomeText",
                        sideNavbar::getBrandSection,
                        () -> navigateTo(Route.LIBRARY),
                        false,
                        new Tour.StepOptions(null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "tour.libraryTitle",
                        "tour.libraryText",
                        () -> viewRouter,
                        () -> navigateTo(Route.LIBRARY),
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("library"))),
                new Tour.Step(
                        "tour.createProjectTitle",
                        "tour.createProjectText",
                        sideNavbar::getNewProjectButton,
                        modal::hide,
                        true),
                new Tour.Step(
                        "tour.addTextTitle",
                        "tour.addTextText",
                        newProjectModal::getSourceToggle,
                        this::showProjectForm,
                        true),
                new Tour.Step(
                        "tour.projectNameTitle",
                        "tour.projectNameText",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("title"),
                        null,
                        true),
                new Tour.Step(
                        "tour.fileNameTitle",
                        "tour.fileNameText",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("fileName"),
                        null,
                        true),
                new Tour.Step(
                        "tour.pasteTextTitle",
                        "tour.pasteTextText",
                        () -> newProjectModal.getPasteForm().getForm().getFieldNode("content"),
                        null,
                        true),
                new Tour.Step(
                        "tour.workspaceTitle",
                        "tour.workspaceText",
                        () -> newProjectModal.getDestinationToggle(SourceType.PASTE),
                        null,
                        true),
                new Tour.Step(
                        "tour.createTitle",
                        "tour.createText",
                        () -> newProjectModal.getPasteForm().getForm().getSubmitButton(),
                        null,
                        true),
                new Tour.Step(
                        "tour.readerTitle",
                        "tour.readerText",
                        () -> analyseScreen().getDocumentView(),
                        this::openAnalysis,
                        false),
                new Tour.Step(
                        "tour.quotationTitle",
                        "tour.quotationText",
                        () -> analyseScreen().getDocumentView(),
                        () -> {
                            openAnalysis();
                            showQuotationPreview();
                        },
                        true,
                        new Tour.StepOptions(0.5)),
                new Tour.Step(
                        "tour.quotationsTitle",
                        "tour.quotationsText",
                        () -> analyseScreen().getHeader().getQuotationsButton(),
                        null,
                        true,
                        new Tour.StepOptions(
                                null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "tour.analyzeTitle",
                        "tour.analyzeText",
                        () -> analyseScreen().getStatisticsSidebar(),
                        this::clearQuotationPreview,
                        true),
                new Tour.Step(
                        "tour.frequentWordsTitle",
                        "tour.frequentWordsText",
                        () -> analyseScreen().getTextTermFrequencyPanel(),
                        null,
                        true),
                new Tour.Step(
                        "tour.searchTitle",
                        "tour.searchText",
                        () -> analyseScreen().getSearchView(),
                        null,
                        true),
                new Tour.Step(
                        "tour.keyParagraphsTitle",
                        "tour.keyParagraphsText",
                        () -> analyseScreen().getTextContextPanel(),
                        null,
                        true),
                new Tour.Step(
                        "tour.saveAnalysisTitle",
                        "tour.saveAnalysisText",
                        () -> analyseScreen().getHeader().getSaveButton(),
                        this::openAnalysis,
                        true),
                new Tour.Step(
                        "tour.compareTitle",
                        "tour.compareText",
                        () -> compareScreen().getDocumentView(),
                        this::openComparison,
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("compare"))),
                new Tour.Step(
                        "tour.comparisonTitle",
                        "tour.comparisonText",
                        () -> compareScreen().getHeader(),
                        null,
                        true,
                        new Tour.StepOptions(null, Tour.TooltipPosition.BELOW)),
                new Tour.Step(
                        "tour.sharedWordsTitle",
                        "tour.sharedWordsText",
                        () -> compareScreen().getComparisonSidePanel().getCommonWordsPanel(),
                        null,
                        true),
                new Tour.Step(
                        "tour.searchBothTitle",
                        "tour.searchBothText",
                        () -> compareScreen().getComparisonSidePanel().getSearchView(),
                        null,
                        true),
                new Tour.Step(
                        "tour.similarParagraphsTitle",
                        "tour.similarParagraphsText",
                        () -> compareScreen().getComparisonSidePanel().getSimilarParagraphsPanel(),
                        null,
                        true),
                new Tour.Step(
                        "tour.saveComparisonTitle",
                        "tour.saveComparisonText",
                        () -> compareScreen().getHeader().getSaveButton(),
                        null,
                        true),
                new Tour.Step(
                        "archive",
                        "tour.archiveText",
                        () -> viewRouter,
                        this::showArchivePreview,
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("archive"))),
                new Tour.Step(
                        "settings",
                        "tour.settingsText",
                        () -> viewRouter,
                        () -> navigateTo(Route.SETTINGS),
                        true,
                        new Tour.StepOptions(
                                () -> sideNavbar.getNavigation().getButton("settings"))),
                new Tour.Step(
                        "profile",
                        "tour.profileText",
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
