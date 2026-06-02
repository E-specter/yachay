import { ChangeDetectionStrategy, Component } from '@angular/core';

interface StudentProfile {
  codigo: string;
  nombres: string;
  apellidos: string;
  documento: string;
  correoInstitucional: string;
  nivel: string;
  grado: string;
  seccion: string;
  apoderado: string;
  correoApoderado: string;
  celularApoderado: string;
}

@Component({
  selector: 'app-student-perfil',
  imports: [],
  templateUrl: './perfil.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StudentPerfil {
  readonly profile: StudentProfile = {
    codigo: 'ALU-2026-0001',
    nombres: 'María Fernanda',
    apellidos: 'Salazar Rojas',
    documento: 'DNI 81234567',
    correoInstitucional: 'msalazar@mgp.edu.pe',
    nivel: 'Primaria',
    grado: '3° Primaria',
    seccion: 'B',
    apoderado: 'Rosa Rojas Pérez',
    correoApoderado: 'rosa.rojas@example.com',
    celularApoderado: '987654321',
  };
}
