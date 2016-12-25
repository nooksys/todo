import { Injectable } from '@angular/core';
import { Http, URLSearchParams } from '@angular/http';
import { Task } from './task';
import { PageResult } from './page-result';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';

@Injectable()
export class TaskService {

  constructor(private http: Http) { }

  get(id: string): Observable<Task> {
    return this.http.get('/api/tasks/' + id).map(response => response.json());
  }

  save(task: Task): Observable<string> {
    return this.http.post('/api/tasks', task).map(response => {
      return response.json().id;
    });
  }

  update(task: Task): Observable<void> {
    return this.http.put('/api/tasks/' + task.id, task).map(response => null);
  }

  delete(id: string): Observable<void> {
    return this.http.delete('/api/tasks/' + id).map(response => null);
  }

  list(pageNo?: number, pageSize?: number): Observable<PageResult<Task>> {
    let params = new URLSearchParams();
    if (pageNo) {
      params.append('pageNo', pageNo.toString());
    }
    if (pageSize) {
      params.append('pageSize', pageSize.toString());
    }
    return this.http.get('/api/tasks', { search: params }).map(response => response.json());
  }
}
