package actions;

import java.io.IOException;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;

public class TopAction extends ActionBase{
    //indexアクションの実行
    @Override
    public void process() throws ServletException,IOException{
      //パラメータのcommandの値に該当するメソッドを実行
        invoke();
    }

    //一覧画面の表示
    public void index()throws ServletException,IOException{
        //セッションにフラッシュメッセージが設定されている場合はリクエストメッセージに移し替える
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush!=null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        //一覧画面を表示
        forward(ForwardConst.FW_TOP_INDEX);
    }
}
