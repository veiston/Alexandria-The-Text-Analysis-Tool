package com.alexandria.view;

import com.alexandria.view.components.side_navbar.SideNavbar;
import com.alexandria.view.router.Route;
import com.alexandria.view.router.ViewRouter;

import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final SideNavbar sideNavbar;
    private final ViewRouter viewRouter;

    public MainView() {
        sideNavbar = new SideNavbar();
        viewRouter = new ViewRouter();

        setLeft(sideNavbar);
        setCenter(viewRouter);

        configureNavigation();

        // Initial screen
        navigateTo(Route.LIBRARY);
    }

    private void configureNavigation() {

        // Main navigation
        sideNavbar.setOnNavigate(id -> {
            navigateTo(Route.fromId(id));
        });

        // Footer navigation
        sideNavbar.setOnFooterNavigate(id -> {
            navigateTo(Route.fromId(id));
        });

        // New project action
        sideNavbar.setOnNewProject(() -> {
            // TODO: Implement the logic to open the new project dialog/screen
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
