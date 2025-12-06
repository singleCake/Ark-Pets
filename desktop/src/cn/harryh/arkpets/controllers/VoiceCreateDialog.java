package cn.harryh.arkpets.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import com.jfoenix.controls.JFXListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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

    public void initialize() {
        // 默认返回行为：关闭窗口；如在父界面中会被重写为切换视图
        backBtn.setOnAction(e -> ((Stage) root.getScene().getWindow()).close());

        // TODO: 初始化已有语音列表数据
        generateListView.setPlaceholder(new javafx.scene.control.Label("暂无生成语音"));
        pokeListView.setPlaceholder(new javafx.scene.control.Label("暂无戳动语音"));
        dragListView.setPlaceholder(new javafx.scene.control.Label("暂无拖动语音"));

        uploadGenerateBtn.setOnAction(e -> {
            // TODO: 处理生成语音上传
        });
        uploadPokeBtn.setOnAction(e -> {
            // TODO: 处理戳动语音上传
        });
        uploadDragBtn.setOnAction(e -> {
            // TODO: 处理拖动语音上传
        });

        createConfirmBtn.setOnAction(e -> {
            // TODO: 创建语音包逻辑
        });
    }
}
