//
//  NetworkingController.h
//  ProcurableApp
//
//  Created by Will Turner on 2/9/16.
//  Copyright © 2016 Wilson Turner. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Item.h"
#import "InventoryItem.h"

NS_ASSUME_NONNULL_BEGIN
typedef void(^DataControllerCompletionHandler)(NSArray * __nullable values, NSError * __nullable error);
typedef void(^DepartmentControllerCompletionHandler)(NSArray * __nullable names, NSArray * __nullable numbers, NSError * __nullable error);
typedef void(^NetworkingControllerCompletionHandler)(BOOL value, NSError * __nullable error);
typedef void(^RequestsCompletionHandler)(NSArray *requests, NSError * __nullable error);
typedef void(^ItemsCompletionHandler)(NSArray *items, NSError * __nullable error);
typedef void(^SearchItemsCompletionHandler)(BOOL value, NSArray *items, NSError * __nullable error);
typedef void(^ItemDetailCompletionHandler)(InventoryItem * __nullable item, NSError * __nullable error);


@interface NetworkingController : NSObject
- (void)registerNewUser:(NSString *)user withPassword:(NSString *)password withConfirmPassword:(NSString *)confirmPassword withDepartmentNumber:(NSNumber *)departmentNumber withcompletion:(NetworkingControllerCompletionHandler)completionHandler;
- (void)loginUser:(NSString *)user withPassword:(NSString *)password completion:(NetworkingControllerCompletionHandler)completionHandler;
- (void)cookieTestWithCompletion:(NetworkingControllerCompletionHandler)completionHandler;
- (void)listAllRequests:(RequestsCompletionHandler)completionHandler;
- (void)listAllRequestsToBeApproved:(RequestsCompletionHandler)completionHandler;
- (void)temporaryCreateItemWithCompletion:(NetworkingControllerCompletionHandler)completionHandler;
- (void)updateRequestStatus:(NSNumber *)idNumber withValue:(NSNumber *)value withCompletion:(NetworkingControllerCompletionHandler)completionHandler;
- (void)listAllInventoryItems:(ItemsCompletionHandler)completionHandler;
- (void)searchForItems:(NSString *)string withCompletion:(SearchItemsCompletionHandler)completionHandler;
- (void)inventoryItemDetail:(NSNumber *)idNumber withCompletion:(ItemDetailCompletionHandler)completionHandler;
- (void)fetchDepartmentsForRegister:(DepartmentControllerCompletionHandler)completionHandler;
@end
NS_ASSUME_NONNULL_END
