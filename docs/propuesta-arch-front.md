# 3. Arquitectura del Sistema Frontend

Para el desarrollo de la aplicación web se ha diseñado e implementado una arquitectura modular en el frontend utilizando **Angular (v21+)**. El diseño sigue estrictamente las directrices de la guía oficial de estilo de Angular y los patrones arquitectónicos modernos de la industria, garantizando principios fundamentales de la ingeniería de software como la **separación de responsabilidades (Separation of Concerns)**, la **reutilización de código**, la **mantenibilidad** y la **escalabilidad** (siempre en lo posible).

A continuación se detalla la estructura del directorio principal `src/app/` y se justifica la distribución de sus componentes.

---

## 3.1. Estructura de Directorios

El núcleo del frontend se organiza de la siguiente manera:

```text
src/app/
│
├── core/                   # Módulos e infraestructura global (Singleton)
├── shared/                 # Componentes, directivas y pipes reutilizables
│
├── layouts/                # Estructuras maestras visuales (Wrappers)
│   └── private-layout/     # Layout privado (Header, Sidebar, Content)
│
├── features/               # Dominios de negocio / Páginas y Vistas
│   ├── landing/            # Vista pública inicial
│   ├── reservation/        # Gestión de reservas (Privado)
│   └── user-profile/       # Gestión de Perfil de usuario (Privado)
│
├── app.component.ts        # Componente raíz (Punto de entrada)
├── app.component.html      # Contenedor del RouterOutlet global
└── app.routes.ts           # Enrutamiento principal de la aplicación
```


---


## 3.2. Justificación Arquitectónica y Buenas Prácticas

### 3.2.1. Núcleo de la Aplicación (`core/`)

La carpeta `core/` aloja toda la infraestructura global que debe instanciarse como **Singleton** (una única instancia para toda la aplicación). Aquí se ubican:

* **Guardias de seguridad (Guards):** Controlan el flujo de navegación (ej. denegar el acceso a rutas privadas si el usuario no está autenticado).
* **Interceptores HTTP:** Capturan las peticiones hacia la API para adjuntar automáticamente tokens de autenticación o gestionar errores globales de red.
* **Servicios del núcleo:** Servicios globales de comunicación con el Backend (ej. `AuthService`).

*Justificación basada en buenas prácticas:* Aislar estos elementos en un módulo central evita la duplicación de lógica crítica de la aplicación y previene fallos de seguridad en la fuga de sesiones.

### 3.2.2. Capa Reutilizable Transversal (`shared/`)

El directorio `shared/` contiene elementos puramente visuales y genéricos que no pertenecen a ningún dominio de negocio específico, sino que son consumidos por múltiples pantallas de la aplicación. Ejemplos de ello son botones personalizados, spinners de carga, cuadros de diálogo modales o validadores de formularios reutilizables.

*Justificación basada en buenas prácticas:* Fomenta el principio **DRY (Don't Repeat Yourself)**. Al centralizar los componentes comunes, cualquier cambio estético o funcional en un botón genérico se propaga inmediatamente por todo el software, reduciendo drásticamente la deuda técnica.

### 3.2.3. Patrón de Capas de Diseño (`layouts/`)

Uno de los puntos clave del proyecto es la separación de la estructura estructural del contenido dinámico mediante el uso de Layouts. En `layouts/private-layout/` se agrupa el contenedor maestro de la zona privada de la aplicación.

* El archivo HTML y CSS de este layout define la persistencia del **Header** superior y el **Sidebar** lateral de navegación.
* Utiliza un elemento `<router-outlet>` interno para renderizar dinámicamente las páginas del usuario.

*Justificación basada en buenas prácticas:* Al extraer los layouts de la raíz (`app.component`), se consigue una experiencia de usuario limpia y desacoplada. Permite coexistir de manera elegante vistas totalmente limpias (como la `Landing Page` o la pantalla de `Login`) con vistas que requieren obligatoriamente una estructura de panel de control o *Dashboard*, delegando la responsabilidad de pintar el menú lateral únicamente a las rutas que lo requieren.

### 3.2.4. Arquitectura Basada en Características (`features/`)

En lugar de organizar el proyecto por el tipo de archivo técnico (todos los componentes juntos, todos los HTML juntos), se ha adoptado una **organización por características u objetivos de negocio (Feature-Driven Architecture)**. Cada carpeta dentro de `features/` representa una página autónoma o un caso de uso dentro de la plataforma:

* `landing/`: Se encarga de la presentación pública de la aplicación.
* `reservas/`: Agrupa toda la lógica de gestión de turnos o citas.
* `perfil/`: Maneja los datos del usuario logueado.

*Justificación basada en buenas prácticas:* Esta distribución minimiza el acoplamiento. Si un desarrollador necesita modificar la pantalla de reservas, todo el contexto que requiere (HTML, CSS, TypeScript y submódulos) está confinado en un mismo lugar. Esto optimiza el tiempo de mantenimiento y mitiga el riesgo de que una modificación en un módulo rompa colateralmente otra sección del sistema.

---

## 3.3. Componentes Standalone y Enrutamiento Eficiente

La aplicación hace uso de los **Componentes Standalone**, el estándar moderno introducido en las últimas versiones de Angular. Al eliminar los archivos pesados de configuración colectiva (`NgModule`), cada componente se autogestiona declarando explícitamente sus propias dependencias (por ejemplo, importando de forma aislada `ReactiveFormsModule` solo en los formularios de registro).

Finalmente, el archivo `app.routes.ts` orquesta la navegación utilizando **rutas vacías con hijos agrupados**. Este patrón permite mapear rutas limpias directamente en la URL del navegador (como `localhost:4200/profile` en lugar de `localhost:4200/app/profile`) mientras se mantiene por debajo el beneficio arquitectónico del `PrivateLayoutComponent`, garantizando URLs estéticas, indexables y semánticas para el usuario final.