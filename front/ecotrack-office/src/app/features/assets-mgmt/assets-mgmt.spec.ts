import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssetsMgmt } from './assets-mgmt';

describe('AssetsMgmt', () => {
  let component: AssetsMgmt;
  let fixture: ComponentFixture<AssetsMgmt>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssetsMgmt],
    }).compileComponents();

    fixture = TestBed.createComponent(AssetsMgmt);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
