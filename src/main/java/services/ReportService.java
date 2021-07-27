package services;

import java.time.LocalDateTime;
import java.util.List;

import actions.ReportView;
import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import actions.views.ReportConverter;
import constants.JpaConst;
import models.Report;
import models.validators.ReportValidator;

//日報テーブルの操作にかかわる処理を行うクラス
public class ReportService extends ServiceBase{
    //指定した従業員が作成した日報データを指定されたページ数の一覧画面に表示する分取得する
    public List<ReportView> getMinePerPage(EmployeeView employee,int page){
        List<Report>reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL_MINE,Report.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE, EmployeeConverter.toModel(employee))
                .setFirstResult(JpaConst.ROW_PER_PAGE*(page-1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }

    //指定した従業員が作成した日報データの件数を取得し返却する
    public long cocuntAllMine(EmployeeView employee) {
        long count = (long)em.createNamedQuery(JpaConst.Q_REP_COUNT_ALL_MINE,Long.class)
                .setParameter(JpaConst.JPQL_PARM_EMPLOYEE,EmployeeConverter.toModel(employee))
                .getSingleResult();
        return count;
    }

    //指定されたページ数の一覧画面に表示する日報データを取得し、ReportViewのリストで返却
    public List<ReportView> getAllPerPage(int page){
        List<Report>reports = em.createNamedQuery(JpaConst.Q_REP_GET_ALL,Report.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE*(page-1))
                .setMaxResults(JpaConst.ROW_PER_PAGE)
                .getResultList();
        return ReportConverter.toViewList(reports);
    }
    //日報テーブルのデータの件数を取得し返却する
    public long countAll() {
        long reports_count = (long)em.createNamedQuery(JpaConst.Q_REP_COUNT,Long.class)
                .getSingleResult();
        return reports_count;
    }
    //idを条件に取得したデータをReportViewのインスタンスで返却する
    public ReportView findOne(int id) {
        return ReportConverter.toView(findOneInternal(id));
    }

    //画面から入力された日報の登録内容をもとにデータを1件作成し、日報テーブルに登録する
    public List<String> create(ReportView rv){
        List<String>errors = ReportValidator.validate(rv);
        if(errors.size()==0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setCreatedAt(ldt);
            rv.setUpdatedAt(ldt);
            createInternal(rv);
        }
        return errors;
    }

    //画面から入力された日報の登録内容をもとに、日報データを更新する
    public List<String> update(ReportView rv){
        //バリデーションを行う
        List<String>errors = ReportValidator.validate(rv);
        if(errors.size()==0) {
            LocalDateTime ldt = LocalDateTime.now();
            rv.setUpdatedAt(ldt);

            updateInternal(rv);
        }
        return errors;
    }
    //idを条件に1件データを取得する
    private Report findOneInternal(int id) {
       return em.find(Report.class,id);
    }
    //日報データを１件取得
    private void createInternal(ReportView rv) {
        em.getTransaction().begin();
        em.persist(ReportConverter.toModel(rv));
        em.getTransaction().commit();
    }
    //日報データの更新
    private void updateInternal(ReportView rv) {
        em.getTransaction().begin();
        Report r = findOneInternal(rv.getId());
        ReportConverter.copyViewToModel(r, rv);
        em.getTransaction().commit();
    }
}