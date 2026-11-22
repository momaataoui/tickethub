import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

// Composant racine : la "coquille" commune à toutes les pages (barre de navigation)
@Component({
  imports: [RouterOutlet, RouterLink],
  selector: 'app-root',
  styleUrl: './app.scss',
  templateUrl: './app.html',
})
export class App {}
