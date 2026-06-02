import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-section-card',
  imports: [RouterLink],
  template: `
    <section class="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
      <div class="flex flex-col gap-3 border-b border-slate-200 px-5 py-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 class="text-lg font-black text-ink">{{ title() }}</h2>
          @if (subtitle()) {
            <p class="mt-1 text-sm font-semibold leading-6 text-slate-500">{{ subtitle() }}</p>
          }
        </div>

        @if (actionLabel() && actionLink()) {
          <a
            [routerLink]="actionLink()"
            class="inline-flex min-h-10 items-center justify-center rounded-lg border border-blue px-4 text-sm font-extrabold text-blue transition hover:bg-blue hover:text-white"
          >
            {{ actionLabel() }}
          </a>
        }
      </div>

      <div class="p-5">
        <ng-content></ng-content>
      </div>
    </section>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SectionCard {
  readonly title = input.required<string>();
  readonly subtitle = input<string>('');
  readonly actionLabel = input<string>('');
  readonly actionLink = input<string>('');
}
