import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth';

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
    canActivate: [authGuard],
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
        path: 'comunicados',
        loadComponent: () =>
          import('./features/admin/pages/comunicados/comunicados').then(
            (m) => m.Comunicados,
          ),
        title: 'Comunicados | Yachay',
      },
    ],
  },
  {
    path: 'docente',
    loadComponent: () =>
      import('./features/teacher/layout/teacher-layout/teacher-layout').then(
        (m) => m.TeacherLayout,
      ),
    canActivate: [authGuard],
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
        path: 'comunicados',
        loadComponent: () =>
          import('./features/teacher/pages/comunicados/comunicados').then(
            (m) => m.TeacherComunicados,
          ),
        title: 'Comunicados docente | Yachay',
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
    canActivate: [authGuard],
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
        path: 'tareas',
        loadComponent: () =>
          import('./features/student/pages/tareas/tareas').then(
            (m) => m.StudentTareas,
          ),
        title: 'Tareas alumno | Yachay',
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
