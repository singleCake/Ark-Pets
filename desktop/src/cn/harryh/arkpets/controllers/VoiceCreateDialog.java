package cn.harryh.arkpets.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import cn.harryh.arkpets.utils.Logger;

public class VoiceCreateDialog {
    @FXML
    private StackPane root;
    @FXML
    public JFXButton backBtn;
    @FXML
    private TextField voiceNameInput;
    @FXML
    private JFXButton createConfirmBtn;
    @FXML
    private JFXListView<String> generateListView;
    @FXML
    private JFXListView<String> pokeListView;
    @FXML
    private JFXListView<String> dragListView;
    @FXML
    private JFXButton uploadGenerateBtn;
    @FXML
    private JFXButton uploadPokeBtn;
    @FXML
    private JFXButton uploadDragBtn;

    private final ObservableList<String> generateFiles = FXCollections.observableArrayList();
    private final ObservableList<String> pokeFiles = FXCollections.observableArrayList();
    private final ObservableList<String> dragFiles = FXCollections.observableArrayList();
    private Stage dialogStage;
    private Runnable onPackageCreated;

    public void initialize() {
        backBtn.setOnAction(e -> {
            try {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            } catch (NullPointerException ex) {
                Logger.warn("VoiceCreate", "Failed to close dialog: root not initialized");
            }
        });

        generateListView.setItems(generateFiles);
        pokeListView.setItems(pokeFiles);
        dragListView.setItems(dragFiles);

        generateListView.setPlaceholder(new javafx.scene.control.Label("暂无生成语音"));
        pokeListView.setPlaceholder(new javafx.scene.control.Label("暂无戳动语音"));
        dragListView.setPlaceholder(new javafx.scene.control.Label("暂无拖动语音"));

        uploadGenerateBtn.setOnAction(e -> uploadVoiceFiles(generateFiles, "生成"));
        uploadPokeBtn.setOnAction(e -> uploadVoiceFiles(pokeFiles, "戳动"));
        uploadDragBtn.setOnAction(e -> uploadVoiceFiles(dragFiles, "拖动"));

        createConfirmBtn.setOnAction(e -> createVoicePack());
    }

    private void uploadVoiceFiles(ObservableList<String> targetList, String typeLabel) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择" + typeLabel + "语音文件");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("OGG音频文件", "*.ogg")
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("所有文件", "*.*")
        );

        try {
            Stage stage = dialogStage != null ? dialogStage : (Stage) root.getScene().getWindow();
            java.util.List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);

            if (selectedFiles != null) {
                for (File file : selectedFiles) {
                    String fileName = file.getName();
                    String entry = fileName + "|" + file.getAbsolutePath();
                    if (!targetList.contains(entry)) {
                        targetList.add(entry);
                        Logger.debug("VoiceCreate", "Added file: " + fileName);
                    }
                }
            }
        } catch (NullPointerException ex) {
            Logger.warn("VoiceCreate", "FileChooser failed - scene not ready: " + typeLabel);
            ex.printStackTrace();
        }
    }

    private void createVoicePack() {
        String voiceName = voiceNameInput.getText().trim();

        if (voiceName.isEmpty()) {
            Logger.warn("VoiceCreate", "Voice pack name is empty");
            return;
        }

        if (!voiceName.matches("[a-zA-Z0-9_\\-]+")) {
            Logger.warn("VoiceCreate", "Invalid voice pack name: " + voiceName);
            return;
        }

        File voicePackDir = new File("../assets/audio/" + voiceName);
        Logger.debug("VoiceCreate", "Creating voice pack at: " + voicePackDir.getAbsolutePath());
        
        if (voicePackDir.exists()) {
            Logger.warn("VoiceCreate", "Voice pack already exists: " + voiceName);
            return;
        }

        try {
            File spawnDir = new File(voicePackDir, "spawn");
            File clickDir = new File(voicePackDir, "click");
            File dragDir = new File(voicePackDir, "drag");

            boolean spawnCreated = spawnDir.mkdirs();
            boolean clickCreated = clickDir.mkdirs();
            boolean dragCreated = dragDir.mkdirs();
            
            Logger.debug("VoiceCreate", "spawn dir created: " + spawnCreated + " at " + spawnDir.getAbsolutePath());
            Logger.debug("VoiceCreate", "click dir created: " + clickCreated + " at " + clickDir.getAbsolutePath());
            Logger.debug("VoiceCreate", "drag dir created: " + dragCreated + " at " + dragDir.getAbsolutePath());

            copyVoiceFiles(generateFiles, spawnDir);
            copyVoiceFiles(pokeFiles, clickDir);
            copyVoiceFiles(dragFiles, dragDir);

            Logger.info("VoiceCreate", "Voice pack created successfully: " + voiceName);
            
            // Call callback to notify parent and handle closing
            if (onPackageCreated != null) {
                javafx.application.Platform.runLater(onPackageCreated);
            }

        } catch (IOException e) {
            Logger.error("VoiceCreate", "Failed to create voice pack: " + voiceName, e);
        }
    }

    private void copyVoiceFiles(ObservableList<String> fileList, File targetDir) throws IOException {
        for (String fileEntry : fileList) {
            String[] parts = fileEntry.split("\\|", 2);
            if (parts.length == 2) {
                File sourceFile = new File(parts[1]);
                if (sourceFile.exists()) {
                    File targetFile = new File(targetDir, parts[0]);
                    Files.copy(
                            sourceFile.toPath(),
                            targetFile.toPath(),
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }
            }
        }
    }

    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setOnPackageCreated(Runnable callback) {
        this.onPackageCreated = callback;
    }
}
