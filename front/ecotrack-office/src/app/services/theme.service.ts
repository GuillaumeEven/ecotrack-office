import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export type Theme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly THEME_KEY = 'ecotrack-theme';
  private theme$ = new BehaviorSubject<Theme>(this.loadTheme());

  constructor() {
    this.applyTheme(this.theme$.value);
  }

  /**
   * Carga el tema desde localStorage o detecta la preferencia del sistema
   */
  private loadTheme(): Theme {
    const stored = localStorage.getItem(this.THEME_KEY) as Theme | null;
    if (stored) {
      return stored;
    }

    // Detecta la preferencia del sistema
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  /**
   * Aplica el tema agregando/eliminando la clase 'dark' en <html>
   */
  private applyTheme(theme: Theme): void {
    const htmlElement = document.documentElement;
    if (theme === 'dark') {
      htmlElement.classList.add('dark');
    } else {
      htmlElement.classList.remove('dark');
    }
    localStorage.setItem(this.THEME_KEY, theme);
  }

  /**
   * Alterna entre los temas light y dark
   */
  toggleTheme(): void {
    const newTheme = this.theme$.value === 'light' ? 'dark' : 'light';
    this.setTheme(newTheme);
  }

  /**
   * Establece un tema específico
   */
  setTheme(theme: Theme): void {
    this.applyTheme(theme);
    this.theme$.next(theme);
  }

  /**
   * Retorna el tema actual
   */
  getTheme(): Theme {
    return this.theme$.value;
  }

  /**
   * Observable del tema actual
   */
  getTheme$(): Observable<Theme> {
    return this.theme$.asObservable();
  }
}
