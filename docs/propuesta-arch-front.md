# 3. Arquitectura del Sistema Frontend

Para el desarrollo de la aplicación web se ha diseñado e implementado una arquitectura modular en el frontend utilizando **Angular (v21+)**. El diseño sigue estrictamente las directrices de la guía oficial de estilo de Angular y los patrones arquitectónicos modernos de la industria, garantizando principios fundamentales de la ingeniería de software como la **separación de responsabilidades (Separation of Concerns)**, la **reutilización de código**, la **mantenibilidad** y la **escalabilidad**.

A continuación se detalla la estructura del directorio principal del espacio de trabajo y se justifica la distribución de todos sus elementos, incluyendo componentes, layouts, servicios, modelos y la gestión de recursos estáticos.

---

## 3.1. Estructura de Directorios Ampliada

El núcleo del frontend y sus recursos públicos se organizan de la siguiente manera:

```
mi-proyecto/
│
├── public/                 # Recursos estáticos globales (Raíz del servidor)
│   ├── favicon.ico         # Icono de la aplicación
│   ├── images/             # Imágenes de la aplicación (PNG, JPG, WebP)
│   └── icons/              # Vectores e iconos locales (SVG)
│
└── src/
    └── app/
        ├── core/           # Módulos e infraestructura global (Singleton)
        │   ├── guards/     # Guardianes de rutas (ej. AuthGuard)
        │   ├── services/   # Servicios globales transversales (ej. AuthService)
        │   └── interceptors/
        │
        ├── shared/         # Elementos reutilizables transversales
        │   ├── components/ # Botones, spinners, modales genéricos
        │   └── models/     # Interfaces de datos compartidas (ej. User)
        │
        ├── layouts/        # Estructuras maestras visuales (Wrappers)
        │   └── private-layout/
        │
        └── features/       # Dominios de negocio / Páginas y Vistas autónomas
            ├── landing/    # Vista pública inicial
            └── reservas/   # Módulo de reservas encapsulado
                ├── services/
                └── models/

```

---

## 3.2. Justificación Arquitectónica y Buenas Prácticas

### 3.2.1. Gestión de Recursos Estáticos Nativa (`public/`)

Siguiendo las convenciones de las versiones más recientes de Angular, se ha adoptado el uso de la carpeta `public/` ubicada en la raíz del espacio de trabajo para el almacenamiento de todos los activos estáticos (imágenes, iconos, fuentes y archivos de configuración global como el `favicon.ico`).

*Justificación basada en buenas prácticas:* En el ecosistema moderno de Angular, la carpeta `public/` sustituye o complementa la antigua ruta `src/assets/`. Los recursos depositados en este directorio se sirven directamente desde la raíz del servidor web en tiempo de ejecución. Esto optimiza el direccionamiento en las plantillas HTML, permitiendo el uso de rutas absolutas limpias (ej. `src="images/logo.webp"`) en lugar de rutas relativas complejas, lo que reduce errores de carga en producción y mejora el rendimiento de indexación de recursos.

### 3.2.2. Núcleo de la Aplicación (`core/`)

La carpeta `core/` aloja toda la infraestructura global que debe instanciarse como **Singleton** (una única instancia para toda la aplicación). Aquí se ubican los guardianes de seguridad, los interceptores HTTP y los servicios core independientes de las vistas (como la gestión del estado de la sesión del usuario).

*Justificación de Servicios Core:* Los servicios en esta capa (como `AuthService`) administran datos que repercuten en el comportamiento global del sistema. Centralizarlos aquí evita la duplicación de lógica crítica y garantiza una única fuente de verdad para el estado de la autenticación.

### 3.2.3. Capa Reutilizable Transversal (`shared/`)

El directorio `shared/` contiene elementos puramente visuales y estructuras de datos genéricas que carecen de lógica de negocio propia, orientados a ser consumidos de manera transversal por múltiples flujos de la aplicación.

*Justificación de Modelos Globales:* En `shared/models/` se definen las interfaces de TypeScript cuyo ciclo de vida abarca más de un dominio de negocio (por ejemplo, la interfaz `User.model.ts`). Al unificar estos contratos de datos, se asegura que cualquier tipado común sea consistente en todo el proyecto, fomentando el principio **DRY (Don't Repeat Yourself)**.

### 3.2.4. Patrón de Capas de Diseño (`layouts/`)

Aborda la separación de la estructura visual del contenido dinámico. El `private-layout/` actúa como envoltorio maestro (*wrapper*) persistente para las secciones que requieren controles administrativos o paneles laterales (Header y Sidebar), delegando la inyección dinámica de las páginas a un componente `<router-outlet>` interno.

*Justificación basada en buenas prácticas:* Permite la coexistencia limpia de vistas completamente despejadas (Landing Page, Login) con el área de gestión privada, abstrayendo al componente raíz (`app.component`) de responsabilidades geométricas o estéticas de la interfaz.

### 3.2.5. Arquitectura Basada en Características (`features/`)

Se descarta la clásica organización por tipo de archivo técnico a favor de una **Organización Orientada al Dominio (Feature-Driven Architecture)**. Cada carpeta dentro de `features/` opera como un microentorno funcional y autónomo.

*Justificación de la Cohesión Local (Servicios y Modelos en Features):* Siguiendo las directrices de bajo acoplamiento, si un servicio o modelo de datos es consumido exclusivamente por una característica (por ejemplo, las peticiones a `/api/reservas` en `ReservasService` o el tipado del estado de un formulario local), estos se almacenan **dentro** de su respectiva *feature*.
Esto maximiza la cohesión: si un caso de uso requiere mantenimiento, el desarrollador tiene todo su contexto operativo (componentes, estilos, tipados y peticiones HTTP) encapsulado en un único espacio de trabajo. Si en el futuro esta funcionalidad fuera trasladada a otra aplicación, bastaría con mover la carpeta contenedora.

---

## 3.3. Componentes Standalone y Enrutamiento Eficiente

La aplicación implementa **Componentes Standalone**, el paradigma moderno de arquitectura en Angular. Al prescindir de los módulos globales de configuración (`NgModule`), cada componente declara de manera explícita y aislada sus dependencias técnicas (como la importación de `ReactiveFormsModule` únicamente donde existan formularios), aligerando la carga inicial de la aplicación.

Finalmente, el archivo `app.routes.ts` orquesta el sistema de navegación mediante la técnica de **rutas vacías con hijos agrupados**. Este patrón permite que el enrutador resuelva direcciones URL semánticas y limpias directamente desde la raíz del dominio (como `/reservas` o `/perfil`) al mismo tiempo que hereda jerárquicamente la estructura del layout privado, proveyendo un flujo de navegación eficiente, óptimo y mantenible.