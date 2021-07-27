package models.validators;

import java.util.ArrayList;
import java.util.List;

import actions.ReportView;
import constants.MessageConst;

//日報インスタンスに設定されている値のバリデーションを行うクラス
public class ReportValidator {

    public static List<String> validate(ReportView rv){
        List<String>errors = new ArrayList<String>();
        //タイトルチェック
        String titleError = validateTitle(rv.getTitle());
        if(!titleError.equals("")) {
            errors.add(titleError);
        }
        //内容チェック
        String contentError = validateContent(rv.getContent());
        if(!contentError.equals("")) {
            errors.add(contentError);
        }
        return errors;
    }
    //タイトルのチェック
    private static String validateTitle(String title) {
        if(title == null||title.equals("")) {
            return MessageConst.E_NOTITLE.getMessage();
        }
        //入力値がある場合は空文字を返却
        return "";
    }
    //内容のチェック
    private static String validateContent(String content) {
        if(content == null||content.equals("")) {
            return MessageConst.E_NOCONTENT.getMessage();
        }
      //入力値がある場合は空文字を返却
        return "";
    }

}