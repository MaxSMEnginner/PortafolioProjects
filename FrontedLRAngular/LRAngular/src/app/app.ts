import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,          // 👈 necesario porque no tienes AppModule
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.css']   // 👈 plural y en array
})
export class App {
  protected readonly title = signal('LRAngular');
}
