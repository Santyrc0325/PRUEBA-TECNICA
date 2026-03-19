import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Auth } from '../../core/services/auth';
import { Envio } from '../../core/services/envio';
import { Cliente } from '../../core/services/cliente';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class Dashboard implements OnInit {
  private fb = inject(FormBuilder);
  private authService = inject(Auth);
  private envioService = inject(Envio);
  private clienteService = inject(Cliente);
  private router = inject(Router);

  editandoId: number | null = null;

  listaTerrestres: any[] = [];
  listaMaritimos: any[] = [];
  listaClientes: any[] = [];
  
 terrestreForm: FormGroup = this.fb.group({
    clienteId: ['', Validators.required],
    numeroGuia: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]{10}$')]],
    tipoProducto: ['', Validators.required],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    placaVehiculo: ['', [Validators.required, Validators.pattern('^[a-zA-Z]{3}[0-9]{3}$')]],
    bodegaEntrega: ['', Validators.required],
    precioEnvio: [0, [Validators.required, Validators.min(1)]],
    fechaEntrega: [new Date().toISOString().slice(0, 16), Validators.required]
  });

  maritimoForm: FormGroup = this.fb.group({
    clienteId: ['', Validators.required],
    numeroGuia: ['', [Validators.required, Validators.pattern('^[a-zA-Z0-9]{10}$')]],
    tipoProducto: ['', Validators.required],
    cantidad: [1, [Validators.required, Validators.min(1)]],
    numeroFlota: ['', [Validators.required, Validators.pattern('^[a-zA-Z]{3}[0-9]{4}[a-zA-Z]{1}$')]],
    puertoEntrega: ['', Validators.required],
    precioEnvio: [0, [Validators.required, Validators.min(1)]],
    fechaEntrega: [new Date().toISOString().slice(0, 16), Validators.required]
  });

  prepararEdicion(item: any, tipo: 'terrestre' | 'maritimo') {
  this.editandoId = item.id;
  
  if (tipo === 'terrestre') {
    this.terrestreForm.patchValue({
      clienteId: item.clienteId || item.cliente?.id,
      numeroGuia: item.numeroGuia,
      tipoProducto: item.tipoProducto,
      cantidad: item.cantidad,
      placaVehiculo: item.placaVehiculo,
      bodegaEntrega: item.bodegaEntrega,
      precioEnvio: item.precioEnvio,
      fechaEntrega: item.fechaEntrega?.slice(0, 16)
    });
  } else {
    this.maritimoForm.patchValue({
      clienteId: item.clienteId || item.cliente?.id,
      numeroGuia: item.numeroGuia,
      tipoProducto: item.tipoProducto,
      cantidad: item.cantidad,
      numeroFlota: item.numeroFlota,
      puertoEntrega: item.puertoEntrega,
      precioEnvio: item.precioEnvio,
      fechaEntrega: item.fechaEntrega?.slice(0, 16)
    });
  }
}

  ngOnInit(): void {
    this.cargarDatos();
    this.cargarClientes();
  }

  cargarClientes() {
    this.clienteService.listarTodos().subscribe(data => this.listaClientes = data);
  }

  cargarDatos() {
    this.envioService.getTerrestres().subscribe(data => this.listaTerrestres = data);
    this.envioService.getMaritimos().subscribe(data => this.listaMaritimos = data);
  }

  guardarTerrestre() {
  if (this.terrestreForm.valid) {
    if (this.editandoId) {
      this.envioService.actualizarTerrestre(this.editandoId, this.terrestreForm.value).subscribe({
        next: () => {
          this.finalizarAccion('Envío actualizado correctamente');
        },
        error: (err) => console.error('Error al actualizar:', err)
      });
    } else {
      this.envioService.crearTerrestre(this.terrestreForm.value).subscribe(() => {
        this.finalizarAccion('Envío creado correctamente');
      });
    }
  }
}

  guardarMaritimo() {
    if (this.maritimoForm.invalid) return;

    if (this.editandoId) {
      this.envioService.actualizarMaritimo(this.editandoId, this.maritimoForm.value).subscribe({
        next: () => this.finalizarAccion('Envío marítimo actualizado con éxito'),
        error: (err) => console.error('Error al actualizar marítimo:', err)
      });
    } else {
      this.envioService.crearMaritimo(this.maritimoForm.value).subscribe({
        next: () => this.finalizarAccion('Envío marítimo creado con éxito'),
        error: (err) => console.error('Error al crear marítimo:', err)
      });
    }
  }

  eliminar(id: number, tipo: 'terrestre' | 'maritimo') {
    if (confirm('¿Estás seguro de eliminar este envío?')) {
      const peticion = tipo === 'terrestre' 
        ? this.envioService.eliminarTerrestre(id) 
        : this.envioService.eliminarMaritimo(id);

      peticion.subscribe(() => this.cargarDatos());
    }
  }

  private finalizarAccion(msj: string) {
    alert(msj);
    this.editandoId = null;
    this.terrestreForm.reset({cantidad: 1});
    this.maritimoForm.reset({cantidad: 1});
    this.cargarDatos();
  }

  nuevoEnvio() {
  this.editandoId = null;
  this.terrestreForm.reset({ cantidad: 1, precioEnvio: 0 });
  this.maritimoForm.reset({ cantidad: 1, precioEnvio: 0 });
}

  irAClientes() {
    this.router.navigate(['/clientes']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}