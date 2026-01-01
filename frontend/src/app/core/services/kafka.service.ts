import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, interval } from 'rxjs';
import { switchMap, startWith } from 'rxjs/operators';

export interface KafkaEvent {
  topic: string;
  key: string;
  message: string;
  timestamp: number;
}

export interface KafkaEventStats {
  totalEvents: number;
  billingEvents: number;
  supplierEvents: number;
  inventoryEvents: number;
}

@Injectable({
  providedIn: 'root'
})
export class KafkaService {
  private baseUrl = 'http://localhost:8090/api/kafka';

  constructor(private http: HttpClient) { }

  getEvents(): Observable<KafkaEvent[]> {
    return this.http.get<KafkaEvent[]>(`${this.baseUrl}/events`);
  }

  getEventStats(): Observable<KafkaEventStats> {
    return this.http.get<KafkaEventStats>(`${this.baseUrl}/events/count`);
  }

  // Stream events with polling every 2 seconds
  streamEvents(): Observable<KafkaEvent[]> {
    return interval(2000).pipe(
      startWith(0),
      switchMap(() => this.getEvents())
    );
  }

  // Stream stats with polling every 2 seconds
  streamStats(): Observable<KafkaEventStats> {
    return interval(2000).pipe(
      startWith(0),
      switchMap(() => this.getEventStats())
    );
  }
}

