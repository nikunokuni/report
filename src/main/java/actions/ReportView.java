package actions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import actions.views.EmployeeView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//日報情報について画面の入力値・出力地を扱うモデル
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportView {
private Integer id;
private EmployeeView employee;
private LocalDate reportDate;
private String title;
private String content;
private LocalDateTime createdAt;
private LocalDateTime updatedAt;
}
