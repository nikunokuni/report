package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.ReportService;

public class TopAction extends ActionBase{
    private ReportService service;
    //indexアクションの実行
    @Override
    public void process() throws ServletException,IOException{
        service = new ReportService();
        //パラメータのcommandの値に該当するメソッドを実行
        invoke();
        service.close();
    }

    //一覧画面の表示
    public void index()throws ServletException,IOException{
        //セッションからログイン中の従業員情報を取得
        EmployeeView loginEmployee = (EmployeeView)getSessionScope(AttributeConst.LOGIN_EMP);
        //指定されたページ数に表示する分を取得
        int page = getPage();
        List<ReportView>reports = service.getMinePerPage(loginEmployee, page);
        //ログインした従業員の日報データ数を取得
        long myReportCount = service.countAllMine(loginEmployee);

        putRequestScope(AttributeConst.REPORTS,reports);
        putRequestScope(AttributeConst.REP_COUNT,myReportCount);
        putRequestScope(AttributeConst.PAGE,page);
        putRequestScope(AttributeConst.MAX_ROW,JpaConst.ROW_PER_PAGE);

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
