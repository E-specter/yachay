import { ChangeDetectionStrategy, Component } from '@angular/core';

import { AppIcon } from '../../../../shared/components/app-icon/app-icon';
import { QuickActionCard } from '../../../../shared/components/quick-action-card/quick-action-card';
import { SectionCard } from '../../../../shared/components/section-card/section-card';
import { StatCard } from '../../../../shared/components/stat-card/stat-card';
import { StatusBadge } from '../../../../shared/components/status-badge/status-badge';

@Component({
  selector: 'app-dashboard',
  imports: [AppIcon, QuickActionCard, SectionCard, StatCard, StatusBadge],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Dashboard {
  readonly stats = [
    { label: 'Postulaciones en revision', value: 18, caption: '6 ingresaron esta semana', icon: 'plus', tone: 'blue' },
    { label: 'Usuarios activos', value: 816, caption: 'Administradores, docentes y alumnos', icon: 'user', tone: 'sky' },
    { label: 'Docentes habilitados', value: 46, caption: 'Con carga academica vigente', icon: 'profile', tone: 'yellow' },
    { label: 'Comunicados publicados', value: 12, caption: 'Ultimos 30 dias', icon: 'announcements', tone: 'red' },
  ] as const;

  readonly quickActions = [
    { title: 'Nueva postulacion', description: 'Revisar solicitudes pendientes de admision.', link: '/admin/postulaciones', icon: 'plus', tone: 'blue' },
    { title: 'Crear usuario', description: 'Gestionar accesos internos del campus.', link: '/admin/usuarios', icon: 'user', tone: 'sky' },
    { title: 'Ver cursos', description: 'Administrar oferta academica por nivel.', link: '/admin/cursos', icon: 'courses', tone: 'yellow' },
    { title: 'Comunicados', description: 'Publicar avisos institucionales.', link: '/admin/comunicados', icon: 'announcements', tone: 'red' },
  ] as const;

  readonly admissions = [
    { codigo: 'ADM-2026-018', postulante: 'Valeria Quispe Ramos', nivel: 'Primaria', grado: '3 Primaria', estado: 'PENDIENTE' },
    { codigo: 'ADM-2026-017', postulante: 'Mateo Salas Flores', nivel: 'Inicial', grado: '5 anos', estado: 'PENDIENTE' },
    { codigo: 'ADM-2026-016', postulante: 'Luciana Torres Vega', nivel: 'Secundaria', grado: '1 Secundaria', estado: 'ACEPTADA' },
  ] as const;

  readonly recentStudents = [
    { codigo: 'ALU-2026-1042', nombre: 'Maria Fernanda Salazar', aula: '3 Primaria B', apoderado: 'Carmen Rojas' },
    { codigo: 'ALU-2026-1041', nombre: 'Diego Paredes Soto', aula: '1 Secundaria A', apoderado: 'Miguel Paredes' },
    { codigo: 'ALU-2026-1040', nombre: 'Sofia Herrera Luna', aula: '5 anos C', apoderado: 'Rosa Luna' },
  ] as const;

  readonly activity = [
    'Se actualizo la carga de cursos de primaria.',
    'Administracion publico un comunicado general.',
    'Se habilitaron nuevas secciones para admision 2026.',
  ] as const;

  readonly events = [
    { day: '08 May', title: 'Consejo academico', detail: 'Coordinacion general' },
    { day: '10 May', title: 'Cierre de postulaciones', detail: 'Inicial y primaria' },
    { day: '13 May', title: 'Entrega de reportes', detail: 'Bimestre I' },
  ] as const;

  readonly adminCalendar = [
    { day: 'Lun', event: 'Revision de postulaciones', type: 'Admision', tone: 'bg-sky-soft/40 text-ink-dark' },
    { day: 'Mie', event: 'Consejo academico', type: 'Gestion', tone: 'bg-green-soft/30 text-ink-dark' },
    { day: 'Vie', event: 'Cierre de reportes', type: 'Evaluacion', tone: 'bg-brown/10 text-brown' },
  ] as const;
}
