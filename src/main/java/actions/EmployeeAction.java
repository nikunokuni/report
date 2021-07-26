package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import constants.PropertyConst;
import services.EmployeeService;

//従業員にかかわる処理を行う
public class EmployeeAction extends ActionBase{
    private EmployeeService service;

    @Override
    public void process()throws ServletException,IOException{
        service = new EmployeeService();
        invoke();
        service.close();
    }
//一覧画面の表示  呼ばれると、DBのテーブルが作成される
    public void index() throws ServletException,IOException{
        int page = getPage();
        List<EmployeeView> employees = service.getPerPage(page);

        long employeeCount = service.countAll();

        putRequestScope(AttributeConst.EMPLOYEES,employees);
        putRequestScope(AttributeConst.EMP_COUNT,employeeCount);
        putRequestScope(AttributeConst.PAGE,page);
        putRequestScope(AttributeConst.MAX_ROW,JpaConst.ROW_PER_PAGE);

        //セッションにフラッシュメッセージがあるときはリクエストスコープに移し替える
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush!=null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        //一覧画面の表示
        forward(ForwardConst.FW_EMP_INDEX);
    }

    public void entryNew() throws ServletException,IOException{
        putRequestScope(AttributeConst.TOKEN,getTokenId());
        //空の従業員インスタンス
        putRequestScope(AttributeConst.EMPLOYEE,new EmployeeView());
        //新規登録画面を表示
        forward(ForwardConst.FW_EMP_NEW);
    }
    //新規登録をおこなう
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //パラメータの値を元に従業員情報のインスタンスを作成する
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

            //アプリケーションスコープからpepper文字列を取得
            String pepper = getContextScope(PropertyConst.PEPPER);

            //従業員情報登録
            List<String> errors = service.create(ev, pepper);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.EMPLOYEE, ev); //入力された従業員情報
                putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_EMP_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP, ForwardConst.CMD_INDEX);
            }

        }
    }

    //詳細画面の表示
    public void show()throws ServletException, IOException{
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));
        if(ev==null||ev.getDeleteFlag()==AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
          //データが取得できないか、論理削除されている場合
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return;
        }
        putRequestScope(AttributeConst.EMPLOYEE,ev);
        //詳細画面の表示
        forward(ForwardConst.FW_EMP_SHOW);
    }

//編集画面の表示
    public void edit() throws ServletException,IOException{
        //idから従業員データを取得
        EmployeeView ev = service.findOne(toNumber(getRequestParam(AttributeConst.EMP_ID)));

        if(ev==null||ev.getDeleteFlag()==AttributeConst.DEL_FLAG_TRUE.getIntegerValue()) {
            //データを取得できないか、論理削除されているときはエラー
            forward(ForwardConst.FW_ERR_UNKNOWN);
            return ;
        }
        //CSRF対策のトークン
        putRequestScope(AttributeConst.TOKEN,getTokenId());
        //取得した従業員情報
        putRequestScope(AttributeConst.EMPLOYEE,ev);
        //編集画面を表示
        forward(ForwardConst.FW_EMP_EDIT);
    }
//更新を行う
    public void update()throws ServletException,IOException{
        if(checkToken()) {
            //従業員情報のインスタンス化
            EmployeeView ev = new EmployeeView(
                    toNumber(getRequestParam(AttributeConst.EMP_ID)),
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
                    //pepper文字列の取得
            String pepper = getContextScope(PropertyConst.PEPPER);
            //従業員情報更新
            List<String> errors = service.update(ev, pepper);

            if(errors.size()>0) {
                putRequestScope(AttributeConst.TOKEN,getTokenId());
                putRequestScope(AttributeConst.EMPLOYEE,ev);
                putRequestScope(AttributeConst.ERR,errors);
                //編集画面を再表示
                forward(ForwardConst.FW_EMP_EDIT);
            }else {//エラーなしの場合
                //フラッシュメッセージの設定
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_UPDATED.getMessage());

            //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);
            }
        }
    }

    //論理削除を行う
        public void destroy()throws ServletException,IOException{
            //CSRF対策
            if(checkToken()){
                //idを条件に従業員データを論理削除する
                service.destroy(toNumber(getRequestParam(AttributeConst.EMP_ID)));
                //フラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_DELETED.getMessage());
                //リダイレクト
                redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);
            }
        }
}