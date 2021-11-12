//
//  HttpClient.m

//
//  Created by Netease on 2020/5/3.
//  Copyright © 2021 Netease. All rights reserved.
//

#import "HttpClient.h"
#import <AFNetworking/AFNetworking.h>
#
typedef NS_ENUM(NSInteger, NEHttpType) {
    NEHttpTypeGet            = 0,
    NEHttpTypePost,
    NEHttpTypePut,
    NEHttpTypeDelete,
};
#define NEHttpTypeStrings  (@[@"GET",@"POST",@"PUT",@"DELETE"])


@interface HttpClient ()

@property (nonatomic,strong) AFHTTPSessionManager *sessionManager;

@end

static HttpClient *manager = nil;

@implementation HttpClient
+ (instancetype)shareManager{
    @synchronized(self){
        if (!manager) {
            manager = [[self alloc]init];
            [manager initSessionManager];
        }
        return manager;
    }
}

- (void)initSessionManager {
    self.sessionManager = [AFHTTPSessionManager manager];
    self.sessionManager.requestSerializer = [AFJSONRequestSerializer serializer];
    self.sessionManager.responseSerializer = [AFJSONResponseSerializer serializer];
    self.sessionManager.requestSerializer.timeoutInterval = 30;
}

+ (void)get:(NSString *)url params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {
    
    if(headers != nil && headers.allKeys.count > 0){
        NSArray<NSString*> *keys = headers.allKeys;
        for(NSString *key in keys){
            [HttpClient.shareManager.sessionManager.requestSerializer setValue:headers[key] forHTTPHeaderField:key];
        }
    }
    
    NSString *encodeUrl = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    
    [HttpClient httpStartLogWithType:NEHttpTypeGet url:encodeUrl headers:headers params:params];
    
    [HttpClient.shareManager.sessionManager GET:encodeUrl parameters:params headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        [HttpClient httpSuccessLogWithType:NEHttpTypeGet url:encodeUrl responseObject:responseObject];
        
        if (success) {
            success(responseObject);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [HttpClient checkHttpError:error task:task success:^(id responseObj) {
            
            [HttpClient httpSuccessLogWithType:NEHttpTypeGet url:encodeUrl responseObject:responseObj];
            if (success) {
                success(responseObj);
            }
            
        } failure:^(NSError *error, NSInteger statusCode) {
            [HttpClient httpErrorLogWithType:NEHttpTypeGet url:encodeUrl error:error];
            
            if (failure) {
                failure(error, statusCode);
            }
        }];
    }];
}

+ (void)post:(NSString *)url params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {

    if(headers != nil && headers.allKeys.count > 0){
        NSArray<NSString*> *keys = headers.allKeys;
        for (NSString *key in keys) {
            [HttpClient.shareManager.sessionManager.requestSerializer setValue:headers[key] forHTTPHeaderField:key];
        }
    }
    
    NSString *encodeUrl = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    [HttpClient httpStartLogWithType:NEHttpTypePost url:encodeUrl headers:headers params:params];
    
    [HttpClient.shareManager.sessionManager POST:encodeUrl parameters:params headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        [HttpClient httpSuccessLogWithType:NEHttpTypePost url:encodeUrl responseObject:responseObject];
        if (success) {
            success(responseObject);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [HttpClient checkHttpError:error task:task success:^(id responseObj) {
            
            [HttpClient httpSuccessLogWithType:NEHttpTypePost url:encodeUrl responseObject:responseObj];
            if (success) {
                success(responseObj);
            }
            
        } failure:^(NSError *error, NSInteger statusCode) {
            [HttpClient httpErrorLogWithType:NEHttpTypePost url:encodeUrl error:error];
            
            if (failure) {
                failure(error, statusCode);
            }
        }];
    }];
}

+ (void)put:(NSString *)url params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {

    if(headers != nil && headers.allKeys.count > 0){
        NSArray<NSString*> *keys = headers.allKeys;
        for(NSString *key in keys){
            [HttpClient.shareManager.sessionManager.requestSerializer setValue:headers[key] forHTTPHeaderField:key];
        }
    }
    
    NSString *encodeUrl = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    
    [HttpClient httpStartLogWithType:NEHttpTypePut url:encodeUrl headers:headers params:params];
    
    [HttpClient.shareManager.sessionManager PUT:encodeUrl parameters:params headers:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        [HttpClient httpSuccessLogWithType:NEHttpTypePut url:encodeUrl responseObject:responseObject];

        if (success) {
            success(responseObject);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [HttpClient checkHttpError:error task:task success:^(id responseObj) {
            
            [HttpClient httpSuccessLogWithType:NEHttpTypePut url:encodeUrl responseObject:responseObj];
            if (success) {
                success(responseObj);
            }
            
        } failure:^(NSError *error, NSInteger statusCode) {
            [HttpClient httpErrorLogWithType:NEHttpTypePut url:encodeUrl error:error];
            
            if (failure) {
                failure(error, statusCode);
            }
        }];
    }];
}

+ (void)del:(NSString *)url params:(NSDictionary *)params headers:(NSDictionary<NSString*, NSString*> *)headers success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {

    if(headers != nil && headers.allKeys.count > 0){
        NSArray<NSString*> *keys = headers.allKeys;
        for(NSString *key in keys){
            [HttpClient.shareManager.sessionManager.requestSerializer setValue:headers[key] forHTTPHeaderField:key];
        }
    }
    
    NSString *encodeUrl = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    
    [HttpClient httpStartLogWithType:NEHttpTypeDelete url:encodeUrl headers:headers params:params];
    
    [HttpClient.shareManager.sessionManager DELETE:encodeUrl parameters:params headers:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        
        [HttpClient httpSuccessLogWithType:NEHttpTypeDelete url:encodeUrl responseObject:responseObject];

        if (success) {
            success(responseObject);
        }
        
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        
        [HttpClient checkHttpError:error task:task success:^(id responseObj) {
            
            [HttpClient httpSuccessLogWithType:NEHttpTypeDelete url:encodeUrl responseObject:responseObj];
            if (success) {
                success(responseObj);
            }
            
        } failure:^(NSError *error, NSInteger statusCode) {
            [HttpClient httpErrorLogWithType:NEHttpTypeDelete url:encodeUrl error:error];
            
            if (failure) {
                failure(error, statusCode);
            }
        }];
    }];
}

#pragma mark LOG
+ (void)httpStartLogWithType:(NEHttpType)type url:(NSString *)url
                     headers:(NSDictionary *)headers params:(NSDictionary *)params {
    
    NSString *msg = [NSString stringWithFormat:
                     @"\n============>%@ HTTP Start<============\n\
                     \nurl==>\n%@\n\
                     \nheaders==>\n%@\n\
                     \nparams==>\n%@\n\
                     ",NEHttpTypeStrings[type], url, headers, params];
    NCKLogInfo(@"%@",msg);
}
+ (void)httpSuccessLogWithType:(NEHttpType)type url:(NSString *)url
                     responseObject:(id)responseObject {
    
    NSString *msg = [NSString stringWithFormat:
                     @"\n============>%@ HTTP Success<============\n\
                     \nurl==>\n%@\n\
                     \nResult==>\n%@\n\
                     ",NEHttpTypeStrings[type], url, responseObject];
    
    NCKLogInfo(@"%@",msg);
}

+ (void)httpErrorLogWithType:(NEHttpType)type url:(NSString *)url
                     error:(NSError *)error {
    
    NSString *msg = [NSString stringWithFormat:
                     @"\n============>%@ HTTP Error<============\n\
                     \nurl==>\n%@\n\
                     \nError==>\n%@\n\
                     ",NEHttpTypeStrings[type], url, error.description];
    NCKLogError(@"%@",msg);
}

#pragma mark Check
+ (void)checkHttpError:(NSError *)error task:(NSURLSessionDataTask *)task success:(void (^)(id responseObj))success failure:(void (^)(NSError *error, NSInteger statusCode))failure {
    
    NSHTTPURLResponse *urlResponse = (NSHTTPURLResponse *)task.response;
    
    NSData *errorData = error.userInfo[@"com.alamofire.serialization.response.error.data"];
    if (errorData == nil) {
        failure(error, urlResponse.statusCode);
        return;
    }
    
    NSDictionary *errorDataDict = [NSJSONSerialization JSONObjectWithData:errorData options:NSJSONReadingMutableLeaves error:nil];
    if (errorDataDict == nil) {
        failure(error, urlResponse.statusCode);
        return;
    }

    success(errorDataDict);
}
@end
