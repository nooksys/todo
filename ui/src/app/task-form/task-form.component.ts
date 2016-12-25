import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Task } from '../task';
import { TaskService } from '../task.service';
import { SelectItem } from 'primeng/primeng';
import { Subscription } from 'rxjs'
import { ConfirmationService } from 'primeng/primeng';

@Component({
  selector: 'app-task-form',
  templateUrl: './task-form.component.html',
  styleUrls: ['./task-form.component.css']
})
export class TaskFormComponent implements OnInit, OnDestroy {

  task: Task = new Task();
  statuses: SelectItem[];

  private sub: Subscription;

  constructor(private taskService: TaskService, private router: Router, private route: ActivatedRoute, private confirmationService: ConfirmationService) {
    this.statuses = [
      { label: 'pending', value: false },
      { label: 'done', value: true }
    ]
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

  onSubmit() {
    if (this.task.id) {
      this.taskService.update(this.task).subscribe(() => this.router.navigate(['/task/detail', this.task.id]));
    } else {
      this.taskService.save(this.task).subscribe((id) => {
        this.router.navigate(['/task/detail', id])
      });
    }
  }

  show () {
    this.router.navigate(['/task/detail', this.task.id]);
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
