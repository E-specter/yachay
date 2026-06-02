import { ChangeDetectionStrategy, Component } from '@angular/core';

interface TeacherProfile {
  nombres: string;
  apellidos: string;
  email: string;
  documento: string;
  especialidad: string;
  telefono: string;
  estado: string;
}

@Component({
  selector: 'app-teacher-perfil',
  imports: [],
  templateUrl: './perfil.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeacherPerfil {
  readonly profile: TeacherProfile = {
    nombres: 'Rosa Elena',
    apellidos: 'Vargas Medina',
    email: 'rvargas@mgp.edu.pe',
    documento: 'DNI 45678123',
    especialidad: 'Comunicación',
    telefono: '987654320',
    estado: 'ACTIVO',
  };
}
