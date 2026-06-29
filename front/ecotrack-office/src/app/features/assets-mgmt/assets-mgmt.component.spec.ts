import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AssetsMgmtComponent } from './assets-mgmt.component';
import { FloorService, RoomService, DeskService, UserService, NotificationService } from '@services/index.service';

describe('AssetsMgmtComponent', () => {
  let component: AssetsMgmtComponent;
  let fixture: ComponentFixture<AssetsMgmtComponent>;
  let roomServiceSpy: {
    list: ReturnType<typeof vi.fn>;
    update: ReturnType<typeof vi.fn>;
    create: ReturnType<typeof vi.fn>;
    delete: ReturnType<typeof vi.fn>;
    getIncidentsByRoomId: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    const floorServiceSpyObj = {
      list: vi.fn().mockReturnValue(of([])),
      update: vi.fn(),
      create: vi.fn(),
      delete: vi.fn()
    };

    const roomServiceSpyObj = {
      list: vi.fn().mockReturnValue(of([])),
      update: vi.fn(),
      create: vi.fn(),
      delete: vi.fn(),
      getIncidentsByRoomId: vi.fn().mockReturnValue(of([]))
    };

    const deskServiceSpyObj = {
      list: vi.fn().mockReturnValue(of([])),
      update: vi.fn(),
      create: vi.fn(),
      delete: vi.fn(),
      getIncidentsByDeskId: vi.fn().mockReturnValue(of([]))
    };

    const userServiceSpyObj = {
      getMe: vi.fn().mockReturnValue(
        of({
          id: 1,
          email: 'admin@ecotrack.local',
          fullName: 'Admin User',
          role: 'ADMIN',
          isActive: true,
          organizationId: 10
        })
      )
    };

    const notificationServiceSpyObj = {
      success: vi.fn(),
      error: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [AssetsMgmtComponent],
      providers: [
        { provide: FloorService, useValue: floorServiceSpyObj },
        { provide: RoomService, useValue: roomServiceSpyObj },
        { provide: DeskService, useValue: deskServiceSpyObj },
        { provide: UserService, useValue: userServiceSpyObj },
        { provide: NotificationService, useValue: notificationServiceSpyObj }
      ]
    }).compileComponents();

    roomServiceSpy = TestBed.inject(RoomService) as unknown as {
      list: ReturnType<typeof vi.fn>;
      update: ReturnType<typeof vi.fn>;
      create: ReturnType<typeof vi.fn>;
      delete: ReturnType<typeof vi.fn>;
      getIncidentsByRoomId: ReturnType<typeof vi.fn>;
    };

    fixture = TestBed.createComponent(AssetsMgmtComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should allow admin to update a meeting room from active to inactive in assets-mgmt', () => {
    const existingMeetingRoom = {
      id: 99,
      name: 'Meeting Room A',
      status: 'AVAILABLE',
      isActive: true,
      equipmentList: 'TV, Whiteboard',
      roomType: 'MEETING_ROOM',
      surfaceArea: 35,
      floorId: 1,
      capacity: 8
    };

    component.allRooms = [existingMeetingRoom];
    roomServiceSpy.update.mockReturnValue(of({ ...existingMeetingRoom, isActive: false }));

    component.openRoomDialog(existingMeetingRoom);
    component.roomForm.patchValue({ isActive: false });

    component.submitRoom();

    expect(roomServiceSpy.update).toHaveBeenCalledTimes(1);
    const callArgs = roomServiceSpy.update.mock.calls[0] as [number, Record<string, unknown>];
    expect(callArgs[0]).toBe(99);
    expect(callArgs[1]['name']).toBe('Meeting Room A');
    expect(callArgs[1]['roomType']).toBe('MEETING_ROOM');
    expect(callArgs[1]['isActive']).toBe(false);
    expect(component.allRooms[0].isActive).toBe(false);
    expect(component.isRoomDialogOpen).toBe(false);
  });

  it('should keep room name field enabled in edit mode', () => {
    const existingMeetingRoom = {
      id: 99,
      name: 'Meeting Room A',
      status: 'AVAILABLE',
      isActive: true,
      equipmentList: 'TV, Whiteboard',
      roomType: 'MEETING_ROOM',
      surfaceArea: 35,
      floorId: 1,
      capacity: 8
    };

    component.openRoomDialog(existingMeetingRoom);

    expect(component.roomForm.get('name')?.disabled).toBe(false);
  });

  it('should mark room as unavailable when room is inactive', () => {
    const existingMeetingRoom = {
      id: 99,
      name: 'Meeting Room A',
      status: 'AVAILABLE',
      isActive: false,
      equipmentList: 'TV, Whiteboard',
      roomType: 'MEETING_ROOM',
      surfaceArea: 35,
      floorId: 1,
      capacity: 8
    };

    component.allRooms = [existingMeetingRoom];

    expect(component.getRoomAvailability(99)).toBe(false);
  });
});
