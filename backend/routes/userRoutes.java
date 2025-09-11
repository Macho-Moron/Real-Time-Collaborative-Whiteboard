@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "https://whiteboard-tutorial-eight.vercel.app"})
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // POST /register
    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterDto registerDto) {
        UserDto registeredUser = userService.registerUser(registerDto);
        return ResponseEntity.ok(registeredUser);
    }

    // POST /login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody LoginDto loginDto) {
        LoginResponseDto response = userService.loginUser(loginDto);
        return ResponseEntity.ok(response);
    }

    // GET /me (protected, JWT required)
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        String userId = jwtUtil.extractUserId(token);
        UserDto user = userService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}
