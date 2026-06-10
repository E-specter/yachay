import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth';
import { roleGuard } from './core/guards/role';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login').then((m) => m.Login),
    title: 'Iniciar sesión | Yachay',
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/admission/pages/register/register').then(
        (m) => m.Register,
      ),
    title: 'Admisión | Yachay',
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('./features/auth/pages/forgot-password/forgot-password').then(
        (m) => m.ForgotPassword,
      ),
    title: 'Recuperar contraseña | Yachay',
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('./features/auth/pages/reset-password/reset-password').then(
        (m) => m.ResetPassword,
      ),
    title: 'Restablecer contraseña | Yachay',
  },
  {
    path: 'admin',
    loadComponent: () =>
      import('./features/admin/layout/admin-layout/admin-layout').then(
        (m) => m.AdminLayout,
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMINISTRADOR'] },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/admin/pages/dashboard/dashboard').then(
            (m) => m.Dashboard,
          ),
        title: 'Dashboard | Yachay',
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/admin/pages/perfil/perfil').then(
            (m) => m.AdminPerfil,
          ),
        title: 'Perfil administrador | Yachay',
      },
      {
        path: 'postulaciones',
        loadComponent: () =>
          import('./features/admin/pages/postulaciones/postulaciones').then(
            (m) => m.Postulaciones,
          ),
        title: 'Postulaciones | Yachay',
      },
      {
        path: 'usuarios',
        loadComponent: () =>
          import('./features/admin/pages/usuarios/usuarios').then(
            (m) => m.Usuarios,
          ),
        title: 'Usuarios | Yachay',
      },
      {
        path: 'alumnos',
        loadComponent: () =>
          import('./features/admin/pages/alumnos/alumnos').then(
            (m) => m.Alumnos,
          ),
        title: 'Alumnos | Yachay',
      },
      {
        path: 'docentes',
        loadComponent: () =>
          import('./features/admin/pages/docentes/docentes').then(
            (m) => m.Docentes,
          ),
        title: 'Docentes | Yachay',
      },
      {
        path: 'cursos',
        loadComponent: () =>
          import('./features/admin/pages/cursos/cursos').then((m) => m.Cursos),
        title: 'Cursos | Yachay',
      },
      {
        path: 'secciones',
        loadComponent: () =>
          import('./features/admin/pages/secciones/secciones').then(
            (m) => m.Secciones,
          ),
        title: 'Secciones | Yachay',
      },
      {
        path: 'tareas',
        loadComponent: () =>
          import('./features/admin/pages/tareas/tareas').then((m) => m.Tareas),
        title: 'Tareas | Yachay',
      },
      {
        path: 'notas',
        loadComponent: () =>
          import('./features/admin/pages/notas/notas').then((m) => m.Notas),
        title: 'Notas | Yachay',
      },
      {
        path: 'calendario',
        loadComponent: () =>
          import('./features/admin/pages/calendario/calendario').then(
            (m) => m.AdminCalendario,
          ),
        title: 'Calendario | Yachay',
      },
      {
        path: 'comunicados',
        loadComponent: () =>
          import('./features/admin/pages/comunicados/comunicados').then(
            (m) => m.Comunicados,
          ),
        title: 'Comunicados | Yachay',
      },
      {
        path: 'notificaciones',
        loadComponent: () =>
          import('./features/admin/pages/notificaciones/notificaciones').then(
            (m) => m.AdminNotificaciones,
          ),
        title: 'Notificaciones | Yachay',
      },
      {
        path: 'configuracion',
        loadComponent: () =>
          import('./features/admin/pages/configuracion/configuracion').then(
            (m) => m.AdminConfiguracion,
          ),
        title: 'Configuración | Yachay',
      },
    ],
  },
  {
    path: 'docente',
    loadComponent: () =>
      import('./features/teacher/layout/teacher-layout/teacher-layout').then(
        (m) => m.TeacherLayout,
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['DOCENTE'] },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/teacher/pages/dashboard/dashboard').then(
            (m) => m.TeacherDashboardPage,
          ),
        title: 'Dashboard docente | Yachay',
      },
      {
        path: 'cursos',
        loadComponent: () =>
          import('./features/teacher/pages/cursos/cursos').then(
            (m) => m.TeacherCursos,
          ),
        title: 'Mis cursos | Yachay',
      },
      {
        path: 'alumnos',
        loadComponent: () =>
          import('./features/teacher/pages/alumnos/alumnos').then(
            (m) => m.TeacherAlumnos,
          ),
        title: 'Mis alumnos | Yachay',
      },
      {
        path: 'tareas',
        loadComponent: () =>
          import('./features/teacher/pages/tareas/tareas').then(
            (m) => m.TeacherTareas,
          ),
        title: 'Tareas docente | Yachay',
      },
      {
        path: 'notas',
        loadComponent: () =>
          import('./features/teacher/pages/notas/notas').then(
            (m) => m.TeacherNotas,
          ),
        title: 'Notas docente | Yachay',
      },
      {
        path: 'calendario',
        loadComponent: () =>
          import('./features/teacher/pages/calendario/calendario').then(
            (m) => m.TeacherCalendario,
          ),
        title: 'Calendario docente | Yachay',
      },
      {
        path: 'comunicados',
        loadComponent: () =>
          import('./features/teacher/pages/comunicados/comunicados').then(
            (m) => m.TeacherComunicados,
        ),
        title: 'Comunicados docente | Yachay',
      },
      {
        path: 'notificaciones',
        loadComponent: () =>
          import('./features/teacher/pages/notificaciones/notificaciones').then(
            (m) => m.TeacherNotificaciones,
          ),
        title: 'Notificaciones docente | Yachay',
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/teacher/pages/perfil/perfil').then(
            (m) => m.TeacherPerfil,
          ),
        title: 'Perfil docente | Yachay',
      },
    ],
  },
  {
    path: 'alumno',
    loadComponent: () =>
      import('./features/student/layout/student-layout/student-layout').then(
        (m) => m.StudentLayout,
      ),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ALUMNO'] },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/student/pages/dashboard/dashboard').then(
            (m) => m.StudentDashboardPage,
          ),
        title: 'Dashboard alumno | Yachay',
      },
      {
        path: 'cursos',
        loadComponent: () =>
          import('./features/student/pages/cursos/cursos').then(
            (m) => m.StudentCursos,
          ),
        title: 'Mis cursos | Yachay',
      },
      {
        path: 'cursos/:id',
        loadComponent: () =>
          import('./features/student/pages/curso-detalle/curso-detalle').then(
            (m) => m.CursoDetalle,
          ),
        title: 'Detalle de curso | Yachay',
      },
      {
        path: 'calendario',
        loadComponent: () =>
          import('./features/student/pages/calendario/calendario').then(
            (m) => m.StudentCalendario,
          ),
        title: 'Calendario alumno | Yachay',
      },
      {
        path: 'tareas',
        loadComponent: () =>
          import('./features/student/pages/tareas/tareas').then(
            (m) => m.StudentTareas,
          ),
        title: 'Tareas alumno | Yachay',
      },
      {
        path: 'tareas/:id',
        loadComponent: () =>
          import('./features/student/pages/tarea-detalle/tarea-detalle').then(
            (m) => m.TareaDetalle,
          ),
        title: 'Detalle de tarea | Yachay',
      },
      {
        path: 'notas',
        loadComponent: () =>
          import('./features/student/pages/notas/notas').then(
            (m) => m.StudentNotas,
          ),
        title: 'Notas alumno | Yachay',
      },
      {
        path: 'comunicados',
        loadComponent: () =>
          import('./features/student/pages/comunicados/comunicados').then(
            (m) => m.StudentComunicados,
        ),
        title: 'Comunicados alumno | Yachay',
      },
      {
        path: 'notificaciones',
        loadComponent: () =>
          import('./features/student/pages/notificaciones/notificaciones').then(
            (m) => m.StudentNotificaciones,
          ),
        title: 'Notificaciones alumno | Yachay',
      },
      {
        path: 'comunicados/:id',
        loadComponent: () =>
          import('./features/student/pages/comunicado-detalle/comunicado-detalle').then(
            (m) => m.ComunicadoDetalle,
          ),
        title: 'Detalle de comunicado | Yachay',
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/student/pages/perfil/perfil').then(
            (m) => m.StudentPerfil,
          ),
        title: 'Perfil alumno | Yachay',
      },
    ],
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];
