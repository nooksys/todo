import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { DataListModule, ButtonModule, ConfirmDialogModule, ConfirmationService, InputTextModule, DropdownModule, EditorModule, SharedModule } from 'primeng/primeng';
import { AppComponent } from './app.component';
import { TaskListComponent } from './task-list/task-list.component';
import { TaskService } from './task.service';
import { TaskFormComponent } from './task-form/task-form.component';
import { routing } from './app.routing';
import { TaskDetailComponent } from './task-detail/task-detail.component'

@NgModule({
  declarations: [
    AppComponent,
    TaskListComponent,
    TaskFormComponent,
    TaskDetailComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    DataListModule,
    ButtonModule,
    ConfirmDialogModule,
    InputTextModule,
    DropdownModule,
    EditorModule, 
    SharedModule,
    routing
  ],
  providers: [TaskService, ConfirmationService],
  bootstrap: [AppComponent]
})
export class AppModule { }
