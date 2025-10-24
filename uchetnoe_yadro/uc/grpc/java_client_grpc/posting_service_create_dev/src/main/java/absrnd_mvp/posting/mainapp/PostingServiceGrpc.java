package absrnd_mvp.posting.mainapp;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: absrnd_mvp.posting.mainapp.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class PostingServiceGrpc {

  private PostingServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "absrnd_mvp.posting.mainapp.PostingService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest,
      absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> getCreateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Create",
      requestType = absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest.class,
      responseType = absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest,
      absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> getCreateMethod() {
    io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest, absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> getCreateMethod;
    if ((getCreateMethod = PostingServiceGrpc.getCreateMethod) == null) {
      synchronized (PostingServiceGrpc.class) {
        if ((getCreateMethod = PostingServiceGrpc.getCreateMethod) == null) {
          PostingServiceGrpc.getCreateMethod = getCreateMethod =
              io.grpc.MethodDescriptor.<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest, absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Create"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PostingServiceMethodDescriptorSupplier("Create"))
              .build();
        }
      }
    }
    return getCreateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest,
      absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> getDeleteMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Delete",
      requestType = absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest.class,
      responseType = absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest,
      absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> getDeleteMethod() {
    io.grpc.MethodDescriptor<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest, absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> getDeleteMethod;
    if ((getDeleteMethod = PostingServiceGrpc.getDeleteMethod) == null) {
      synchronized (PostingServiceGrpc.class) {
        if ((getDeleteMethod = PostingServiceGrpc.getDeleteMethod) == null) {
          PostingServiceGrpc.getDeleteMethod = getDeleteMethod =
              io.grpc.MethodDescriptor.<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest, absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Delete"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse.getDefaultInstance()))
              .setSchemaDescriptor(new PostingServiceMethodDescriptorSupplier("Delete"))
              .build();
        }
      }
    }
    return getDeleteMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static PostingServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PostingServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PostingServiceStub>() {
        @java.lang.Override
        public PostingServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PostingServiceStub(channel, callOptions);
        }
      };
    return PostingServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static PostingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PostingServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PostingServiceBlockingStub>() {
        @java.lang.Override
        public PostingServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PostingServiceBlockingStub(channel, callOptions);
        }
      };
    return PostingServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static PostingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<PostingServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<PostingServiceFutureStub>() {
        @java.lang.Override
        public PostingServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new PostingServiceFutureStub(channel, callOptions);
        }
      };
    return PostingServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void create(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest request,
        io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateMethod(), responseObserver);
    }

    /**
     */
    default void delete(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest request,
        io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service PostingService.
   */
  public static abstract class PostingServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return PostingServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service PostingService.
   */
  public static final class PostingServiceStub
      extends io.grpc.stub.AbstractAsyncStub<PostingServiceStub> {
    private PostingServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PostingServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PostingServiceStub(channel, callOptions);
    }

    /**
     */
    public void create(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest request,
        io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void delete(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest request,
        io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service PostingService.
   */
  public static final class PostingServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<PostingServiceBlockingStub> {
    private PostingServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PostingServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PostingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse create(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateMethod(), getCallOptions(), request);
    }

    /**
     */
    public absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse delete(absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service PostingService.
   */
  public static final class PostingServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<PostingServiceFutureStub> {
    private PostingServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected PostingServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new PostingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse> create(
        absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse> delete(
        absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE = 0;
  private static final int METHODID_DELETE = 1;

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

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE:
          serviceImpl.create((absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest) request,
              (io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse>) responseObserver);
          break;
        case METHODID_DELETE:
          serviceImpl.delete((absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest) request,
              (io.grpc.stub.StreamObserver<absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
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
          getCreateMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateRequest,
              absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.CreateResponse>(
                service, METHODID_CREATE)))
        .addMethod(
          getDeleteMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteRequest,
              absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.DeleteResponse>(
                service, METHODID_DELETE)))
        .build();
  }

  private static abstract class PostingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    PostingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return absrnd_mvp.posting.mainapp.AbsrndMvpPostingMainapp.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("PostingService");
    }
  }

  private static final class PostingServiceFileDescriptorSupplier
      extends PostingServiceBaseDescriptorSupplier {
    PostingServiceFileDescriptorSupplier() {}
  }

  private static final class PostingServiceMethodDescriptorSupplier
      extends PostingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    PostingServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (PostingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new PostingServiceFileDescriptorSupplier())
              .addMethod(getCreateMethod())
              .addMethod(getDeleteMethod())
              .build();
        }
      }
    }
    return result;
  }
}
