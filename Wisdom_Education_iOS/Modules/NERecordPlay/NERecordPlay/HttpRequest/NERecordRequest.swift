//
//  NERecordRequest.swift
//  Pods
//
//  Created by 郭园园 on 2021/8/6.
//

import Foundation
import Alamofire

//enum RequestError: Error {
//    case networkError(Int)
//    case logicError(Int)
//    case decodeError
//}

@objc public class NERecordRequest: NSObject {
//    public static let request = NERecordRequest()
    var appKey : String = "5fa5726d7fde462d8198bf35476c4047"
    var authorization : String = "OTI1MGVhOWVjNTdhNDk5ZjhjZjJhMWZjODZkYTg5Nzg6Mzk0YjU2NTBlMDUxNGRlNWJjNWE5NzA2ZDk0NTYyMDk="
    var userUuid : String?
    var token : String?
    var baseUrl = "https://yiyong-xedu-v2.netease.im"
    
    @objc public init(appKey: String, authorization: String, baseUrl: String, userUuid: String?, token: String?) {
        self.appKey = appKey
        self.authorization = authorization
        self.userUuid = userUuid
        self.token = token
        self.baseUrl = baseUrl
        print("appkey:\(appKey)\n authorization:\(authorization)\n userUuid:\(String(describing: userUuid))\ntoken:\(String(describing: token))\n baseUrl:\(baseUrl)")
    }
    
    @objc public func getRecordList(roomUuid:String, rtcCid:String, success: @escaping (Any?) -> Void,failure: @escaping (Error?) -> Void) {
        let url = "\(baseUrl)/scene/apps/\(appKey)/v1/rooms/\(roomUuid)/\(rtcCid)/record/playback"
        print("Request:\n \(url)")
        
        let deviceId = UIDevice.current.identifierForVendor?.uuidString
        
        let version: String = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String
        let build: String = Bundle.main.infoDictionary?["CFBundleVersion"] as! String
        let versionCode = version.replacingOccurrences(of: ".", with: "") + build
        var headers : HTTPHeaders = [
            "authorization": "Basic \(authorization)",
            "versionCode": versionCode,
            "clientType": "ios"
        ]
        if deviceId != nil {
            headers["deviceId"] = deviceId
        }
        if userUuid != nil {
            headers["user"] = userUuid
        }
        if token != nil {
            headers["token"] = token
        }
        print(headers);
        AF.request(url,headers: headers).responseData { response in
            switch response.result {
            case.success(let data):
                let decoder = JSONDecoder()
                do {
                    let dataStr = String.init(data: data, encoding: .utf8)
                    print("请求数据：\n \(dataStr)")
                    let model = try decoder.decode(Response.self, from: data)
                    if model.code == 0 {
                        success(model.data)
                    }else if model.code == 404 {
                        let error = NSError(domain: "record.request.error", code: model.code, userInfo: [NSLocalizedDescriptionKey:"查找录制回放失败，请稍后重试"])
                        failure(error)
                    }else if model.code == 500 {
                        let error = NSError(domain: "record.request.error", code: model.code, userInfo: [NSLocalizedDescriptionKey:"服务器内部异常"])
                        failure(error)
                    }
                } catch {
                    print("error:\(error)")
                    let error = NSError(domain: "record.request.error", code: -1, userInfo: [NSLocalizedDescriptionKey:"解析数据出错了"])
                    failure(error)
                }
            case.failure(let error):
                //network error
                print("[req] network error:\(error)")
                failure(error)
            }
        }
    }
}
