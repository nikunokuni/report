package actions;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import services.ReportService;

//日報に関する処理を行うクラス
public class ReportAction extends ActionBase{
    private ReportService service;

    @Override
    public void process()throws ServletException,IOException{
        service = new ReportService();
        //メソッドの実行
        invoke();
        service.close();
    }
    //一覧画面の表示
    public void index() throws ServletException,IOException{
        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();
        List<ReportView>reports = service.getAllPerPage(page);
        //日報の全データの件数を取得
        long reportsCount = service.countAll();
        putRequestScope(AttributeConst.REPORTS,reports);
        putRequestScope(AttributeConst.REP_COUNT,reportsCount);
        putRequestScope(AttributeConst.PAGE,page);
        putRequestScope(AttributeConst.MAX_ROW,JpaConst.ROW_PER_PAGE);
        //セッションにフラッシュメッセージがある場合はリクエストスコープに移動
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }
        //一覧画面を表示
        forward(ForwardConst.FW_REP_INDEX);

    }
}
