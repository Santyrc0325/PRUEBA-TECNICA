import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class Envio {
  private http = inject(HttpClient);
  
  private urlMaritima = `${environment.apiUrl}/logistica/maritima`;
  private urlTerrestre = `${environment.apiUrl}/logistica/terrestre`;

  getTerrestres(): Observable<any[]> {
    return this.http.get<any[]>(this.urlTerrestre);
  }

  getMaritimos(): Observable<any[]> {
    return this.http.get<any[]>(this.urlMaritima);
  }

  crearTerrestre(datos: any): Observable<any> {
    return this.http.post<any>(this.urlTerrestre, datos);
  }

  crearMaritimo(datos: any): Observable<any> {
    return this.http.post<any>(this.urlMaritima, datos);
  }

  eliminarTerrestre(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlTerrestre}/${id}`);
  }

  eliminarMaritimo(id: number): Observable<void> {
    return this.http.delete<void>(`${this.urlMaritima}/${id}`);
  }

actualizarTerrestre(id: number, datos: any): Observable<any> {
  return this.http.put<any>(`${this.urlTerrestre}/${id}`, datos);
}

actualizarMaritimo(id: number, datos: any): Observable<any> {
  return this.http.put<any>(`${this.urlMaritima}/${id}`, datos);
}
}