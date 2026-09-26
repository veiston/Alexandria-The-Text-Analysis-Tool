package com.alexandria.view.components.shared.tour;

import java.util.List;
import java.util.function.Supplier;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;

public class Tour extends StackPane {

    public static final EventType<Event> START_EVENT = new EventType<>(Event.ANY, "START_TOUR");

    public record Step(
            String title,
            String description,
            Supplier<Node> target,
            Runnable beforeShow,
            boolean isBackAllowed,
            StepOptions options) {

        public Step(
                String title,
                String description,
                Supplier<Node> target,
                Runnable beforeShow,
                boolean isBackAllowed) {
            this(title, description, target, beforeShow, isBackAllowed, StepOptions.DEFAULT);
        }

        public Step {
            options = options == null ? StepOptions.DEFAULT : options;
        }
    }

    public record StepOptions(
            Supplier<Node> tooltipAnchor,
            double heightRatio,
            TooltipPosition tooltipPosition) {

        public static final StepOptions DEFAULT = new StepOptions(null, 1, TooltipPosition.RIGHT);

        public StepOptions(Supplier<Node> tooltipAnchor) {
            this(tooltipAnchor, 1, TooltipPosition.RIGHT);
        }

        public StepOptions(double heightRatio) {
            this(null, heightRatio, TooltipPosition.RIGHT);
        }

        public StepOptions(Supplier<Node> tooltipAnchor, TooltipPosition tooltipPosition) {
            this(tooltipAnchor, 1, tooltipPosition);
        }

        public StepOptions {
            heightRatio = Math.max(0, Math.min(1, heightRatio));
            tooltipPosition = tooltipPosition == null ? TooltipPosition.RIGHT : tooltipPosition;
        }
    }

    public enum TooltipPosition { RIGHT, BELOW }

    private final Path dimOverlay = new Path();
    private final Rectangle inputBlocker = new Rectangle();
    private final Rectangle spotlight = new Rectangle();
    private final Rectangle secondarySpotlight = new Rectangle();
    private final VBox tooltip = new VBox(10);
    private final Label stepLabel = new Label();
    private final Label titleLabel = new Label();
    private final Label descriptionLabel = new Label();
    private final Button backButton = new Button("Back");
    private final Button nextButton = new Button("Next");
    private final Button skipButton = new Button("Skip tour");

    private List<Step> steps = List.of();
    private int currentStep;
    private Runnable onClose = () -> {};
    private final EventHandler<KeyEvent> keyEventBlocker = KeyEvent::consume;
    private boolean keyboardBlocked;

    public Tour() {
        getStyleClass().add("tour");
        setVisible(false);
        setManaged(false);
        setMinSize(0, 0);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setPickOnBounds(true);

        dimOverlay.getStyleClass().add("tour-overlay");
        dimOverlay.setManaged(false);
        dimOverlay.setMouseTransparent(true);
        dimOverlay.setFillRule(FillRule.EVEN_ODD);

        inputBlocker.setManaged(false);
        inputBlocker.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.001));
        inputBlocker.widthProperty().bind(widthProperty());
        inputBlocker.heightProperty().bind(heightProperty());
        inputBlocker.addEventFilter(MouseEvent.ANY, MouseEvent::consume);

        spotlight.getStyleClass().add("tour-spotlight");
        spotlight.setManaged(false);
        spotlight.setMouseTransparent(true);

        secondarySpotlight.getStyleClass().add("tour-spotlight");
        secondarySpotlight.setManaged(false);
        secondarySpotlight.setMouseTransparent(true);
        secondarySpotlight.setVisible(false);

        configureTooltip();
        getChildren().addAll(
                dimOverlay, inputBlocker, spotlight, secondarySpotlight, tooltip);
    }

    private void configureTooltip() {
        tooltip.getStyleClass().add("tour-tooltip");
        tooltip.setManaged(false);
        tooltip.setMouseTransparent(false);
        tooltip.setPrefWidth(300);

        stepLabel.getStyleClass().add("tour-step");
        titleLabel.getStyleClass().add("heading-md");
        descriptionLabel.getStyleClass().add("tour-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMinWidth(0);
        descriptionLabel.setMaxWidth(Double.MAX_VALUE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        backButton.getStyleClass().addAll("button", "secondary");
        nextButton.getStyleClass().addAll("button", "primary");
        nextButton.getStyleClass().add("tour-next");
        skipButton.getStyleClass().add("tour-skip");

        backButton.setOnAction(event -> showStep(currentStep - 1));
        nextButton.setOnAction(event -> {
            if (currentStep == steps.size() - 1) {
                finish();
            } else {
                showStep(currentStep + 1);
            }
        });
        skipButton.setOnAction(event -> finish());

        HBox actions = new HBox(8, backButton, spacer, skipButton, nextButton);
        tooltip.getChildren().addAll(stepLabel, titleLabel, descriptionLabel, actions);
    }

    public void start(List<Step> steps, Runnable onClose) {
        if (steps == null || steps.isEmpty()) {
            return;
        }

        this.steps = List.copyOf(steps);
        this.onClose = onClose == null ? () -> {} : onClose;
        setVisible(true);
        setManaged(true);
        toFront();
        blockKeyboardInput();
        showStep(0);
    }

    private void blockKeyboardInput() {
        if (getScene() != null && !keyboardBlocked) {
            getScene().addEventFilter(KeyEvent.ANY, keyEventBlocker);
            keyboardBlocked = true;
        }
    }

    private void showStep(int index) {
        if (index < 0 || index >= steps.size()) {
            return;
        }

        currentStep = index;
        Step step = steps.get(index);
        if (step.beforeShow() != null) {
            step.beforeShow().run();
        }

        toFront();

        stepLabel.setText("Step " + (index + 1) + " of " + steps.size());
        titleLabel.setText(step.title());
        descriptionLabel.setText(step.description());
        backButton.setDisable(index == 0 || !step.isBackAllowed());
        nextButton.setText(index == steps.size() - 1 ? "Finish" : "Next");

        Platform.runLater(() -> {
            if (!isVisible() || currentStep != index) {
                return;
            }
            toFront();
            tooltip.toFront();
            Node target = step.target().get();
            StepOptions options = step.options();
            Node tooltipAnchor = options.tooltipAnchor() == null
                    ? target
                    : options.tooltipAnchor().get();
            positionFor(target, tooltipAnchor, options);
        });
    }

    private void positionFor(Node target, Node tooltipAnchor, StepOptions options) {
        if (target == null || target.getScene() == null) {
            return;
        }

        Bounds targetBounds = target.localToScene(target.getBoundsInLocal());
        Point2D topLeft = sceneToLocal(targetBounds.getMinX(), targetBounds.getMinY());
        double padding = 8;

        double holeX = Math.max(0, topLeft.getX() - padding);
        double holeY = Math.max(0, topLeft.getY() - padding);
        double visibleTargetWidth = targetBounds.getWidth();
        double visibleTargetHeight = targetBounds.getHeight() * options.heightRatio();
        double holeWidth = Math.min(getWidth() - holeX, visibleTargetWidth + padding * 2);
        double holeHeight = Math.min(getHeight() - holeY, visibleTargetHeight + padding * 2);

        spotlight.setX(holeX);
        spotlight.setY(holeY);
        spotlight.setWidth(holeWidth);
        spotlight.setHeight(holeHeight);
        Hole secondaryHole = layoutSecondarySpotlight(tooltipAnchor, target);
        layoutDimOverlay(new Hole(holeX, holeY, holeWidth, holeHeight), secondaryHole);

        tooltip.applyCss();
        tooltip.autosize();

        Bounds anchorBounds = tooltipAnchor == null || tooltipAnchor.getScene() == null
                ? targetBounds
                : tooltipAnchor.localToScene(tooltipAnchor.getBoundsInLocal());
        Point2D anchorTopLeft = sceneToLocal(anchorBounds.getMinX(), anchorBounds.getMinY());
        double anchorWidth = tooltipAnchor == target
                ? visibleTargetWidth
                : anchorBounds.getWidth();

        double tooltipX;
        double tooltipY;
        if (options.tooltipPosition() == TooltipPosition.BELOW) {
            tooltipX = Math.max(16, Math.min(
                    anchorTopLeft.getX() + anchorWidth - tooltip.getWidth(),
                    getWidth() - tooltip.getWidth() - 16));
            tooltipY = Math.max(16, Math.min(
                    anchorTopLeft.getY() + anchorBounds.getHeight() + 20,
                    getHeight() - tooltip.getHeight() - 16));
        } else {
            tooltipX = anchorTopLeft.getX() + anchorWidth + 20;
            if (tooltipX + tooltip.getWidth() > getWidth()) {
                tooltipX = anchorTopLeft.getX() - tooltip.getWidth() - 20;
            }
            if (tooltipX < 16) {
                tooltipX = Math.max(16, getWidth() - tooltip.getWidth() - 16);
            }
            tooltipY = Math.max(16, Math.min(
                    anchorTopLeft.getY(), getHeight() - tooltip.getHeight() - 16));
        }
        tooltip.relocate(tooltipX, tooltipY);
    }

    private Hole layoutSecondarySpotlight(Node tooltipAnchor, Node target) {
        if (tooltipAnchor == null || tooltipAnchor == target || tooltipAnchor.getScene() == null) {
            secondarySpotlight.setVisible(false);
            return null;
        }

        Bounds bounds = tooltipAnchor.localToScene(tooltipAnchor.getBoundsInLocal());
        Point2D topLeft = sceneToLocal(bounds.getMinX(), bounds.getMinY());
        double padding = 6;

        double holeX = Math.max(0, topLeft.getX() - padding);
        double holeY = Math.max(0, topLeft.getY() - padding);
        double holeWidth = Math.min(getWidth() - holeX, bounds.getWidth() + padding * 2);
        double holeHeight = Math.min(getHeight() - holeY, bounds.getHeight() + padding * 2);

        secondarySpotlight.setX(holeX);
        secondarySpotlight.setY(holeY);
        secondarySpotlight.setWidth(holeWidth);
        secondarySpotlight.setHeight(holeHeight);
        secondarySpotlight.setVisible(true);
        return new Hole(holeX, holeY, holeWidth, holeHeight);
    }

    private void layoutDimOverlay(Hole primaryHole, Hole secondaryHole) {
        dimOverlay.getElements().clear();
        addRectanglePath(0, 0, getWidth(), getHeight());
        addRectanglePath(primaryHole.x(), primaryHole.y(), primaryHole.width(), primaryHole.height());
        if (secondaryHole != null) {
            addRectanglePath(
                    secondaryHole.x(),
                    secondaryHole.y(),
                    secondaryHole.width(),
                    secondaryHole.height());
        }
    }

    private void addRectanglePath(double x, double y, double width, double height) {
        dimOverlay.getElements().addAll(
                new MoveTo(x, y),
                new LineTo(x + width, y),
                new LineTo(x + width, y + height),
                new LineTo(x, y + height),
                new ClosePath());
    }

    private record Hole(double x, double y, double width, double height) {}

    private void finish() {
        if (getScene() != null && keyboardBlocked) {
            getScene().removeEventFilter(KeyEvent.ANY, keyEventBlocker);
            keyboardBlocked = false;
        }
        setVisible(false);
        setManaged(false);
        onClose.run();
        steps = List.of();
    }
}
