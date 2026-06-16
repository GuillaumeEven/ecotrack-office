import { Injectable } from '@angular/core';
// import { BehaviorSubject, Observable } from 'rxjs';

// import type { User } from '../models/user';
// import { getCurrentUser } from '../config/user-stub';

// /**
//  * User Service
//  * Manages current user data and authentication state
//  *
//  * TODO: Once authentication is implemented, integrate with a login service
//  * and replace stub user with real API calls
//  */
// @Injectable({
//   providedIn: 'root'
// })
// export class UserService {
//   private currentUser$ = new BehaviorSubject<User | null>(getCurrentUser());

//   constructor() {
//     // Initialize with stub user
//     // In the future, this should call an auth service to fetch the real user
//   }

//   /**
//    * Get the current user as an Observable
//    */
//   getCurrentUser(): Observable<User | null> {
//     return this.currentUser$.asObservable();
//   }

//   /**
//    * Get the current user synchronously (if already loaded)
//    */
//   getCurrentUserSync(): User | null {
//     return this.currentUser$.value;
//   }

//   /**
//    * Set the current user (used after login)
//    * TODO: This will be called by auth service after successful login
//    */
//   setCurrentUser(user: User | null): void {
//     this.currentUser$.next(user);
//   }

//   /**
//    * Logout the user
//    * TODO: Call backend logout endpoint and clear session
//    */
//   logout(): void {
//     this.currentUser$.next(null);
//   }
// }
