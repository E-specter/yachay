export interface TeacherDashboardMetric {
  label: string;
  value: number;
}

export interface TeacherReviewTask {
  id: number;
  titulo: string;
  curso: string;
  aula: string;
  fechaEntrega: string;
  pendientes: number;
}

export interface TeacherDashboardAnnouncement {
  id: number;
  titulo: string;
  fechaPublicacion: string;
  estado: string;
}

export interface TeacherAssignedCourse {
  id: number;
  codigo: string;
  nombre: string;
  aula: string;
  cantidadAlumnos: number;
}

export interface TeacherDashboard {
  metrics: TeacherDashboardMetric[];
  reviewTasks: TeacherReviewTask[];
  announcements: TeacherDashboardAnnouncement[];
  courses: TeacherAssignedCourse[];
}
