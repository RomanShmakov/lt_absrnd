package ru.alfabank.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class SettlementOperationServiceGrpc {

  private SettlementOperationServiceGrpc() {}

  public static final String SERVICE_NAME = "ru.alfabank.grpc.SettlementOperationService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<BankOrderExecRequest,
      BankOrderExecResponse> getExecBankOrderMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecBankOrder",
      requestType = BankOrderExecRequest.class,
      responseType = BankOrderExecResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<BankOrderExecRequest,
      BankOrderExecResponse> getExecBankOrderMethod() {
    io.grpc.MethodDescriptor<BankOrderExecRequest, BankOrderExecResponse> getExecBankOrderMethod;
    if ((getExecBankOrderMethod = SettlementOperationServiceGrpc.getExecBankOrderMethod) == null) {
      synchronized (SettlementOperationServiceGrpc.class) {
        if ((getExecBankOrderMethod = SettlementOperationServiceGrpc.getExecBankOrderMethod) == null) {
          SettlementOperationServiceGrpc.getExecBankOrderMethod = getExecBankOrderMethod =
              io.grpc.MethodDescriptor.<BankOrderExecRequest, BankOrderExecResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecBankOrder"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BankOrderExecRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  BankOrderExecResponse.getDefaultInstance()))
              .setSchemaDescriptor(new SettlementOperationServiceMethodDescriptorSupplier("ExecBankOrder"))
              .build();
        }
      }
    }
    return getExecBankOrderMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SettlementOperationServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceStub>() {
        @Override
        public SettlementOperationServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SettlementOperationServiceStub(channel, callOptions);
        }
      };
    return SettlementOperationServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static SettlementOperationServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceBlockingV2Stub>() {
        @Override
        public SettlementOperationServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SettlementOperationServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return SettlementOperationServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SettlementOperationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceBlockingStub>() {
        @Override
        public SettlementOperationServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SettlementOperationServiceBlockingStub(channel, callOptions);
        }
      };
    return SettlementOperationServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SettlementOperationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SettlementOperationServiceFutureStub>() {
        @Override
        public SettlementOperationServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SettlementOperationServiceFutureStub(channel, callOptions);
        }
      };
    return SettlementOperationServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void execBankOrder(BankOrderExecRequest request,
                               io.grpc.stub.StreamObserver<BankOrderExecResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecBankOrderMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service SettlementOperationService.
   */
  public static abstract class SettlementOperationServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return SettlementOperationServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service SettlementOperationService.
   */
  public static final class SettlementOperationServiceStub
      extends io.grpc.stub.AbstractAsyncStub<SettlementOperationServiceStub> {
    private SettlementOperationServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SettlementOperationServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SettlementOperationServiceStub(channel, callOptions);
    }

    /**
     */
    public void execBankOrder(BankOrderExecRequest request,
                              io.grpc.stub.StreamObserver<BankOrderExecResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecBankOrderMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service SettlementOperationService.
   */
  public static final class SettlementOperationServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<SettlementOperationServiceBlockingV2Stub> {
    private SettlementOperationServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SettlementOperationServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SettlementOperationServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public BankOrderExecResponse execBankOrder(BankOrderExecRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecBankOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service SettlementOperationService.
   */
  public static final class SettlementOperationServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<SettlementOperationServiceBlockingStub> {
    private SettlementOperationServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SettlementOperationServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SettlementOperationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public BankOrderExecResponse execBankOrder(BankOrderExecRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecBankOrderMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service SettlementOperationService.
   */
  public static final class SettlementOperationServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<SettlementOperationServiceFutureStub> {
    private SettlementOperationServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected SettlementOperationServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SettlementOperationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<BankOrderExecResponse> execBankOrder(
        BankOrderExecRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecBankOrderMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXEC_BANK_ORDER = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXEC_BANK_ORDER:
          serviceImpl.execBankOrder((BankOrderExecRequest) request,
              (io.grpc.stub.StreamObserver<BankOrderExecResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExecBankOrderMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              BankOrderExecRequest,
              BankOrderExecResponse>(
                service, METHODID_EXEC_BANK_ORDER)))
        .build();
  }

  private static abstract class SettlementOperationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SettlementOperationServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return BankOrderProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SettlementOperationService");
    }
  }

  private static final class SettlementOperationServiceFileDescriptorSupplier
      extends SettlementOperationServiceBaseDescriptorSupplier {
    SettlementOperationServiceFileDescriptorSupplier() {}
  }

  private static final class SettlementOperationServiceMethodDescriptorSupplier
      extends SettlementOperationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SettlementOperationServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SettlementOperationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SettlementOperationServiceFileDescriptorSupplier())
              .addMethod(getExecBankOrderMethod())
              .build();
        }
      }
    }
    return result;
  }
}
