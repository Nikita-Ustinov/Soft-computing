package sample;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import sample.old.NNAutoencoders;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class Controller {

    @FXML
    private Button btn0;

    @FXML
    private ImageView img0;

    @FXML
    private Button btn4;

    @FXML
    private ImageView img01;

    @FXML
    private Button btn9;

    @FXML
    private ImageView img02;

    @FXML
    private Button btn6;

    @FXML
    private ImageView img03;

    @FXML
    private Button btn5;

    @FXML
    private ImageView img04;

    @FXML
    private Button btn3;

    @FXML
    private ImageView img05;

    @FXML
    private Button btn2;

    @FXML
    private ImageView img06;

    @FXML
    private Button btn1;

    @FXML
    private ImageView img07;

    @FXML
    private Button btn8;

    @FXML
    private ImageView img08;

    @FXML
    private Button btn7;

    @FXML
    private ImageView img09;

    @FXML
    private Text textAnswer;


    void initialize() {


    }

    public void FirstBtnAction(ActionEvent actionEvent) {
        textAnswer.setText("You pushed on Ferarri!");
    }

    public void MustangAction(MouseEvent mouseEvent) {
        textAnswer.setText("You pushed on Mustang!");
    }

    public void FirstBtnAction(javafx.event.ActionEvent actionEvent) {
        textAnswer.setText("You pushed on Ferarri!");
    }

    public void MustangAction(javafx.event.ActionEvent actionEvent) throws Exception {
        textAnswer.setText("You pushed on Mustang!");
        NNAutoencoders.main(null);
    }
}
