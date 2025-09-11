package com.example.whiteboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import io.jsonwebtoken.*;

// -------- SINGLE FILE SPRING BOOT APP --------

@SpringBootApplication
public class WhiteboardApplication {
    public static void main(String[] args) {
        SpringApplication.run(WhiteboardApplication.class, args);
    }
}

// --------- ENTITY ---------
@Document(collection = "canvas")
class Canvas {
    @Id
    String id;
    String owner;
    List<String> shared;
    List<Object> elements;
    // getters, setters (Omitted for brevity)
}

// --------- REPOSITORY ---------
interface CanvasRepository extends MongoRepository<Canvas, String> {}

// --------- JWT UTIL ---------
@Component
class JwtUtil {
    @Value("${jwt.secret:your_secret_key}")
    private String SECRET_KEY;

    public String extractUserId(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        Object uid = claims.get("userId");
        return uid != null ? uid.toString() : null;
    }
}

// --------- USER & CANVAS REST ROUTES ---------
@RestController
@RequestMapping("/api/users")
class UserController {
    // Dummy endpoints for example.
    @GetMapping("/ping") 
    public String ping() { return "pong"; }
}

@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = {"http://localhost:3000", "https://whiteboard-tutorial-eight.vercel.app"})
class CanvasController {
    private final CanvasRepository repo;
    CanvasController(CanvasRepository r) { this.repo = r;}
    @GetMapping("/{id}")
    public ResponseEntity<Canvas> getCanvas(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}

// --------- WEBSOCKET CONFIG ---------
@Configuration
@EnableWebSocket
class WebSocketConfig implements WebSocketConfigurer {
    private final CanvasSocketHandler handler;
    WebSocketConfig(CanvasSocketHandler h) { this.handler = h;}
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/canvas")
                .setAllowedOrigins("http://localhost:3000", "https://whiteboard-tutorial-eight.vercel.app");
    }
}

// --------- WEBSOCKET HANDLER ---------
@Component
class CanvasSocketHandler extends TextWebSocketHandler {

    private final CanvasRepository repo;
    private final JwtUtil jwtUtil;
    // In-memory canvas data like Node.js canvasData
    private final Map<String, Object> canvasData = new HashMap<>();

    public CanvasSocketHandler(CanvasRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("A user connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Parse message, expect JSON {"event":"joinCanvas", ...} or {"event":"drawingUpdate", ...}
        // Use a real JSON parser in production (e.g. Jackson)
        String payload = message.getPayload();
        Map<String, Object> msg = new HashMap<>(); // Replace with actual JSON parsing
        if (payload.contains("joinCanvas")) msg.put("event", "joinCanvas");
        if (payload.contains("drawingUpdate")) msg.put("event", "drawingUpdate");
        String canvasId = extractValue(payload, "canvasId");

        // Get JWT from headers
        String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Deny access
            session.sendMessage(new TextMessage("{\"event\":\"unauthorized\", \"message\":\"Access Denied: No Token\"}"));
            return;
        }
        String token = authHeader.substring(7);
        String userId;
        try {
            userId = jwtUtil.extractUserId(token);
        } catch (Exception e) {
            session.sendMessage(new TextMessage("{\"event\":\"unauthorized\", \"message\":\"Invalid Token\"}"));
            return;
        }

        if ("joinCanvas".equals(msg.get("event"))) {
            Optional<Canvas> optionalCanvas = repo.findById(canvasId);
            if (!optionalCanvas.isPresent()) {
                session.sendMessage(new TextMessage("{\"event\":\"error\", \"message\":\"Canvas not found\"}"));
                return;
            }
            Canvas canvas = optionalCanvas.get();
            if (!canvas.owner.equals(userId) && (canvas.shared==null || !canvas.shared.contains(userId))) {
                session.sendMessage(new TextMessage("{\"event\":\"unauthorized\", \"message\":\"You are not authorized to join this canvas.\"}"));
                return;
            }
            session.sendMessage(new TextMessage("{\"event\":\"authorized\"}"));
            Object elements = canvasData.getOrDefault(canvasId, canvas.elements);
            session.sendMessage(new TextMessage("{\"event\":\"loadCanvas\", \"data\":\"" + elements +"\"}"));
        }

        if ("drawingUpdate".equals(msg.get("event"))) {
            Object elements = extractValue(payload, "elements");
            canvasData.put(canvasId, elements);
            // Broadcast to all sessions except sender (not implemented in this single-file sample)
            // Update MongoDB
            Optional<Canvas> optionalCanvas = repo.findById(canvasId);
            if (optionalCanvas.isPresent()) {
                Canvas canvas = optionalCanvas.get();
                canvas.elements = (List<Object>)elements;
                repo.save(canvas);
            }
        }
    }

    private String extractValue(String payload, String key) {
        // Very naive: replace with real JSON parsing
        if (payload.contains(key)) return "canvasIdValue";
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        System.out.println("User disconnected: " + session.getId());
    }
}
