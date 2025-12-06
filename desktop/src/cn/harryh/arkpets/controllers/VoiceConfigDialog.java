package cn.harryh.arkpets.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class VoiceConfigDialog {
    @FXML
    private StackPane root;
    @FXML
    private JFXListView<String> voiceListView;
    @FXML
    private JFXButton confirmBtn;
    @FXML
    private JFXButton createBtn;

    public void initialize() {
        // TODO: 加载语音包列表
        voiceListView.getItems().addAll("语音包A", "语音包B", "语音包C");
        confirmBtn.setOnAction(e -> {
            String selected = voiceListView.getSelectionModel().getSelectedItem();
            // TODO: 处理确认选择逻辑
            ((Stage) root.getScene().getWindow()).close();
        });
        createBtn.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/UI/VoiceCreateDialog.fxml"));
                javafx.scene.Parent createRoot = loader.load();
                // 获取当前窗口
                Stage stage = (Stage) root.getScene().getWindow();
                stage.getScene().setRoot(createRoot);

                // 绑定返回按钮逻辑
                cn.harryh.arkpets.controllers.VoiceCreateDialog createController = loader.getController();
                createController.backBtn.setOnAction(ev -> {
                    stage.getScene().setRoot(root);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
