import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading',
  imports: [],
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.css'
})
export class LoadingComponent {
  message = input<string>('Se încarcă...');

  // 'inline' = spinner mic, în interiorul unui card/secțiune
  // 'overlay' = acoperă tot ecranul, peste tot conținutul
  variant = input<'inline' | 'overlay'>('inline');

}
