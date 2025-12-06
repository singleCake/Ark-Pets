package cn.harryh.arkpets.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
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

    public void initialize() {
        loadVoicePacks();
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
        File audioDir = new File("../assets/audio");
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

    public void setOnVoiceSelected(Consumer<String> onVoiceSelected) {
        this.onVoiceSelected = onVoiceSelected;
    }

    public void setStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
