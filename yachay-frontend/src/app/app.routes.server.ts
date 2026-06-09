import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: 'alumno/cursos/:id',
    renderMode: RenderMode.Server,
  },
  {
    path: 'alumno/tareas/:id',
    renderMode: RenderMode.Server,
  },
  {
    path: 'alumno/comunicados/:id',
    renderMode: RenderMode.Server,
  },
  {
    path: '**',
    renderMode: RenderMode.Prerender,
  },
];
