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
//一覧画面の表示
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
    public void create()throws ServletException,IOException{
        //CSRF対策
        if(checkToken()) {
            //パラメータの値をもとに従業員情報のインスタンスを作成
            EmployeeView ev = new EmployeeView(
                    null,
                    getRequestParam(AttributeConst.EMP_CODE),
                    getRequestParam(AttributeConst.EMP_NAME),
                    getRequestParam(AttributeConst.EMP_PASS),
                    toNumber(getRequestParam(AttributeConst.EMP_ADMIN_FLG)),
                    null,
                    null,
                    AttributeConst.DEL_FLAG_FALSE.getIntegerValue());
//アプリケーションスコープからpepper文字列の取得
            String pepper = getContextScope(PropertyConst.PEPPER);
            //従業員情報の登録
            List<String>errors = service.create(ev, pepper);

            if(errors.size()>0) {
                putRequestScope(AttributeConst.TOKEN,getTokenId());
                //入力された従業員情報
                putRequestScope(AttributeConst.EMPLOYEE,ev);
                //エラーのリスト
                putRequestScope(AttributeConst.ERR,errors);
                //新規登録画面を再表示
                forward(ForwardConst.FW_EMP_NEW);
            }else {
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_EMP,ForwardConst.CMD_INDEX);
            }


        }
    }
}