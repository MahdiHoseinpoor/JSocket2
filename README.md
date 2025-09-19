![JSocket2 Header Image](/images/header1.png)

# JSocket2: A Robust & Extensible Java Client-Server Framework

**JSocket2** is a sophisticated and high-performance Java framework engineered for crafting resilient client-server applications. Born from a collegiate Telegram-like project, it has evolved into a comprehensive solution that masterfully demonstrates cutting-edge **Object-Oriented Programming (OOP)** principles and a diverse array of **Design Patterns**.

This framework is not just a communication layer; it's a testament to architectural elegance, emphasizing modularity, extensibility, and testability. It offers a rich set of features, from secure, encrypted channels to dynamic Remote Procedure Calls (RPC) and an event-driven messaging system, all underpinned by a custom, lightweight Dependency Injection (DI) container. JSocket2 stands as a prime example of building complex systems with clarity and maintainability at their core.

### 🌟 Showcase: Telegram Clone Desktop
JSocket2 is battle-tested. It serves as the complete networking and architectural backbone for a feature-rich Telegram Clone Desktop application. This project demonstrates how JSocket2 can be used to build a complex, real-world application with professional features like real-time messaging, file transfers, and secure authentication.

See JSocket2 in action in the [Telegram Clone Desktop repository](https://github.com/11-devs/Telegram-clone).

## Table of Contents

1.  [Features](#features)
2.  [Architectural Overview & Design Patterns](#architectural-overview--design-patterns)
    *   [Core Communication Layer](#core-communication-layer)
    *   [Dependency Injection (DI) & Inversion of Control (IoC)](#dependency-injection-di--inversion-of-control-ioc)
    *   [Remote Procedure Call (RPC) Framework](#remote-procedure-call-rpc-framework)
    *   [Event Hub (Publish-Subscribe)](#event-hub-publish-subscribe)
    *   [Secure Communication (Cryptography)](#secure-communication-cryptography)
    *   [Robust File Transfer Mechanism](#robust-file-transfer-mechanism)
    *   [Client-Side Resilience (Reconnection)](#client-side-resilience-reconnection)
    *   [Session Management](#session-management)
    *   [Authentication Service](#authentication-service)
3.  [Getting Started](#getting-started)
    *   [Server Setup Example](#server-setup-example)
    *   [Client Setup Example](#client-setup-example)
    *   [RPC Usage Example](#rpc-usage-example)
    *   [Event Usage Example](#event-usage-example)
4.  [Project Structure](#project-structure)
5.  [Technologies Used](#technologies-used)
6.  [License](#license)

---

## Features

JSocket2 provides a comprehensive set of features for building reliable networked applications:

*   **Bidirectional Communication:** Seamless client-to-server and server-to-client messaging.
*   **Plug-and-Play Architecture:** Easily extendable components for custom business logic.
*   **Built-in Security:** RSA for key exchange, AES for symmetric encryption, ensuring data confidentiality.
*   **Dependency Injection:** A custom, lightweight DI container for managing service lifetimes and dependencies, promoting loose coupling.
*   **Remote Procedure Calls (RPC):** A clear, annotation-driven mechanism for executing server-side methods from the client and vice-versa (via responses).
*   **Event-Driven Communication:** A publish-subscribe event hub for broadcasting messages and decoupling components.
*   **Resumable File Transfers:** Efficiently upload and download files, with support for pausing and resuming transfers.
*   **Automatic Client Reconnection:** Configurable strategies for handling disconnections and attempting to reconnect.
*   **Session Management:** Dedicated session objects for maintaining state per client on both client and server sides.
*   **Authentication System:** Pluggable authentication service for user validation.

---

## Architectural Overview & Design Patterns

JSocket2 is meticulously crafted using a variety of design patterns and adheres to core OOP principles to ensure maintainability, extensibility, and robustness.

### Core Communication Layer

At its heart, the framework defines a clear communication protocol.

*   **`Message`, `MessageHeader`, `MessageType`**: These classes encapsulate the fundamental unit of communication. `Message` acts as a **Composite** object, holding `MessageHeader` (a **Value Object** carrying essential metadata like UUID, type, and lengths) and the actual data (metadata and payload). `MessageType` is a type-safe **Enum** for categorizing messages.
*   **`MessageHandler`**: This class acts as a **Facade** over the raw `InputStream` and `OutputStream` of a `Socket`. It centralizes the logic for reading and writing `Message` objects, handling framing (using `MAGIC_BYTES`), encryption/decryption, and stream synchronization. This significantly simplifies the interaction with the underlying network I/O.
*   **`IMessageProcessor`**: An **Interface Segregation Principle (ISP)** driven interface that defines a contract for processing incoming messages.
    *   **`ClientMessageProcessor`**: Implements `IMessageProcessor` on the client side, acting as a **Strategy** for handling messages like RSA public key reception, download chunks, and events.
    *   **`ServerMessageProcessor`**: Implements `IMessageProcessor` on the server side, acting as a **Strategy** for handling RPC calls, upload requests, AES key reception, and authentication.

### Dependency Injection (DI) & Inversion of Control (IoC)

One of the project's cornerstones is its custom, lightweight Dependency Injection (DI) framework, demonstrating a strong understanding of the **Inversion of Control (IoC)** principle.

*   **`ServiceCollection`**: This acts as the **Registry** where services (interfaces and their implementations) are configured with their desired lifetimes (**Singleton**, **Scoped**, **Transient**). This is where the application's dependencies are declared, promoting **Loose Coupling**.
*   **`ServiceDescriptor`**: A **Value Object** that holds the configuration for a single service (type, implementation, lifetime, and optionally an instance).
*   **`ServiceProvider`**: This is the **IoC Container** that resolves and provides instances of registered services. It acts as a **Factory** for creating objects and their dependencies automatically.
    *   It manages **Singleton** instances (created once and reused), **Scoped** instances (one per logical scope, here tied to a `ThreadLocal`), and **Transient** instances (a new one for every request).
    *   It includes robust error handling for `CircularDependencyException` and `ServiceCreationException`, showcasing attention to framework stability.
*   **`@Inject` Annotation**: Used to explicitly mark the constructor that the `ServiceProvider` should use for dependency resolution, acting as a form of **Constructor Injection**.

### Remote Procedure Call (RPC) Framework

JSocket2 includes a sophisticated RPC mechanism for remote method invocation, adhering to the **Command Pattern**.

*   **`RpcController` & `RpcAction` Annotations**: These annotations are used to mark classes and methods as remotely callable. They serve as metadata for the framework to discover and map RPC endpoints, demonstrating the use of **Metadata-driven Development**.
*   **`RpcControllerBase`**: An **Abstract Class** that provides common utility methods for creating standardized `RpcResponse` objects (e.g., `Ok()`, `BadRequest()`, `NotFound()`). This is an application of the **Template Method** pattern, giving concrete controllers a consistent way to formulate responses and injecting common dependencies like `CurrentUser` and `ServerSessionManager`.
*   **`RpcCallerBase`**: On the client side, this acts as a **Facade** or **Proxy**, simplifying the client's interaction with the RPC system by abstracting away the message construction and response handling.
*   **`RpcControllerCollection`**: A **Registry** that discovers and stores all registered RPC controllers, acting as a **Factory** for the `RpcDispatcher`.
*   **`RpcDispatcher`**: This is the central **Invoker** of the RPC system on the server. It receives RPC call messages, uses the `ServiceProvider` to instantiate the correct controller, and then uses reflection to dynamically invoke the target action method with correctly deserialized parameters. It also handles setting up the `RpcControllerBase` context with the `CurrentUser` and `ServerSessionManager`.
*   **`RpcCallMetadata`, `RpcResponse`, `RpcResponseMetadata`**: These are **Data Transfer Objects (DTOs)** used to carry structured information about RPC requests and responses.

### Event Hub (Publish-Subscribe)

The framework incorporates an event hub for asynchronous, decoupled communication between components.

*   **`EventBroker`**: This is the central **Publisher** and **Mediator**. It takes an `EventMetadata` (a **Value Object** with the event name) and a payload, then dispatches it to all registered subscribers.
*   **`EventSubscriberCollection`**: This acts as the **Registry** for event subscribers. It scans classes for methods annotated with `@OnEvent` and registers them.
*   **`@OnEvent` Annotation**: Marks methods as event handlers, specifying the event name they subscribe to. This enables **Annotation-Driven Configuration**.
*   **`EventBase`, `EventSubscriberBase`**: Provide abstract bases for event definitions and subscribers, leveraging **Inheritance** and offering a **Template Method** for event publication logic on the server side.
*   **Pattern**: The entire event system is a direct implementation of the **Observer Pattern** (often referred to as Publish-Subscribe). It effectively **Decouples** event producers from event consumers.

### Secure Communication (Cryptography)

Security is paramount, and JSocket2 implements a robust handshake and encryption mechanism.

*   **`EncryptionUtil`**: A **Utility Class** (effectively a collection of static methods) that provides cryptographic primitives for RSA key generation, encryption, decryption, and AES key generation.
*   **`RsaKeyManager`**: Managed as a **Singleton** by the DI container, this class encapsulates the server's RSA key pair. This ensures a single, consistent key pair is used for the entire server lifecycle.
*   **Handshake Protocol**: The initial client-server communication involves an RSA public key exchange, followed by the client encrypting a newly generated AES key with the server's RSA public key and sending it. This establishes a shared secret AES key for symmetric encryption of all subsequent communication, implementing a secure **Key Exchange** protocol.
*   **`MessageUtil`**: A **Facade** for `EncryptionUtil`, providing high-level methods to encrypt and decrypt `Message` objects, handling the generation and inclusion of Initialization Vectors (IVs) for AES.

### Robust File Transfer Mechanism

The framework supports resumable file transfers, crucial for large files or unreliable networks.

*   **`FileTransferManager` (Abstract)**: This **Abstract Class** defines the **Template Method** for common file transfer operations (sending chunks, processing received chunks, registering listeners, managing transfer state). It handles the complexities of chunking, buffering, and saving/loading transfer progress.
*   **`ClientFileTransferManager` & `ServerFileTransferManager`**: Concrete implementations of `FileTransferManager` for client-side and server-side logic, respectively. This is a form of the **Strategy Pattern**, allowing different behaviors based on the role.
*   **`TransferFiles`**: This class manages the persistence of file transfer state and data. It uses `RandomAccessFile` for efficient chunk writing and a `.info` file (JSON) to store `TransferInfo`. It also supports loading/saving transfer state, implementing the **Memento Pattern** for resuming transfers.
*   **`TransferInfo`**: A **Data Transfer Object (DTO)** that encapsulates all necessary metadata for a file transfer, enabling it to be paused and resumed.
*   **`IProgressListener`**: An **Observer Pattern** interface allowing external components to monitor transfer progress.
*   **`SendChunkMetadata`, `UploadRequestMetadata`, `DownloadRequestMetadata`, `UploadResumeRequestMetadata`, etc.**: Various **DTOs** for orchestrating file transfer requests and metadata.

### Client-Side Resilience (Reconnection)

*   **`ReconnectionOptions`**: A **Strategy** object that encapsulates parameters for the client's automatic reconnection logic (min/max retry delays, jitter coefficient). This allows for flexible and configurable backoff strategies.
*   **`IConnectionEventListener`**: An **Observer Pattern** interface used by `ClientApplication` to notify interested parties about connection loss, enabling graceful handling of network interruptions.
*   **`ClientApplication`**: The central orchestrator on the client side, implementing an **Automatic Reconnection Loop** that leverages `ReconnectionOptions`.

### Session Management

*   **`Session` (Abstract)**: An **Abstract Base Class** encapsulating the shared AES secret key for encrypted communication within a session.
*   **`ClientSession`**: Extends `Session` to hold client-specific state, such as the server's public key and authentication status.
*   **`ServerSession`**: Extends `Session` to hold server-specific state for a connected client, including its associated `ClientHandler`, user identity, and authorization status. It is managed by the `ServerSessionManager`.
*   **`ServerSessionManager`**: This is a **Singleton** (managed by DI) responsible for creating, tracking, and removing `ServerSession` instances. It also implements an **Index** to map user IDs to active sessions, facilitating efficient message broadcasting (**Publisher-Subscriber** aspect for user-specific events).

### Authentication Service

*   **`IAuthService`**: An **Interface** defining the contract for an authentication service (`Login`, `IsKeyValid`). This is a prime example of the **Strategy Pattern**, allowing different authentication mechanisms to be plugged into the server without modifying core logic.
*   **`AuthModel`**: A **Data Transfer Object (DTO)** for carrying client authentication credentials.
*   **`UserIdentity`**: A **Value Object** representing the authenticated user's details.
*   **`IClientLifecycleListener`**: An **Observer Pattern** interface that allows the application to react to client authentication and disconnection events.

---

## Getting Started

To demonstrate the framework's usage, here are basic examples of setting up a server, a client, and utilizing RPC and Event Hub features.

*(Note: The `javaFX` aspect mentioned in the prompt is for the UI layer and not directly present in the core framework code provided. The examples will focus on the core server-client communication.)*

### Server Setup Example

```java
// ServerMain.java
import JSocket2.Core.Server.IClientLifecycleListener;
import JSocket2.Core.Server.ServerApplication;
import JSocket2.Core.Server.ServerApplicationBuilder;
import JSocket2.Core.Server.ServerSession;
import JSocket2.DI.Inject;
import JSocket2.Protocol.Authentication.AuthException;
import JSocket2.Protocol.Authentication.IAuthService;
import JSocket2.Protocol.Authentication.UserIdentity;
import JSocket2.Protocol.EventHub.EventBase;
import JSocket2.Protocol.EventHub.OnEvent;
import JSocket2.Protocol.Rpc.RpcAction;
import JSocket2.Protocol.Rpc.RpcController;
import JSocket2.Protocol.Rpc.RpcControllerBase;
import JSocket2.Protocol.Rpc.RpcResponse;
import JSocket2.Protocol.StatusCode;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        // Build the server application
        ServerApplication server = new ServerApplicationBuilder()
                .setPort(8080)
                .setAuthService(MyAuthService.class)
                .setClientLifecycleListener(MyClientLifecycleListener.class)
                .addController(MyRpcController.class)
                // You would add more event types here if they were server-side publishable events
                // .getServices().AddSingleton(MyServerEvent.class); // If MyServerEvent extends EventBase
                .build();

        // Run the server in a new thread to keep the main thread free
        new Thread(server::Run).start();
        System.out.println("Server started on port 8080. Press Enter to close.");
        System.in.read(); // Keep server running until Enter is pressed
        server.Close();
        System.out.println("Server shut down.");
    }

    // --- Authentication Service Implementation ---
    public static class MyAuthService implements IAuthService {
        @Inject
        public MyAuthService() {} // Injectable

        @Override
        public UserIdentity Login(String key) {
            if ("valid_key".equals(key)) {
                return new UserIdentity("user123", "John", "Doe");
            }
            throw new AuthException("Invalid access key provided.");
        }

        @Override
        public boolean IsKeyValid(String key) {
            return "valid_key".equals(key);
        }
    }

    // --- Client Lifecycle Listener Implementation ---
    public static class MyClientLifecycleListener implements IClientLifecycleListener {
        @Inject
        public MyClientLifecycleListener() {} // Injectable

        @Override
        public void onClientAuthenticated(ServerSession session) {
            System.out.println("Client authenticated: " + session.getActiveUser().getUserId());
            // Potentially publish a server-side event here
            // var onlineEvent = getServiceProvider().GetService(UserOnlineEvent.class);
            // onlineEvent.Invoke(getServerSessionManager(), session.getActiveUser().getUserId(), new UserOnlineModel(session.getActiveUser().getUserId()));
        }

        @Override
        public void onClientDisconnected(ServerSession session) {
            if (session.getActiveUser() != null) {
                System.out.println("Client disconnected: " + session.getActiveUser().getUserId());
            } else {
                System.out.println("Unauthenticated client disconnected.");
            }
        }
    }

    // --- RPC Controller Implementation ---
    @RpcController(Name = "myrpc")
    public static class MyRpcController extends RpcControllerBase {
        @Inject
        public MyRpcController() {} // Injectable

        @RpcAction(Name = "greet")
        public RpcResponse<String> greet(String name) {
            if (getCurrentUser() != null) {
                System.out.println("RPC Call from authenticated user " + getCurrentUser().getUserId() + ": greet " + name);
                return Ok("Hello, " + name + " from JSocket2 Server! You are " + getCurrentUser().getFirstName());
            } else {
                System.out.println("RPC Call from unauthenticated user: greet " + name);
                return Unauthorized("You must be authenticated to use this service.");
            }
        }

        @RpcAction(Name = "sum")
        public RpcResponse<Integer> sum(int a, int b) {
            if (getCurrentUser() == null) {
                return Unauthorized("Authentication required.");
            }
            return Ok(a + b);
        }
    }
}
```

### Client Setup Example

```java
// ClientMain.java
import JSocket2.Core.Client.ClientApplication;
import JSocket2.Core.Client.ClientApplicationBuilder;
import JSocket2.DI.Inject;
import JSocket2.Protocol.Authentication.AuthModel;
import JSocket2.Protocol.EventHub.OnEvent;
import JSocket2.Protocol.Rpc.RpcCallerBase;
import JSocket2.Protocol.Rpc.RpcResponse;
import JSocket2.Protocol.StatusCode;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClientMain {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Build the client application
        ClientApplication client = new ClientApplicationBuilder()
                .setEndpoint("localhost", 8080)
                .addEventSubscriber(MyClientEventSubscriber.class)
                .withReconnectionOptions(options -> {
                    options.setMinRetryDelay(1000);
                    options.setMaxRetryDelay(5000);
                })
                .Build();

        // Add a connection status listener
        client.addConnectionStatusListener(isConnected -> {
            System.out.println("Client connection status: " + (isConnected ? "CONNECTED" : "DISCONNECTED"));
        });

        // Add a connected listener to perform actions after connection is established
        client.addConnectedListener(app -> {
            System.out.println("Client successfully connected and handshake complete!");
            // Authenticate after connection
            try {
                System.out.println("Attempting to authenticate...");
                AuthModel auth = new AuthModel(new String[]{"valid_key"}, 1);
                StatusCode authStatus = app.sendAuthModel(auth);
                System.out.println("Authentication status: " + authStatus);

                if (authStatus == StatusCode.OK) {
                    // Make an RPC call after authentication
                    MyRpcClient rpcClient = new MyRpcClient(app);
                    RpcResponse<String> greetResponse = rpcClient.greet("World");
                    System.out.println("RPC Greet Response: " + greetResponse.getStatusCode() + " - " + greetResponse.getPayload());

                    RpcResponse<Integer> sumResponse = rpcClient.sum(10, 20);
                    System.out.println("RPC Sum Response: " + sumResponse.getStatusCode() + " - " + sumResponse.getPayload());

                    // Simulate a server-side event if the server sends one
                    // For this example, we'd need a server-side component to push this event
                    // The client is ready to receive it.
                }

            } catch (IOException | InterruptedException e) {
                System.err.println("Authentication or RPC failed: " + e.getMessage());
            }
        });

        // Start the client asynchronously
        client.startAsync();

        // Keep client running for a while
        TimeUnit.SECONDS.sleep(30);

        // Shutdown the client
        client.shutdown();
        System.out.println("Client shut down.");
    }

    // --- Client-side RPC Caller ---
    public static class MyRpcClient extends RpcCallerBase {
        @Inject
        public MyRpcClient(ClientApplication clientApplication) {
            super(clientApplication);
        }

        public RpcResponse<String> greet(String name) throws IOException {
            return callRpcAndGetResponse("myrpc", "greet", String.class, name);
        }

        public RpcResponse<Integer> sum(int a, int b) throws IOException {
            return callRpcAndGetResponse("myrpc", "sum", Integer.class, a, b);
        }
    }

    // --- Client-side Event Subscriber ---
    public static class MyClientEventSubscriber {
        @Inject
        public MyClientEventSubscriber() {} // Injectable

        @OnEvent("serverMessage") // Assuming the server publishes an event named "serverMessage"
        public void onServerMessage(String message) {
            System.out.println("Received server event: " + message);
        }

        @OnEvent("userOnline")
        public void onUserOnline(String userId, String firstName) {
            System.out.println("User " + firstName + " (" + userId + ") is online.");
        }
    }
}
```

### RPC Usage Example

As shown in `ClientMain.java` and `ServerMain.java`:

**Server-side (`MyRpcController.java`):**
```java
@RpcController(Name = "myrpc")
public static class MyRpcController extends RpcControllerBase {
    @Inject
    public MyRpcController() {}

    @RpcAction(Name = "greet")
    public RpcResponse<String> greet(String name) {
        // Access authenticated user
        UserIdentity currentUser = getCurrentUser();
        // ... business logic ...
        return Ok("Hello, " + name);
    }
}
```

**Client-side (`MyRpcClient.java`):**
```java
public static class MyRpcClient extends RpcCallerBase {
    @Inject
    public MyRpcClient(ClientApplication clientApplication) {
        super(clientApplication);
    }

    public RpcResponse<String> greet(String name) throws IOException {
        // Calls the "greet" action on the "myrpc" controller
        return callRpcAndGetResponse("myrpc", "greet", String.class, name);
    }
}
```

### Event Usage Example

**Client-side (`MyClientEventSubscriber.java`):**
```java
public static class MyClientEventSubscriber {
    @Inject
    public MyClientEventSubscriber() {}

    @OnEvent("serverMessage") // Subscribes to an event named "serverMessage"
    public void onServerMessage(String message) {
        System.out.println("Received server event: " + message);
    }
}
```

**Server-side (hypothetical event publishing from a service or controller):**
*(Note: A concrete `EventBase` implementation would be needed to actually send from the server, which wasn't fully detailed in the provided code for server-to-client events. Here's a conceptual example assuming a `ServerEventPublisher` service.)*

```java
// Conceptual ServerEventPublisher.java (not in codebase, but how it would be used)
public class ServerEventPublisher extends EventBase {
    private final ServerSessionManager serverSessionManager;

    @Inject
    public ServerEventPublisher(ServerSessionManager serverSessionManager) {
        this.serverSessionManager = serverSessionManager;
    }

    @Override
    public void Invoke(ServerSessionManager serverSessionManager, String receiverId, Object... args) throws IOException {
        // Creates the event message with "serverMessage" name and payload
        Message eventMessage = createEventMessage("serverMessage", args);
        // Publishes to a specific user (receiverId) or all if broadcast logic is implemented
        serverSessionManager.publishMessage(receiverId, eventMessage);
    }

    public void publishServerMessageToUser(String userId, String messageContent) throws IOException {
        Invoke(serverSessionManager, userId, messageContent);
    }
}

// In a server-side service or controller:
// @Inject MyAuthService myAuthService; // assuming MyAuthService has a reference to ServerEventPublisher
// @Inject ServerEventPublisher eventPublisher;

// When a user logs in (e.g., in IClientLifecycleListener or MyAuthService)
// eventPublisher.publishServerMessageToUser(userId, "Welcome to the server!");
```

---

## Project Structure

The project is organized into logical packages:

*   **`JSocket2.Core`**: Contains core client and server application logic, session management, and fundamental interfaces.
    *   `Client`: Client-side application, session, builders, and reconnection options.
    *   `Server`: Server-side application, client handlers, session management, and builders.
*   **`JSocket2.Cryptography`**: Handles all cryptographic operations (RSA, AES) and key management.
*   **`JSocket2.DI`**: Implements the custom Dependency Injection container.
*   **`JSocket2.Protocol`**: Defines the communication protocol elements.
    *   `Authentication`: Data models and interfaces for user authentication.
    *   `EventHub`: The publish-subscribe event system.
    *   `Rpc`: Remote Procedure Call definitions, dispatchers, and callers.
    *   `Transfer`: File transfer models and managers for uploads and downloads.
*   **`JSocket2.Utils`**: General utility classes, like file helpers and message encryption/decryption.
*   **`test`**: Unit tests for various components, showcasing correctness and robustness.

---

## Technologies Used

*   **Java 11+**: Core programming language.
*   **Gson**: Google's JSON library for serialization and deserialization of messages and metadata.
*   **JUnit 5**: For unit testing.
*   **JavaFX** (original project context, not directly in this framework's core, but intended for UI integration).

---

## License

This project is open-source and available under the [MIT License](LICENSE.md). Feel free to use, modify, and distribute it for your own projects.

## More Information
[JSocket2: How a C# Developer Rebuilt the Best Parts of ASP.NET in Java](https://medium.com/@mahdihoseinpoor/jsocket2-how-a-c-developer-rebuilt-the-best-parts-of-asp-net-in-java-04701ca0ea39)
