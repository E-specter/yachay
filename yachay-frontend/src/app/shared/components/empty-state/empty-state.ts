import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  template: `
    <div class="rounded-lg border border-dashed border-slate-300 bg-slate-50 px-6 py-10 text-center">
      <div class="mx-auto grid size-12 place-items-center rounded-lg bg-white text-sm font-black text-blue shadow-sm" aria-hidden="true">
        {{ icon() }}
      </div>
      <h3 class="mt-4 text-base font-black text-ink">{{ title() }}</h3>
      <p class="mx-auto mt-2 max-w-md text-sm font-semibold leading-6 text-slate-500">{{ message() }}</p>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmptyState {
  readonly title = input.required<string>();
  readonly message = input.required<string>();
  readonly icon = input<string>('YA');
}
