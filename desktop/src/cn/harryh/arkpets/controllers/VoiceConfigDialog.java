package cn.harryh.arkpets.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Consumer;

import cn.harryh.arkpets.utils.Logger;

public class VoiceConfigDialog {
    @FXML
    private StackPane root;
    @FXML
    private JFXListView<String> voiceListView;
    @FXML
    private JFXButton confirmBtn;
    @FXML
    private JFXButton createBtn;

    private Consumer<String> onVoiceSelected;
    private Stage dialogStage;
    private String wikiModelName;

    public void initialize() {
        loadVoicePacks();
        setupCellFactory();
        confirmBtn.setOnAction(e -> {
            String selected = voiceListView.getSelectionModel().getSelectedItem();
            if (onVoiceSelected != null) {
                onVoiceSelected.accept(selected);
            }
            if (dialogStage != null) {
                dialogStage.close();
            }
        });
        createBtn.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/UI/VoiceCreateDialog.fxml"));
                javafx.scene.Parent createRoot = loader.load();
                // 获取当前窗口
                Stage stage = dialogStage != null ? dialogStage : (Stage) root.getScene().getWindow();
                
                // 获取 controller 并设置 stage
                cn.harryh.arkpets.controllers.VoiceCreateDialog createController = loader.getController();
                createController.setStage(stage);
                createController.setWikiModelName(wikiModelName);
                
                // 设置包创建完成后返回到列表的回调
                createController.setOnPackageCreated(() -> {
                    loadVoicePacks(); // 重新加载语音包列表
                    stage.getScene().setRoot(root);
                });
                
                stage.getScene().setRoot(createRoot);

                // 绑定返回按钮逻辑
                createController.backBtn.setOnAction(ev -> {
                    stage.getScene().setRoot(root);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void loadVoicePacks() {
        File audioDir = new File("audio");
        if (!audioDir.exists() || !audioDir.isDirectory()) {
            Logger.warn("VoiceConfig", "audio directory not found: " + audioDir.getPath());
            voiceListView.setPlaceholder(new Label("未找到语音包目录"));
            return;
        }

        String[] names = audioDir.list((dir, name) -> new File(dir, name).isDirectory());
        if (names == null || names.length == 0) {
            voiceListView.setPlaceholder(new Label("暂无语音包"));
            return;
        }

        // 清空原有列表，避免重复
        voiceListView.getItems().clear();
        
        Arrays.stream(names)
                .sorted(Comparator.naturalOrder())
                .forEach(voiceListView.getItems()::add);
    }

    private void setupCellFactory() {
        voiceListView.setCellFactory(lv -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final Region spacer = new Region();
            private final Button deleteBtn = new Button("🗑");
            private final HBox container = new HBox(8, nameLabel, spacer, deleteBtn);

            {
                deleteBtn.setFocusTraversable(false);
                deleteBtn.setOnAction(e -> onDelete());
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-padding: 2 4 2 4;");
                spacer.setMinWidth(0);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                container.setAlignment(Pos.CENTER_LEFT);
                container.setFillHeight(true);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    nameLabel.setText(item);
                    setGraphic(container);
                }
            }

            private void onDelete() {
                String name = getItem();
                if (name == null) return;

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("确认删除");
                alert.setHeaderText(null);
                alert.setContentText("确认删除语音包 " + name + " ?");
                Stage stage = dialogStage != null ? dialogStage : (Stage) root.getScene().getWindow();
                alert.initOwner(stage);
                Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    File target = new File("../assets/audio/" + name);
                    if (deleteDirectory(target)) {
                        voiceListView.getItems().remove(name);
                        if (voiceListView.getSelectionModel().getSelectedItem() != null &&
                                voiceListView.getSelectionModel().getSelectedItem().equals(name)) {
                            voiceListView.getSelectionModel().clearSelection();
                        }
                        Logger.info("VoiceConfig", "Deleted voice pack: " + name);
                    } else {
                        Logger.warn("VoiceConfig", "Failed to delete voice pack: " + name);
                    }
                }
            }
        });
    }

    private boolean deleteDirectory(File dir) {
        if (dir == null || !dir.exists()) return true;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    if (!deleteDirectory(f)) return false;
                } else if (!f.delete()) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public void setOnVoiceSelected(Consumer<String> onVoiceSelected) {
        this.onVoiceSelected = onVoiceSelected;
    }

    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setWikiModelName(String wikiModelName) {
        this.wikiModelName = wikiModelName;
    }
}
