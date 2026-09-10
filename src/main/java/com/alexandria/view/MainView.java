package com.alexandria.view;

import java.util.function.Consumer;

import com.alexandria.view.components.side_navbar.SideNavbar;
import com.alexandria.view.components.shared.modal.Modal;
import com.alexandria.view.components.side_navbar.new_project.NewProjectModal;
import com.alexandria.view.router.Route;
import com.alexandria.view.router.ViewRouter;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends StackPane {

    private final SideNavbar sideNavbar;
    private final ViewRouter viewRouter;

    private final Modal modal = new Modal();
    private final NewProjectModal newProjectModal = new NewProjectModal();

    public MainView() {
        sideNavbar = new SideNavbar();
        viewRouter = new ViewRouter();

        BorderPane shell = new BorderPane();
        shell.setLeft(sideNavbar);
        shell.setCenter(viewRouter);

        getChildren().addAll(shell, modal);

        configureNavigation();

        viewRouter.navigateTo(Route.LIBRARY);
    }

    private void configureNavigation() {
        sideNavbar.setOnNavigate(id -> navigateAndSync(Route.fromId(id)));

        sideNavbar.setOnFooterNavigate(id -> navigateAndSync(Route.fromId(id)));

        sideNavbar.setOnNewProject(this::showNewProjectModal);
    }

    private void navigateAndSync(Route route) {
        viewRouter.navigateTo(route);
        sideNavbar.selectItem(route.id());
    }

    private void showNewProjectModal() {
        newProjectModal.reset();
        modal.show(newProjectModal);
    }

    public void closeProjectModal() {
        modal.hide();
    }

    /* New Project */

    public void setOnProjectCreated(
            Consumer<NewProjectModal.CreatedProject> handler) {

        newProjectModal.setOnCreated(handler);
    }

    public void setNewProjectLoading(boolean loading) {
        newProjectModal.setLoading(loading);
    }

    public void showProjectError(String message) {
        newProjectModal.showError(message);
    }

    /* Navigation */

    public void navigateTo(Route route) {
        navigateAndSync(route);
    }

    public SideNavbar getSideNavbar() {
        return sideNavbar;
    }

    public ViewRouter getViewRouter() {
        return viewRouter;
    }
}
