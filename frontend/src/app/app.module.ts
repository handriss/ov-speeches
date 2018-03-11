import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {
  MatButtonModule, MatCheckboxModule, MatToolbarModule, MatCardModule,
  MatFormFieldModule, MatInputModule
} from '@angular/material';

import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {MainComponent} from "./component/main/main.component";


@NgModule({
  declarations: [
    MainComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatCheckboxModule,
    MatToolbarModule,
    MatCardModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [MainComponent]
})
export class AppModule { }
