package com.alexandria.view.components.analyse_screen;

import com.alexandria.model.FileType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import java.nio.file.Path;

/** TODO: real PDF/TXT rendering via PdfDocumentRenderer/TextDocumentRenderer. */
public class DocumentView extends BorderPane {

    private final Label placeholder = new Label("Document viewer");

    public DocumentView() {
        getStyleClass().add("document-view");
        placeholder.getStyleClass().add("text-muted");
        setCenter(placeholder);
        BorderPane.setAlignment(placeholder, Pos.CENTER);
    }

    public void loadDocument(String content, FileType fileType, Path sourcePath) {
        placeholder.setText("Document viewer: " + fileType);
    }

    /** TODO go to: implement once renderer + highlight system exist. */
    public void goToPage(Integer page, Integer paragraph) {}

    public void dispose() {
        placeholder.setText("Document viewer");
    }
}