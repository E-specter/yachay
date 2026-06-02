import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { AppIcon, AppIconName } from '../app-icon/app-icon';

type ActionTone = 'blue' | 'red' | 'yellow' | 'sky';

@Component({
  selector: 'app-quick-action-card',
  imports: [RouterLink, AppIcon],
  template: `
    <a
      [routerLink]="link()"
      class="group flex min-h-32 flex-col justify-between rounded-lg border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:border-blue hover:shadow-md"
    >
      <div class="flex items-start justify-between gap-4">
        <div
          [class]="'grid size-11 place-items-center rounded-lg text-sm font-black ' + iconClass()"
          aria-hidden="true"
        >
          <app-icon [name]="icon()" className="h-5 w-5" />
        </div>
        <app-icon name="plus" className="h-5 w-5 text-slate-300 transition group-hover:text-blue" />
      </div>

      <div class="mt-5">
        <h3 class="text-base font-black text-ink">{{ title() }}</h3>
        <p class="mt-2 text-sm font-semibold leading-6 text-slate-500">{{ description() }}</p>
      </div>
    </a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class QuickActionCard {
  readonly title = input.required<string>();
  readonly description = input.required<string>();
  readonly link = input.required<string>();
  readonly icon = input<AppIconName>('plus');
  readonly tone = input<ActionTone>('blue');

  readonly iconClass = computed(() => {
    switch (this.tone()) {
      case 'red':
        return 'bg-red/10 text-red';
      case 'yellow':
        return 'bg-yellow/30 text-ink';
      case 'sky':
        return 'bg-sky/10 text-blue';
      default:
        return 'bg-blue/10 text-blue';
    }
  });
}
