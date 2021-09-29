//
//  NEEduErrorType.h
//  Pods
//
//  Created by Groot on 2021/5/26.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#ifndef NEEduErrorType_h
#define NEEduErrorType_h

static NSString *NEEduErrorDomain = @"netease.edu.logic.error";

typedef NS_ENUM(NSInteger, NEEduErrorType) {
    // No error.
    EduErrorTypeNone = 0,
    // An operation is valid, but currently unsupported.
    NEEduErrorTypeUnsupportOperation = 1,
    NEEduErrorTypeNotModified = 304,
    NEEduErrorTypeInvalidParemeter = 400,
    NEEduErrorTypeUnauthorized = 401,
    NEEduErrorTypeForbidden = 403,
    NEEduErrorTypeNotFound = 404,
    NEEduErrorTypeMethodNotAllowed = 405,
    NEEduErrorTypeRoomAlreadyExists = 409,
    NEEduErrorTypeUnsurpportedType = 415,
    NEEduErrorTypeInternalServerError = 500,
    NEEduErrorTypeServiceBusy = 503,
    NEEduErrorTypeConfigError = 1001,
    NEEduErrorTypeRoleNumberOutOflimit = 1002,
    NEEduErrorTypeRoleUndefined = 1003,
    NEEduErrorTypeRoomNotFound = 1004,
    NEEduErrorTypeBadRoomConfig = 1005,
    NEEduErrorTypeRoomPropertyExists = 1006,
    NEEduErrorTypeMemberPropertyExists = 1007,
    NEEduErrorTypeSeatConflict = 1008,
    NEEduErrorTypeSeatIsFull = 1009,
    NEEduErrorTypeUserIsSeated = 1010,
    NEEduErrorTypeSeatNotExist = 1011,
    NEEduErrorTypeOutOfConcurrentLimit = 1012,
    NEEduErrorTypeInvalidSeatConfig = 1014,
    NEEduErrorTypeUserNotFound = 1015,
    NEEduErrorTypeUserIsAlreadyInRoom = 1016,
    NEEduErrorTypeRoomConfigConflict = 1017,
    NEEduErrorTypeCreateIMUserFailed = 700,
    NEEduErrorTypeIMUserNotExist = 701,
    NEEduErrorTypeBadImService = 702,
    NEEduErrorTypeNimUserExist = 703,

};

#endif /* NEEduErrorType_h */
