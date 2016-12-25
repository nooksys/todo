import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { TaskService } from '../task.service';
import { ConfirmationService } from 'primeng/primeng';
import { Task } from '../task';

@Component({
  selector: 'app-task-list',
  templateUrl: './task-list.component.html',
  styleUrls: ['./task-list.component.css']
})
export class TaskListComponent implements OnInit {

  tasks: Task[];
  pageSize: number = 10;
  totalRecords: number;

  constructor(private router: Router, private route: ActivatedRoute, private taskService: TaskService, private confirmationService: ConfirmationService) { }

  ngOnInit() {
    this.loadData();
  }

  loadData(pageNo?: number, pageSize?: number) {
    this.taskService.list(pageNo, pageSize).subscribe(pageResult => {
      this.tasks = pageResult.items;
      this.pageSize = pageResult.pageSize;
      this.totalRecords = pageResult.totalRecords;
    }, (err) => {
      console.error(err);
    });
  }

  onLazyLoad(event) {
    let pageNo = 1 + (event.first / event.rows);
    this.loadData(pageNo, event.rows);
  }

  confirmDelete(id: string) {
    this.confirmationService.confirm({
      message: 'Do you want to delete this task?',
      header: 'Delete Confirmation',
      icon: 'fa fa-trash',
      accept: () => {
        this.taskService.delete(id).subscribe(response => {
          this.loadData() // refresh
        });
      }
    });
  }

  newTask() {
    this.router.navigate(["/task/create"]);
  }

}
