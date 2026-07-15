import { NgModule } from '@angular/core';
import { getTestBed, ɵgetCleanupHook as getCleanupHook } from '@angular/core/testing';
import { BrowserTestingModule, platformBrowserTesting } from '@angular/platform-browser/testing';
import { afterEach, beforeEach } from 'vitest';

beforeEach(getCleanupHook(false));
afterEach(getCleanupHook(true));

const setupKey = Symbol.for('@yachay/testbed-setup');
const state = globalThis as typeof globalThis & { [setupKey]?: boolean };

if (!state[setupKey]) {
  state[setupKey] = true;

  class TestModule {}
  NgModule({ providers: [] })(TestModule);

  getTestBed().initTestEnvironment(
    [BrowserTestingModule, TestModule],
    platformBrowserTesting(),
    {
      errorOnUnknownElements: true,
      errorOnUnknownProperties: true,
    },
  );
}
