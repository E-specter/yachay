import { ChangeDetectionStrategy, Component, input } from '@angular/core';

export type AppIconName =
  | 'menu'
  | 'close'
  | 'dashboard'
  | 'courses'
  | 'tasks'
  | 'grades'
  | 'announcements'
  | 'profile'
  | 'logout'
  | 'calendar'
  | 'book'
  | 'notification'
  | 'chart'
  | 'user'
  | 'settings'
  | 'chevron-down'
  | 'chevron-left'
  | 'chevron-right'
  | 'arrow-left'
  | 'plus'
  | 'search'
  | 'clock'
  | 'check'
  | 'alert';

@Component({
  selector: 'app-icon',
  template: `
    @switch (name()) {
      @case ('menu') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" aria-hidden="true">
          <path d="M4 7h16" />
          <path d="M4 12h16" />
          <path d="M4 17h16" />
        </svg>
      }
      @case ('close') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" aria-hidden="true">
          <path d="M6 6l12 12" />
          <path d="M18 6L6 18" />
        </svg>
      }
      @case ('dashboard') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <rect x="3" y="3" width="7" height="8" rx="1.5" />
          <rect x="14" y="3" width="7" height="5" rx="1.5" />
          <rect x="14" y="12" width="7" height="9" rx="1.5" />
          <rect x="3" y="15" width="7" height="6" rx="1.5" />
        </svg>
      }
      @case ('courses') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H21" />
          <path d="M6.5 2H21v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2Z" />
          <path d="M9 6h8" />
          <path d="M9 10h6" />
        </svg>
      }
      @case ('tasks') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <rect x="4" y="3" width="16" height="18" rx="2" />
          <path d="m8 9 1.5 1.5L12 8" />
          <path d="M14 10h3" />
          <path d="m8 15 1.5 1.5L12 14" />
          <path d="M14 16h3" />
        </svg>
      }
      @case ('grades') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M4 19V5a2 2 0 0 1 2-2h12" />
          <path d="M8 7h8" />
          <path d="M8 11h6" />
          <path d="M8 15h4" />
          <path d="m16 15 1.5 1.5L21 13" />
        </svg>
      }
      @case ('announcements') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M4 11v2a3 3 0 0 0 3 3h1l3 4v-4h2l7 3V5l-7 3H7a3 3 0 0 0-3 3Z" />
          <path d="M13 8v8" />
        </svg>
      }
      @case ('profile') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="12" cy="8" r="4" />
          <path d="M5 21a7 7 0 0 1 14 0" />
        </svg>
      }
      @case ('logout') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M10 17l5-5-5-5" />
          <path d="M15 12H3" />
          <path d="M21 3v18" />
        </svg>
      }
      @case ('calendar') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <rect x="3" y="5" width="18" height="16" rx="2" />
          <path d="M16 3v4" />
          <path d="M8 3v4" />
          <path d="M3 10h18" />
          <path d="M8 14h.01" />
          <path d="M12 14h.01" />
          <path d="M16 14h.01" />
        </svg>
      }
      @case ('book') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 6.5A6.5 6.5 0 0 0 5.5 3H4v16h1.5A6.5 6.5 0 0 1 12 22" />
          <path d="M12 6.5A6.5 6.5 0 0 1 18.5 3H20v16h-1.5A6.5 6.5 0 0 0 12 22" />
          <path d="M12 6.5V22" />
        </svg>
      }
      @case ('notification') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9" />
          <path d="M10 21h4" />
        </svg>
      }
      @case ('chart') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M4 20V4" />
          <path d="M4 20h16" />
          <path d="M8 16v-5" />
          <path d="M12 16V8" />
          <path d="M16 16v-3" />
        </svg>
      }
      @case ('user') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="9" cy="8" r="4" />
          <path d="M2 21a7 7 0 0 1 14 0" />
          <path d="M17 11h5" />
          <path d="M19.5 8.5v5" />
        </svg>
      }
      @case ('settings') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 15.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Z" />
          <path d="M19.4 15a1.8 1.8 0 0 0 .36 1.98l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06A1.8 1.8 0 0 0 15 19.4a1.8 1.8 0 0 0-1 .6 1.8 1.8 0 0 0-.4 1.1V21a2 2 0 0 1-4 0v-.09A1.8 1.8 0 0 0 8.5 19.3a1.8 1.8 0 0 0-1.98.36l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06A1.8 1.8 0 0 0 4.6 15a1.8 1.8 0 0 0-.6-1 1.8 1.8 0 0 0-1.1-.4H3a2 2 0 0 1 0-4h.09A1.8 1.8 0 0 0 4.7 8.5a1.8 1.8 0 0 0-.36-1.98l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06A1.8 1.8 0 0 0 9 4.6a1.8 1.8 0 0 0 1-.6 1.8 1.8 0 0 0 .4-1.1V3a2 2 0 0 1 4 0v.09A1.8 1.8 0 0 0 15.5 4.7a1.8 1.8 0 0 0 1.98-.36l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06A1.8 1.8 0 0 0 19.4 9c.4.2.72.52.9.9.16.35.52.58.9.58H21a2 2 0 0 1 0 4h-.09a1.8 1.8 0 0 0-1.51.52Z" />
        </svg>
      }
      @case ('chevron-down') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="m6 9 6 6 6-6" />
        </svg>
      }
      @case ('chevron-left') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="m15 18-6-6 6-6" />
        </svg>
      }
      @case ('chevron-right') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="m9 18 6-6-6-6" />
        </svg>
      }
      @case ('arrow-left') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M19 12H5" />
          <path d="m12 19-7-7 7-7" />
        </svg>
      }
      @case ('plus') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 5v14" />
          <path d="M5 12h14" />
        </svg>
      }
      @case ('search') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="11" cy="11" r="7" />
          <path d="m20 20-3.5-3.5" />
        </svg>
      }
      @case ('clock') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7v5l3 2" />
        </svg>
      }
      @case ('check') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="m5 12 4 4L19 6" />
        </svg>
      }
      @case ('alert') {
        <svg [class]="className()" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
          <path d="M12 9v4" />
          <path d="M12 17h.01" />
          <path d="M10.3 4.2 2.8 18a2 2 0 0 0 1.7 3h15a2 2 0 0 0 1.7-3L13.7 4.2a2 2 0 0 0-3.4 0Z" />
        </svg>
      }
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AppIcon {
  readonly name = input.required<AppIconName>();
  readonly className = input('h-5 w-5');
}
