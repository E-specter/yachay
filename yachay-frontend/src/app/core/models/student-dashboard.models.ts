export interface StudentDashboardMetric {
  label: string;
  value: string;
}

export interface StudentUpcomingTask {
  id: number;
  titulo: string;
  curso: string;
  fechaEntrega: string;
  estadoEntrega: string;
}

export interface StudentRecentGrade {
  id: number;
  curso: string;
  bimestre: string;
  nota: number;
  fechaRegistro: string;
}

export interface StudentRecentAnnouncement {
  id: number;
  titulo: string;
  remitente: string;
  fechaPublicacion: string;
}

export interface StudentDashboard {
  metrics: StudentDashboardMetric[];
  upcomingTasks: StudentUpcomingTask[];
  recentGrades: StudentRecentGrade[];
  announcements: StudentRecentAnnouncement[];
}
