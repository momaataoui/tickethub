import { registerLocaleData } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { ApplicationConfig, LOCALE_ID, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';

// Dates et nombres en français : "dimanche 20 décembre 2026", "1 500"
registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding()), // les paramètres d'URL deviennent des input()
    provideHttpClient(), // active HttpClient (appels vers l'API Spring)
    { provide: LOCALE_ID, useValue: 'fr-FR' },
  ],
};
