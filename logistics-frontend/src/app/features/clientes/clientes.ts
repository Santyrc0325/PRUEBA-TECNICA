import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Cliente } from '../../core/services/cliente';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './clientes.html'
})
export class Clientes implements OnInit {
  private fb = inject(FormBuilder);
  private clienteService = inject(Cliente);
  private router = inject(Router);

  listaClientes: any[] = [];
  editandoId: number | null = null; // Controla si estamos editando

  clienteForm: FormGroup = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    identificacion: ['', [Validators.required]],
    direccion: ['', Validators.required],
    telefono: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  ngOnInit(): void {
    this.listar();
  }

  listar() {
    this.clienteService.listarTodos().subscribe({
      next: (data) => this.listaClientes = data,
      error: (err) => console.error('Error al cargar clientes', err)
    });
  }

  prepararEdicion(c: any) {
    this.editandoId = c.id;
    this.clienteForm.patchValue({
      nombre: c.nombre,
      identificacion: c.identificacion,
      direccion: c.direccion,
      telefono: c.telefono,
      email: c.email
    });
  }

  guardar() {
    if (this.clienteForm.invalid) return;

    if (this.editandoId) {
      // Lógica de Actualización
      this.clienteService.actualizar(this.editandoId, this.clienteForm.value).subscribe({
        next: () => {
          this.finalizarAccion('Cliente actualizado con éxito');
        },
        error: (err) => alert('Error al actualizar cliente')
      });
    } else {
      // Lógica de Creación
      this.clienteService.crear(this.clienteForm.value).subscribe({
        next: () => {
          this.finalizarAccion('Cliente registrado con éxito');
        },
        error: (err) => alert('Error: La identificación ya podría existir')
      });
    }
  }

  eliminar(id: number) {
    if (confirm('¿Estás seguro de eliminar este cliente? Esto podría fallar si tiene envíos asociados.')) {
      this.clienteService.eliminar(id).subscribe({
        next: () => this.listar(),
        error: (err) => alert('No se puede eliminar el cliente porque tiene envíos registrados.')
      });
    }
  }

  private finalizarAccion(mensaje: string) {
    alert(mensaje);
    this.editandoId = null;
    this.clienteForm.reset();
    this.listar();
  }

  cancelarEdicion() {
    this.editandoId = null;
    this.clienteForm.reset();
  }

  irAlDashboard() {
    this.router.navigate(['/dashboard']);
  }
}