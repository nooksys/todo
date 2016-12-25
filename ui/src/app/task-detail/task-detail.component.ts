import { Component, OnInit, OnDestroy } from '@angular/core';
import { Task } from '../task';
import { TaskService } from '../task.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService } from 'primeng/primeng';
import { Subscription } from 'rxjs'

@Component({
  selector: 'app-task-detail',
  templateUrl: './task-detail.component.html',
  styleUrls: ['./task-detail.component.css']
})
export class TaskDetailComponent implements OnInit, OnDestroy {

  task: Task = new Task();

  private sub: Subscription;

  constructor(private taskService: TaskService, private router: Router, private route: ActivatedRoute, private confirmationService: ConfirmationService) {
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      let taskId = params['id'];
      if (taskId) {
        this.taskService.get(taskId).subscribe(task => this.task = task)
      }
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  edit() {
    this.router.navigate(['/task/edit', this.task.id]);
  }

  list() {
    this.router.navigate(['/tasks']);
  }

  confirmDelete() {
    this.confirmationService.confirm({
      message: 'Do you want to delete this task?',
      header: 'Delete Confirmation',
      icon: 'fa fa-trash',
      accept: () => {
        this.taskService.delete(this.task.id).subscribe(response => {
          this.router.navigate(['/tasks']);
        });
      }
    });
  }
}
