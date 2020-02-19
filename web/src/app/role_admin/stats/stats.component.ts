import {Component, OnInit, ViewChild} from '@angular/core';
import {ChartDataSets, ChartOptions, ChartType} from 'chart.js';
import {BaseChartDirective, Color, Label} from 'ng2-charts';
import {StatsService} from '../../core/controllers';

@Component({
  templateUrl: './stats.component.html',
  styleUrls: ['../../styles/root.css', '../../styles/card.css']
})
export class StatsComponent implements OnInit {

  @ViewChild('pieChart', {static: true, read: BaseChartDirective})
  public pieChart: BaseChartDirective;

  @ViewChild('barChart', {static: true, read: BaseChartDirective})
  public barChart: BaseChartDirective;

  @ViewChild('lineChart', {static: true, read: BaseChartDirective})
  public lineChart: BaseChartDirective;

  @ViewChild('barChartDay', {static: true, read: BaseChartDirective})
  public barChartDay: BaseChartDirective;

  private BUNDLE_TYPE_NAME = {
    'P': 'Allenamento Personalizzato',
    'C': 'Corsi'
  };

  private INTERVAL_NAME = {
    '1 week': '1 Settimana',
    '1 month': '1 Mese',
    '3 months': '3 Mesi',
    '6 months': '6 Mesi',
    '1 year': '1 Anno'
  };

  private DAY_OF_WEEK_NAME = {
    1: 'Lunedì',
    2: 'Martedì',
    3: 'Mercoledì',
    4: 'Giovedì',
    5: 'Venerdì',
    6: 'Sabato',
    0: 'Domenica',
  };

  public barChartOptions: ChartOptions = {
    responsive: true
  };
  public barChartLabels: Label[] = [];
  public barChartType: ChartType = 'bar';
  public barChartLegend = true;
  public barChartData: ChartDataSets[] = [{data: []}];
  public barChartDayOptions: ChartOptions = {
    responsive: true,
    scales: {
      yAxes: [
        {
          stacked: true,
        }
      ]
    }
  };
  public barChartDayLabels: Label[] = [];
  public barChartDayType: ChartType = 'bar';
  public barChartDayLegend = true;
  public barChartDayData: ChartDataSets[] = [{data: []}];

  public pieChartOptions: ChartOptions = {
    responsive: true,
    legend: {
      position: 'top',
    },
    plugins: {
      datalabels: {
        formatter: (value, ctx) => {
          const label = ctx.chart.data.labels[ctx.dataIndex];
          return label;
        },
      },
    }
  };
  public pieChartLabels: Label[] = [];
  public pieChartType: ChartType = 'pie';
  public pieChartLegend = true;
  public pieChartData: ChartDataSets[] = [{data: []}];
  public pieChartColors = [
    {
      backgroundColor: ['rgba(0,255,0,0.3)', 'rgba(0,0,255,0.3)'],
    },
  ];

  public lineChartData: ChartDataSets[] = [{data: []}];
  public lineChartLabels: Label[] = [];
  public lineChartOptions: ChartOptions = {
    responsive: true,
    scales: {
      yAxes: [
        {
          stacked: true,
        }
      ]
    }
  };
  public lineChartColors: Color[] = [
    {
      backgroundColor: ['rgba(0,255,0,0.3)', 'rgba(0,0,255,0.3)'],
    },
  ];
  public lineChartLegend = true;
  public lineChartType: ChartType = 'line';

  totalPrice: number;
  amountPayed: number;
  intervalName: string;

  constructor(private statsService: StatsService) { }

  async ngOnInit(): Promise<void> {
    await this.update();
  }

  private async getSalesByMonths(interval?) {
    const [d, error] = await this.statsService.getSalesByMonths(interval);
    if (error) {
      throw error;
    }
    this.barChartLabels = d.map(v => v.month);
    this.barChartData = [
        {data: d.map(v => v.totalprice), label: 'Totale'},
      {data: d.map(v => v.amountpayed), label: 'Pagato'}
    ];
    this.barChart.datasets = this.barChartData;
    this.barChart.labels = this.barChartLabels;
    this.barChart.chart.update();
  }

  private async getSalesByBundleType(interval?) {

    const [d, error] = await this.statsService.getSalesByBundleType(interval);
    if (error) {
      throw error;
    }
    this.pieChartLabels.length = 0;
    this.pieChartLabels = d.map(v => this.BUNDLE_TYPE_NAME[v.bundletype]);
    this.pieChartData = [{data: d.map(v => v.totalprice), label: 'Totale'}];
    this.pieChart.datasets = this.pieChartData;
    this.pieChart.labels = this.pieChartLabels;
    this.pieChart.chart.update();

    this.totalPrice = d.map(v => v.totalprice).reduce((previousValue, currentValue) => previousValue + currentValue);
    this.amountPayed = d.map(v => v.amountpayed).reduce((previousValue, currentValue) => previousValue + currentValue);
  }

  private async getReservationsByWeek(interval?) {

    const [d, error] = await this.statsService.getReservationsByWeek(interval);
    if (error) {
      throw error;
    }
    this.lineChartLabels.length = 0;
    this.lineChartLabels = d.map(v => v.week)
        .filter((v, i, a) => a.indexOf(v) === i)
        .map(v => 'Sett. ' + v)
        .sort();
    this.lineChartData = [];
    for (const key in this.BUNDLE_TYPE_NAME) {
      const data = d.filter(v => v.type === key).map(v => v.numreservations);
      this.lineChartData.push({data: data, label: this.BUNDLE_TYPE_NAME[key], stack: '1'});
    }
    this.lineChart.datasets = this.lineChartData;
    this.lineChart.labels = this.lineChartLabels;
    this.lineChart.chart.update();
  }

  private async getReservationsByDayOfWeek(interval?) {
    const [d, error] = await this.statsService.getReservationsByDayOfWeek(interval);
    if (error) {
      throw error;
    }
    this.barChartDayLabels.length = 0;
    this.barChartDayLabels = d.map(v => v.dayofweek)
        .filter((v, i, a) => a.indexOf(v) === i)
        .sort()
        .map(v => this.DAY_OF_WEEK_NAME[v]);
    this.barChartDayData = [];
    for (const key in this.BUNDLE_TYPE_NAME) {
      const data = d.filter(v => v.type === key).map(v => v.numreservations);
      this.barChartDayData.push({data: data, label: this.BUNDLE_TYPE_NAME[key], stack: '1'});
    }
    this.barChartDay.datasets = this.barChartDayData;
    this.barChartDay.labels = this.barChartDayLabels;
    this.barChartDay.chart.update();
  }

  async update(interval?: string) {
    if (!interval) {
      interval = '3 months';
    }
    this.intervalName = this.INTERVAL_NAME[interval];
    await this.getSalesByMonths(interval);
    await this.getSalesByBundleType(interval);
    await this.getReservationsByWeek(interval);
    await this.getReservationsByDayOfWeek(interval);
  }
}
