package com.parasoft.demoapp.grpc.service;

import com.parasoft.demoapp.messages.AssetMessages;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;

@Slf4j
@GrpcGlobalServerInterceptor
public class GrpcServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(serverCallHandler.startCall(serverCall, metadata)) {
            @Override
            public void onMessage(ReqT message) {
                if (message == null) {
                    serverCall.close(Status.INVALID_ARGUMENT.withDescription(AssetMessages.INVALID_REQUEST), metadata);
                    return;
                }
                super.onMessage(message);
            }

            @Override
            public void onHalfClose() {
                if (serverCall.isReady()) {
                    super.onHalfClose();
                }
            }
        };
    }
}
