package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TasksRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class MainController {

  @Autowired
  private TasksRepository repo;

  /**
   * メイン画面.
   * @param model モデル
   * @param user ユーザー情報
   * @param date カレンダーの日付
   * @return
   */
  @GetMapping("/main")
  public String main(Model model, @AuthenticationPrincipal AccountUserDetails user,
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

    MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<LocalDate, Tasks>();

    List<List<LocalDate>> matrix = new ArrayList<>();
    List<LocalDate> week = new ArrayList<>();
    matrix.add(week);
    // 1週目
    LocalDate d;
    if(date == null) {
      d = LocalDate.now();
      d = LocalDate.of(d.getYear(), d.getMonthValue(), 1);
    }else {
      d = date;
    }
    model.addAttribute("prev", d.minusMonths(1));
    model.addAttribute("next", d.plusMonths(1));
    model.addAttribute("month", d.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()));
    DayOfWeek w = d.getDayOfWeek();
    LocalDate start = d = d.minusDays(w.getValue());
    for(int i = 1; i <= 7; i++) {
      week.add(d);
      tasks.put(d, null);
      d = d.plusDays(1);
    }
    week = new ArrayList<>();
    matrix.add(week);
    for(int i = 7; i <= d.lengthOfMonth(); i++) {
      w = d.getDayOfWeek();
      week.add(d);
      tasks.put(d, null);
      if(w == DayOfWeek.SATURDAY) {
        week = new ArrayList<>();
        matrix.add(week);
      }

      d = d.plusDays(1);
    }
    // 最終週
    w = d.getDayOfWeek();
    for(int i = 1; i <= 7-w.getValue(); i++) {
      week.add(d);
      tasks.put(d, null);
      d = d.plusDays(1);
    }
    LocalDate end = d;
    model.addAttribute("matrix", matrix);
    model.addAttribute("tasks", tasks);
    // タスクの追加
    List<Tasks> list;
    if(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ROLE_ADMIN"))) {
      list = repo.findAllByDateBetween(start.atTime(0,0), end.atTime(0,0));
    }else{
      list = repo.findByDateBetween(start.atTime(0, 0),end.atTime(0, 0), user.getName());
    }
    for(Tasks t : list) {
      tasks.add(t.getDate().toLocalDate(), t);
    }

    return "main";
  }
  /**
   * タスクの新規作成画面.
   * @param model モデル
   * @param date 追加対象日
   * @return
   */
  @GetMapping("/main/create/{date}")
  public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

    return "create";
  }

  /**
   * タスクの新規作成.
   * @param model モデル
   * @param form フォームオブジェクト
   * @param user ユーザー情報
   * @return
   */
  @PostMapping("/main/create")
  public String createPost(Model model, TaskForm form, @AuthenticationPrincipal AccountUserDetails user) {
    Tasks task = new Tasks();
    task.setName(user.getName());
    task.setTitle(form.getTitle());
    task.setText(form.getText());
    task.setDate(form.getDate().atTime(0, 0));

    repo.save(task);

    return "redirect:/main";
  }
  /**
   * タスク編集画面の表示.
   * @param model モデル
   * @param id タスクID
   * @return
   */
  @GetMapping("/main/edit/{id}")
  public String edit(Model model, @PathVariable Integer id) {
    Tasks task = repo.getById(id);
    model.addAttribute("task", task);
    return "edit";
  }
  /**
   * タスクの編集.
   * @param model モデル
   * @param form フォームオブジェクト
   * @param id タスクID
   * @param user ユーザー情報
   * @return
   */
  @PostMapping("/main/edit/{id}")
  public String editPost(Model model, TaskForm form, @PathVariable Integer id, @AuthenticationPrincipal AccountUserDetails user) {
    Tasks task = new Tasks();
    task.setId(id);

    task.setName(user.getName());
    task.setTitle(form.getTitle());
    task.setText(form.getText());
    task.setDate(form.getDate().atTime(0, 0));
    task.setDone(form.isDone());

    repo.save(task);

    return "redirect:/main";
  }
  /**
   * タスクの削除処理.
   * @param model モデル
   * @param form フォームオブジェクト
   * @param id タスクID
   * @return
   */
  @PostMapping("/main/delete/{id}")
  public String deletePost(Model model, TaskForm form, @PathVariable Integer id) {
    Tasks task = new Tasks();
    task.setId(id);

    repo.delete(task);

    return "redirect:/main";
  }
}