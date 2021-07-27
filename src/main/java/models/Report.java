package models;
//日報データのDTOモデル

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import constants.JpaConst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = JpaConst.TABLE_REP)
@NamedQueries({
    @NamedQuery(
            name  = JpaConst.Q_REP_GET_ALL,
            query = JpaConst.Q_REP_GET_ALL_DEF),
    @NamedQuery(
            name  = JpaConst.Q_REP_COUNT,
            query = JpaConst.Q_REP_COUNT_DEF),
    @NamedQuery(
            name  = JpaConst.Q_REP_GET_ALL_MINE,
            query = JpaConst.Q_REP_GET_ALL_MINE_DEF),
    @NamedQuery(
            name  = JpaConst.Q_REP_COUNT_ALL_MINE,
            query = JpaConst.Q_REP_COUNT_ALL_MINE_DEF)
})

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Report {
    //id
    @Id//主キー
    @Column(name = JpaConst.REP_COL_ID)
    //主キーを自動生成
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //日報を登録した従業員
    @ManyToOne
    @JoinColumn(name = JpaConst.REP_COL_EMP,nullable = false)
    private Employee employee;
    //日報の日付
    @Column(name = JpaConst.REP_COL_REP_DATE,nullable=false)
    private LocalDate reportDate;
    //タイトル
    @Column(name = JpaConst.REP_COL_TITLE,length = 255,nullable = false)
    private String title;
    //日報の内容
    @Lob
    @Column(name = JpaConst.REP_COL_CONTENT,nullable = false)
    private String content;
    //登録日時
    @Column(name = JpaConst.REP_COL_CREATED_AT,nullable = false)
    private LocalDateTime createdAt;
    //更新日時
    @Column(name = JpaConst.REP_COL_UPDATED_AT,nullable = false)
    private LocalDateTime updatedAt;

}