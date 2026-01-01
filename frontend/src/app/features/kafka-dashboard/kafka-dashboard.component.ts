import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HeaderComponent } from '../../shared/header/header.component';

declare var SmoothieChart: any;
declare var TimeSeries: any;

@Component({
  selector: 'app-kafka-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, HeaderComponent],
  templateUrl: './kafka-dashboard.component.html',
  styleUrls: ['./kafka-dashboard.component.css']
})
export class KafkaDashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  error: string | null = null;
  private smoothieChart: any;
  private billingSeries: any;
  private supplierSeries: any;
  private eventSource: EventSource | null = null;

  ngOnInit() {
    // Component initialization
  }

  ngAfterViewInit() {
    this.initializeChart();
    this.startStreaming();
  }

  ngOnDestroy() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  }

  private initializeChart() {
    const canvas = document.getElementById('kafkaChart') as HTMLCanvasElement;
    if (!canvas) {
      this.error = 'Canvas element not found';
      return;
    }

    // Initialize SmoothieChart with adaptive scaling
    // SmoothieChart s'adapte automatiquement, mais on fixe un max initial pour la visibilité
    this.smoothieChart = new SmoothieChart({
      tooltip: true,
      minValue: 0,
      maxValue: 5, // Max initial de 5 pour bien voir les petites variations (0, 1, 2, 3...)
      scaleSmoothing: 0.125, // Transition douce lors du changement d'échelle
      grid: {
        strokeStyle: 'rgba(125, 125, 125, 0.3)',
        fillStyle: 'rgba(0, 0, 0, 0.1)',
        lineWidth: 1,
        millisPerLine: 1000,
        verticalSections: 5 // 5 sections pour 0, 1, 2, 3, 4, 5
      },
      labels: {
        fillStyle: 'rgb(200, 200, 200)',
        fontSize: 12,
        precision: 0
      },
      timestampFormatter: SmoothieChart.timeFormatter
    });

    // Create time series for Billing and Supplier
    this.billingSeries = new TimeSeries();
    this.supplierSeries = new TimeSeries();

    // Add time series to chart with different colors
    this.smoothieChart.addTimeSeries(this.billingSeries, {
      strokeStyle: 'rgba(0, 255, 0, 1)',
      fillStyle: 'rgba(0, 255, 0, 0.3)',
      lineWidth: 3
    });

    this.smoothieChart.addTimeSeries(this.supplierSeries, {
      strokeStyle: 'rgba(255, 0, 0, 1)',
      fillStyle: 'rgba(255, 0, 0, 0.3)',
      lineWidth: 3
    });

    // Stream to canvas
    this.smoothieChart.streamTo(canvas, 500);
  }

  private startStreaming() {
    const baseUrl = 'http://localhost:8090';
    const url = `${baseUrl}/api/kafka/analyticsAggregate`;

    this.eventSource = new EventSource(url);

    this.eventSource.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        const now = new Date().getTime();

        // Update time series with new values
        if (data.Billing !== undefined) {
          this.billingSeries.append(now, data.Billing);
        }
        if (data.Supplier !== undefined) {
          this.supplierSeries.append(now, data.Supplier);
        }
      } catch (err) {
        console.error('Error parsing event data:', err);
      }
    };

    this.eventSource.onerror = (error) => {
      console.error('EventSource error:', error);
      this.error = 'Failed to connect to Kafka analytics stream. Make sure the Data Analytics service is running.';
      if (this.eventSource) {
        this.eventSource.close();
      }
    };
  }
}
