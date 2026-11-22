import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// Composant racine : la "coquille" commune à toutes les pages (barre de navigation)
@Component({
  imports: [RouterOutlet],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {}
