import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';
import { StudentPortalService } from '../../../../core/services/student-portal';

interface StudentProfile { codigo: string; nombres: string; apellidos: string; documento: string; correoInstitucional: string; nivel: string; grado: string; seccion: string; apoderado: string; correoApoderado: string; celularApoderado: string; institucion: string; }
const EMPTY: StudentProfile = { codigo: '', nombres: '', apellidos: '', documento: '', correoInstitucional: '', nivel: '', grado: '', seccion: '', apoderado: '', correoApoderado: '', celularApoderado: '', institucion: '' };

@Component({ selector: 'app-student-perfil', imports: [], templateUrl: './perfil.html', changeDetection: ChangeDetectionStrategy.OnPush })
export class StudentPerfil implements OnInit {
  private readonly portal = inject(StudentPortalService); readonly profile = signal<StudentProfile>(EMPTY); readonly errorMessage = signal('');
  ngOnInit(): void { this.portal.getProfile<StudentProfile>().subscribe({ next: (item) => this.profile.set(item), error: () => this.errorMessage.set('No se pudo cargar el perfil.') }); }
}
