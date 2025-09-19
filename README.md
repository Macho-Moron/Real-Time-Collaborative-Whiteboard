# Real-Time Collaborative Whiteboard

This project is a full-stack, real-time collaborative whiteboard application that allows multiple users to draw together simultaneously. It is engineered for low-latency communication and a high-fidelity, smooth user experience, simulating a shared creative canvas.



## 🚀 Core Features

* **Ultra-Low Latency:** The backend, built with Spring Boot WebSockets, broadcasts drawing actions to all connected clients with less than **50ms of latency**, ensuring a seamless real-time experience.
* **High-Fidelity SVG Canvas:** The frontend uses Next.js and TypeScript to render drawings on a dynamic SVG canvas, ensuring crisp, scalable, and accurate visual representation of user strokes.
* **Smooth Drawing Experience:** Integrates the `perfect-freehand` library to transform raw pointer data into beautiful, pressure-sensitive strokes, making digital drawing feel more natural and fluid.
* **Scalable Decoupled Architecture:** Designed with a modern, decoupled architecture where the Spring Boot backend manages state and communication, while the Next.js frontend handles rendering and user interaction. This separation of concerns allows for high scalability and maintainability.

## 🧠 System Architecture

The application's real-time functionality is powered by a WebSocket-based client-server architecture.

1.  **Client-Side Event Capturing:** The Next.js frontend captures user pointer events (mouse or touch) on the SVG canvas.
2.  **WebSocket Connection:** On joining a session, each client establishes a persistent WebSocket connection with the Spring Boot server using the STOMP protocol.
3.  **Broadcasting Actions:** The client sends its drawing data (a stream of points) to a specific topic on the WebSocket server.
4.  **Server-Side Distribution:** The Spring Boot backend receives this data and immediately broadcasts it to all other clients subscribed to the same topic.
5.  **Real-Time Rendering:** All clients (including the original sender) receive the broadcasted point data and use the `perfect-freehand` library to render the smooth, continuous stroke onto their local SVG canvas, keeping everyone's view in sync.

## 🛠️ Tech Stack

| Category         | Technology                               |
| ---------------- | ---------------------------------------- |
| **Frontend** | Next.js, TypeScript, perfect-freehand    |
| **Backend** | Java, Spring Boot                        |
| **Real-Time API**| Spring WebSockets, STOMP Protocol        |
| **Build Tools** | Maven (for Backend), npm/yarn (for Frontend) |

## ⚙️ Getting Started (Local Development)

### Prerequisites

* Java Development Kit (JDK) 11 or higher
* Apache Maven
* Node.js and npm/yarn

### Installation & Setup

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/collaborative-whiteboard.git](https://github.com/your-username/collaborative-whiteboard.git)
    cd collaborative-whiteboard
    ```

2.  **Backend Setup & Launch:**
    ```bash
    cd server # Navigate to the Spring Boot application directory
    mvn spring-boot:run
    ```
    The backend server will start, typically on `http://localhost:8080`.

3.  **Frontend Setup & Launch:**
    ```bash
    cd ../client # Navigate to the Next.js application directory
    npm install
    npm run dev
    ```
    The frontend development server will start, typically on `http://localhost:3000`.

4.  **Usage:** Open `http://localhost:3000` in multiple browser tabs or on different devices to see the real-time collaboration in action.

## ✍️ Author

* **Soham Pal**
