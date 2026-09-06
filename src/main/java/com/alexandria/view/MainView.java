package com.alexandria.view;

import com.alexandria.view.components.side_navbar.SideNavbar;
import com.alexandria.view.router.Route;
import com.alexandria.view.router.ViewRouter;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class MainView extends StackPane {
    private final SideNavbar sideNavbar;
    private final ViewRouter viewRouter;

    public MainView() {
        sideNavbar = new SideNavbar();
        viewRouter = new ViewRouter();

        BorderPane shell = new BorderPane();
        shell.setLeft(sideNavbar);
        shell.setCenter(viewRouter);
        getChildren().add(shell);

        configureNavigation();
        navigateTo(Route.LIBRARY);
    }

    private void configureNavigation() {
        sideNavbar.setOnNavigate(id -> navigateTo(Route.fromId(id)));
        sideNavbar.setOnFooterNavigate(id -> navigateTo(Route.fromId(id)));
        sideNavbar.setOnNewProject(() -> {
            // TODO
        });
    }

    private void navigateTo(Route route) {
        viewRouter.navigateTo(route);
        sideNavbar.selectItem(route.id());
    }

    public SideNavbar getSideNavbar() {
        return sideNavbar;
    }

    public ViewRouter getViewRouter() {
        return viewRouter;
    }
}
