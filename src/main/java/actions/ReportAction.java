package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
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

    //新規登録画面の表示
    public void entryNew() throws ServletException,IOException{
        //CSRF対策
        putRequestScope(AttributeConst.TOKEN,getTokenId());
        //日報情報の空のインスタンスに、日報の日付＝今日の日付を設定
        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT,rv);
        //新規登録画面を表示
        forward(ForwardConst.FW_REP_NEW);
    }
    //新規登録を行う
    public void create() throws ServletException,IOException{
        //CSRF対策
        if(checkToken()) {
            //日報の日付が入力されていなければ今日の日付を設定
            LocalDate day = null;
            if(getRequestParam(AttributeConst.REP_DATE)==null
                    ||getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            }else {//入力されていればそれを取得
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev =(EmployeeView)getSessionScope(AttributeConst.LOGIN_EMP);
            //日報情報のインスタンス生成
            ReportView rv = new ReportView(
                    null,
                    ev,//ログインしている従業員
                    day,
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null);

            //日報情報登録
            List<String> errors = service.create(rv);
            if(errors.size() > 0) {
                //CSRF対策
                putRequestScope(AttributeConst.TOKEN,getTokenId());
                //入力された日報情報
                putRequestScope(AttributeConst.REPORT,rv);
                //エラーのリスト
                putRequestScope(AttributeConst.ERR,errors);

                //新規登録画面を再表示
                forward(ForwardConst.FW_REP_NEW);
            }else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH,MessageConst.I_REGISTERED.getMessage());
                //リダイレクト
                redirect(ForwardConst.ACT_REP,ForwardConst.CMD_INDEX);
            }
        }
    }

}
