import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home-landing',
  standalone: true,
  imports: [],
  templateUrl: './home-landing.html',
  styleUrl: './home-landing.css',
})
export class HomeLanding implements OnInit{

  ngOnInit(): void {

    // Esto aplica la configuración de colores de Stitch dinámicamente al script de la cabecera
    const anyWindow = window as any;
    if (anyWindow.tailwind) {
      anyWindow.tailwind.config = {
        darkMode: "class",
        theme: {
          extend: {
            colors: {
              "surface-container-high": "#dce9ff",
              "on-primary-container": "#7c839b",
              "tertiary-container": "#001a42",
              "on-tertiary-fixed": "#001a42",
              "tertiary": "#000000",
              "inverse-surface": "#213145",
              "on-tertiary-fixed-variant": "#004395",
              "on-surface": "#0b1c30",
              "on-primary": "#ffffff",
              "surface-container-low": "#eff4ff",
              "tertiary-fixed": "#d8e2ff",
              "primary-fixed-dim": "#bec6e0",
              "surface-tint": "#565e74",
              "surface-container-highest": "#d3e4fe",
              "on-tertiary": "#ffffff",
              "primary": "#000000",
              "on-secondary": "#ffffff",
              "tertiary-fixed-dim": "#adc6ff",
              "surface-bright": "#f8f9ff",
              "outline-variant": "#c6c6cd",
              "on-secondary-fixed-variant": "#005049",
              "inverse-primary": "#bec6e0",
              "on-secondary-container": "#006f66",
              "surface-container": "#e5eeff",
              "on-error-container": "#93000a",
              "on-tertiary-container": "#3980f4",
              "on-primary-fixed-variant": "#3f465c",
              "error-container": "#ffdad6",
              "background": "#f8f9ff",
              "on-error": "#ffffff",
              "secondary-container": "#86f2e4",
              "surface-container-lowest": "#ffffff",
              "primary-fixed": "#dae2fd",
              "secondary": "#006a61",
              "surface": "#f8f9ff",
              "on-surface-variant": "#45464d",
              "primary-container": "#131b2e",
              "on-primary-fixed": "#131b2e",
              "outline": "#76777d",
              "inverse-on-surface": "#eaf1ff",
              "on-background": "#0b1c30",
              "surface-variant": "#d3e4fe",
              "on-secondary-fixed": "#00201d",
              "secondary-fixed": "#89f5e7",
              "surface-dim": "#cbdbf5",
              "error": "#ba1a1a",
              "secondary-fixed-dim": "#6bd8cb"
            },
            "borderRadius": {
                    "DEFAULT": "0.125rem",
                    "lg": "0.25rem",
                    "xl": "0.5rem",
                    "full": "0.75rem"
            },
            // ESTO ES LO QUE FALTABA PARA EL ESPACIADO CORRECTO
            "spacing": {
                    "md": "24px",
                    "container-max": "1440px",
                    "xs": "8px",
                    "sm": "16px",
                    "lg": "32px",
                    "xl": "48px",
                    "gutter": "24px",
                    "base": "4px"
            },
            // ESTO ES LO QUE FALTABA PARA LAS FUENTES CORRECTAS
            "fontFamily": {
                    "headline-md": ["Inter"],
                    "headline-lg-mobile": ["Inter"],
                    "body-md": ["Inter"],
                    "headline-lg": ["Inter"],
                    "title-md": ["Inter"],
                    "body-lg": ["Inter"],
                    "display-lg": ["Inter"],
                    "label-sm": ["Inter"]
            },
            "fontSize": {
                    "headline-md": ["24px", {"lineHeight": "32px", "fontWeight": "600"}],
                    "headline-lg-mobile": ["24px", {"lineHeight": "32px", "fontWeight": "600"}],
                    "body-md": ["14px", {"lineHeight": "20px", "fontWeight": "400"}],
                    "headline-lg": ["32px", {"lineHeight": "40px", "letterSpacing": "-0.01em", "fontWeight": "600"}],
                    "title-md": ["18px", {"lineHeight": "24px", "fontWeight": "500"}],
                    "body-lg": ["16px", {"lineHeight": "24px", "fontWeight": "400"}],
                    "display-lg": ["48px", {"lineHeight": "56px", "letterSpacing": "-0.02em", "fontWeight": "700"}],
                    "label-sm": ["12px", {"lineHeight": "16px", "letterSpacing": "0.05em", "fontWeight": "600"}]
            }
          }
        }
      };
    }
  }
}
