//
//  RNKitASDatePicker.h
//  RNKitASDatePicker
//
//  Created by SimMan on 2016/11/29.
//  Copyright © 2016年 RNKit.io. All rights reserved.
//

#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#else
#import "RCTBridgeModule.h"
#import "RCTEventEmitter.h"
#endif

@interface RNKitASDatePicker : RCTEventEmitter <RCTBridgeModule>
@end

