package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
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
}