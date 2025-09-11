import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import java.util.Date;
import java.util.List;

@Document(collection = "canvas")
public class Canvas {

    @Id
    private String id;

    @DBRef
    private User owner; // Reference to User entity

    @DBRef
    private List<User> shared; // References to User entities

    private List<Object> elements; // Elements as generic objects

    private Date createdAt = new Date();

    // Getters and Setters
    // (Omitted for brevity)
}
