package exam.project.aanulan.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "video")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    private byte[] content;

    private String contentType;

    private LocalDateTime uploadedAt = LocalDateTime.now();
}