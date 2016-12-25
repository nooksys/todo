import { Routes, RouterModule } from '@angular/router';
import { TaskListComponent } from './task-list/task-list.component';
import { TaskFormComponent } from './task-form/task-form.component';
import { TaskDetailComponent } from './task-detail/task-detail.component';

const appRoutes: Routes = [
    { path: 'tasks', component: TaskListComponent },
    { path: 'task/detail/:id', component: TaskDetailComponent },
    { path: 'task/edit/:id', component: TaskFormComponent },
    { path: 'task/create', component: TaskFormComponent },

    // otherwise redirect to tasks
    { path: '**', redirectTo: 'tasks' }
];

export const routing = RouterModule.forRoot(appRoutes);