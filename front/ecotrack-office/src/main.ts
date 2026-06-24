import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// Import global styles
import './app/styles/variables.css';
import './app/styles/global.css';
import './app/styles/components.css';

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err));
