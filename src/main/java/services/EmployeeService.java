package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.NoResultException;

import actions.views.EmployeeConverter;
import actions.views.EmployeeView;
import constants.JpaConst;
import models.Employee;
import models.validators.EmployeeValidator;
import utils.EncryptUtil;

//従業員テーブル操作にかかわる処理を行うクラス
public class EmployeeService extends ServiceBase{

    //指定されたページ数の一覧画面に表示するデータを取得
    public List<EmployeeView> getPerPage(int page){
        //EmployeeテーブルからSELECTした結果
        List<Employee> employees = em.createNamedQuery(JpaConst.Q_EMP_GET_ALL, Employee.class)
                .setFirstResult(JpaConst.ROW_PER_PAGE*(page-1))//何件目から読みますか
                .setMaxResults(JpaConst.ROW_PER_PAGE)//全部で何件読みますか
                .getResultList();

        return EmployeeConverter.toViewList(employees);
        //Employee型からView型に変換して返却
    }
    //従業員テーブルのデータの件数を取得する
    public long countAll() {
        long empCount = (long)em.createNamedQuery(JpaConst.Q_EMP_COUNT,Long.class)
                .getSingleResult();
        return empCount;
    }
    //社員番号とパスワードを条件に取得したデータをEmployeeViewのインスタンスで返却
    public EmployeeView findOne(String code,String plainPass,String pepper) {
        Employee e = null;
        try {
            //ハッシュ化
            String pass = EncryptUtil.getPasswordEncrypt(plainPass,pepper);
            //社員番号とハッシュ化されたパスワードから、未削除の従業員を1件取得する
            e = em.createNamedQuery(JpaConst.Q_EMP_GET_BY_CODE_AND_PASS,Employee.class)
                    .setParameter(JpaConst.JPQL_PARM_CODE,code)
                    .setParameter(JpaConst.JPQL_PARM_PASSWORD,pass)
                    .getSingleResult();
        }catch(NoResultException ex) {
        }
        return EmployeeConverter.toView(e);
    }

    public EmployeeView findOne(int id) {
        Employee e = findOneInternal(id);
        return EmployeeConverter.toView(e);
    }

    //社員番号から該当するデータを取得
    public long countByCode(String code) {
        long employee_count = (long)em.createNamedQuery(JpaConst.Q_EMP_COUNT_RESISTERED_BY_CODE,Long.class)
                .setParameter(JpaConst.JPQL_PARM_CODE,code)
                .getSingleResult();
        return employee_count;
    }

    //画面から入力された従業員の登録内容をもとにデータを1件作成し、従業員テーブルに登録する
    public List<String> create(EmployeeView ev,String pepper){
        //パスワードのハッシュ化
        String pass = EncryptUtil.getPasswordEncrypt(ev.getPassword(),pepper);
        ev.setPassword(pass);
        //登録日時、更新日時の設定
        LocalDateTime now = LocalDateTime.now();
        ev.setCreatedAt(now);
        ev.setUpdatedAt(now);

        //登録内容のバリデーション
        List<String> errors = EmployeeValidator.validate(this, ev, true, true);
        //バリデーションエラーがなければ登録
        if(errors.size()==0) {
            create(ev);
        }
        return errors;
    }
    //画面から入力された従業員の更新内容をもとにデータを1件作成し、従業員テーブルを更新
    public List<String> update(EmployeeView ev,String pepper){
        //idから従業員情報を取得
        EmployeeView savedEmp = findOne(ev.getId());
        boolean validateCode = false;
        if(!savedEmp.getCode().equals(ev.getCode())) {
            //社員番号を更新する場合
            //社員番号についてのバリデーションをおこなう
            validateCode = true;
            //変更後の社員番号を設定
            savedEmp.setCode(ev.getCode());
        }
        boolean validatePass = false;
        if(ev.getPassword()!=null&&!ev.getPassword().equals("")) {
            validatePass = true;
            //変更後のパスワードをハッシュ化し設定する
            savedEmp.setPassword(EncryptUtil.getPasswordEncrypt(ev.getPassword(),pepper));
        }
        savedEmp.setName(ev.getName());
        savedEmp.setAdminFlag(ev.getAdminFlag());

        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);

        List<String> errors = EmployeeValidator.validate(this, savedEmp, validateCode,validatePass);
        if(errors.size()==0) {
            update(savedEmp);
        }
        return errors;
    }
    //idからデータの論理削除
    public void destroy(Integer id) {
        EmployeeView savedEmp = findOne(id);
        LocalDateTime today = LocalDateTime.now();
        savedEmp.setUpdatedAt(today);
//フラグの更新
        savedEmp.setDeleteFlag(JpaConst.EMP_DEL_TRUE);
        update(savedEmp);
    }
//社員番号とパスワードを条件に検索し、データが取得できるかどうかを認証
    public Boolean validateLogin(String code,String plintPass,String pepper) {
        boolean isValidEmployee = false;
        if(code !=null&&!code.equals("")&&plintPass!=null&&!plintPass.equals("")) {
            EmployeeView ev = findOne(code,plintPass,pepper);
            if(ev!=null&&ev.getId()!=null) {
                isValidEmployee = true;
            }
        }
        return isValidEmployee;
    }
    private Employee findOneInternal(int id) {
        Employee e = em.find(Employee.class,id);
        return e;
    }
    private void create(EmployeeView ev) {
        em.getTransaction().begin();
        em.persist(EmployeeConverter.toModel(ev));
        em.getTransaction().commit();
    }
    private void update(EmployeeView ev) {
        em.getTransaction().begin();
        Employee e = findOneInternal(ev.getId());
        EmployeeConverter.copyViewModel(e, ev);
        em.getTransaction().commit();
    }
}
