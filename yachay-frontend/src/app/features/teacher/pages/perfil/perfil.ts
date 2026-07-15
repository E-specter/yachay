import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { TeacherPortalService } from '../../../../core/services/teacher-portal';

interface TeacherProfile { codigo: string; nombres: string; apellidos: string; documento: string; correoInstitucional: string; especialidad: string; telefono: string; fechaContratacion: string; institucion: string; }
const EMPTY: TeacherProfile = { codigo: '', nombres: '', apellidos: '', documento: '', correoInstitucional: '', especialidad: '', telefono: '', fechaContratacion: '', institucion: '' };

@Component({ selector: 'app-teacher-perfil', imports: [], templateUrl: './perfil.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class TeacherPerfil implements OnInit {
  private readonly portal = inject(TeacherPortalService); readonly profile = signal<TeacherProfile>(EMPTY); readonly errorMessage = signal('');
  ngOnInit(): void { this.portal.getProfile<TeacherProfile>().subscribe({ next: (item) => this.profile.set(item), error: () => this.errorMessage.set('No se pudo cargar el perfil docente.') }); }
}
