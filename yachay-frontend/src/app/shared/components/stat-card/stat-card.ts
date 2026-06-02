import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';

import { AppIcon, type AppIconName } from '../app-icon/app-icon';

type StatTone = 'blue' | 'red' | 'yellow' | 'sky' | 'ink';

@Component({
  selector: 'app-stat-card',
  imports: [AppIcon],
  template: `
    <article
      class="relative overflow-hidden rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
    >
      <div [class]="'absolute inset-x-0 top-0 h-1.5 ' + barClass()"></div>

      <div class="flex items-start justify-between gap-4">
        <div>
          <p class="text-sm font-extrabold text-slate-600">{{ label() }}</p>
          <p class="mt-3 text-3xl font-black tracking-normal text-ink">{{ value() }}</p>
        </div>

        <div
          [class]="'grid size-12 place-items-center rounded-lg text-sm font-black ' + iconClass()"
          aria-hidden="true"
        >
          <app-icon [name]="icon()" className="h-5 w-5" />
        </div>
      </div>

      @if (caption()) {
        <p class="mt-4 text-sm font-semibold leading-6 text-slate-500">{{ caption() }}</p>
      }
    </article>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatCard {
  readonly label = input.required<string>();
  readonly value = input.required<string | number>();
  readonly caption = input<string>('');
  readonly icon = input<AppIconName>('chart');
  readonly tone = input<StatTone>('blue');

  readonly barClass = computed(() => {
    switch (this.tone()) {
      case 'red':
        return 'bg-red';
      case 'yellow':
        return 'bg-yellow';
      case 'sky':
        return 'bg-sky';
      case 'ink':
        return 'bg-ink';
      default:
        return 'bg-blue';
    }
  });

  readonly iconClass = computed(() => {
    switch (this.tone()) {
      case 'red':
        return 'bg-red/10 text-red';
      case 'yellow':
        return 'bg-yellow/30 text-ink';
      case 'sky':
        return 'bg-sky/10 text-blue';
      case 'ink':
        return 'bg-ink/10 text-ink';
      default:
        return 'bg-blue/10 text-blue';
    }
  });
}
