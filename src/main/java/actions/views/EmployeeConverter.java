package actions.views;
/* モデルが保持するフィールドの内容をもう一つの
 * モデルの該当するフィールドへ移し替える。
 * 変換等もこのクラスで実施
 */

import java.util.ArrayList;
import java.util.List;

import constants.AttributeConst;
import constants.JpaConst;
import models.Employee;

//従業員データのDTOとViewモデルの変換
public class EmployeeConverter {
//ViewモデルのインスタンスからDTOモデルのインスタンスを作成する
    public static Employee toModel(EmployeeView ev) {
        return new Employee(
                ev.getId(),
                ev.getCode(),
                ev.getName(),
                ev.getPassword(),
                ev.getAdminFlag()==null
                    ?null
                    :ev.getAdminFlag()==AttributeConst.ROLE_ADMIN.getIntegerValue()
                        ?JpaConst.ROLE_ADMIN
                        :JpaConst.ROLE_GENERAL,
                ev.getCreatedAt(),
                ev.getUpdatedAt(),
                ev.getDeleteFlag()==null
                    ?null
                    :ev.getDeleteFlag()==AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                        ? JpaConst.EMP_DEL_TRUE
                        :JpaConst.EMP_DEL_FALSE);

    }

    //DTOモデルのインスタンスからViewモデルのインスタンスの作成
    public static EmployeeView toView(Employee e) {
        if(e==null) {
            return null;
        }
        return new EmployeeView(
        e.getId(),
        e.getCode(),
        e.getName(),
        e.getPassword(),
        e.getAdminFlag()==null
            ?null
            :e.getAdminFlag()==JpaConst.ROLE_ADMIN
                ?AttributeConst.ROLE_ADMIN.getIntegerValue()
                :AttributeConst.ROLE_GENERAL.getIntegerValue(),
        e.getCreatedAt(),
        e.getUpdatedAt(),
        e.getDeleteFlag()==null
            ?null
            :e.getDeleteFlag()==JpaConst.EMP_DEL_TRUE
                ?AttributeConst.DEL_FLAG_TRUE.getIntegerValue()
                :AttributeConst.DEL_FLAG_FALSE.getIntegerValue());

    }

    //DTOモデルのリストからViewモデルのリストを作成する
    public static List<EmployeeView> toViewList(List<Employee> list){
        List<EmployeeView>evs = new ArrayList();

        for(Employee e:list) {
            evs.add(toView(e));
        }
        return evs;
    }
    //Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
    public static void copyViewModel(Employee e,EmployeeView ev) {
        e.setId(ev.getId());
        e.setCode(ev.getCode());
        e.setName(ev.getName());
        e.setPassword(ev.getPassword());
        e.setAdminFlag(ev.getAdminFlag());
        e.setCreatedAt(ev.getCreatedAt());
        e.setUpdatedAt(ev.getUpdatedAt());
        e.setDeleteFlag(e.getDeleteFlag());
    }
}
