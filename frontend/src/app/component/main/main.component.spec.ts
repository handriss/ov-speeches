import {MainComponent} from "./main.component";
import {TestBed} from "@angular/core/testing";
import {ComponentFixture} from "@angular/core/testing";
import {MatInputModule, MatToolbarModule, MatCardModule, MatCheckboxModule, MatButtonModule} from "@angular/material";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {BrowserModule} from "@angular/platform-browser";

describe('MainComponent', ()=> {

  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MainComponent],
      imports: [
        BrowserModule,
        BrowserAnimationsModule,
        MatButtonModule,
        MatCheckboxModule,
        MatToolbarModule,
        MatCardModule,
        MatInputModule
      ]
    });
    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
  });

  it('should check the contents of the title', () => {
    fixture.detectChanges();
    const title = fixture.nativeElement.querySelector('h3');
    expect(title.textContent).toContain('frontend is working')
  });
});
