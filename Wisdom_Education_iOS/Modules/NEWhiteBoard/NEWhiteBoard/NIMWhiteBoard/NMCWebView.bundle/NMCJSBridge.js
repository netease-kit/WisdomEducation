

function NativeFunction(msg){
    var bridgeObjc = JSON.parse(msg);
    var action = bridgeObjc.action;
    var param  = bridgeObjc.param;
    window.webkit.messageHandlers.NMCNativeMethodMessage.postMessage({
    action:action,
    param:param,
    });
}
