@RestController
@RequestMapping("/api/canvas")
@CrossOrigin(origins = {"http://localhost:3000", "https://whiteboard-tutorial-eight.vercel.app"})
public class CanvasController {

    private final CanvasService canvasService;

    public CanvasController(CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    @PostMapping("/create")
    public ResponseEntity<Canvas> createCanvas(@RequestBody CanvasDto canvasDto, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.createCanvas(canvasDto, userId));
    }

    @PutMapping("/update")
    public ResponseEntity<Canvas> updateCanvas(@RequestBody CanvasDto canvasDto, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.updateCanvas(canvasDto, userId));
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<Canvas> loadCanvas(@PathVariable String id, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.loadCanvas(id, userId));
    }

    @PutMapping("/share/{id}")
    public ResponseEntity<Canvas> shareCanvas(@PathVariable String id, @RequestBody ShareDto shareDto, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.shareCanvas(id, shareDto, userId));
    }

    @PutMapping("/unshare/{id}")
    public ResponseEntity<Canvas> unshareCanvas(@PathVariable String id, @RequestBody ShareDto shareDto, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.unshareCanvas(id, shareDto, userId));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCanvas(@PathVariable String id, HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        canvasService.deleteCanvas(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list")
    public ResponseEntity<List<Canvas>> getUserCanvases(HttpServletRequest req) {
        String userId = (String) req.getAttribute("userId");
        return ResponseEntity.ok(canvasService.getUserCanvases(userId));
    }
}
