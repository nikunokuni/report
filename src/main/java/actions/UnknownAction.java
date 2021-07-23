package actions;
import java.io.IOException;

import javax.servlet.ServletException;

import constants.ForwardConst;

//エラー発生時の処理を行う
public class UnknownAction extends ActionBase{

    @Override
    public void process()throws ServletException,IOException{
        //エラー画面表示
        forward(ForwardConst.FW_ERR_UNKNOWN);
    }
}
