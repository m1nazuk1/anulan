package exam.project.aanulan.controllers;

import exam.project.aanulan.services.VideoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/videos")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        videoService.saveVideo(file.getOriginalFilename(), file.getContentType(), file.getBytes());
        return ResponseEntity.ok("Video uploaded successfully.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getVideo(@PathVariable Long id) {
        return videoService.getVideo(id)
                .map(video -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, video.getContentType())
                        .body(video.getContent()))
                .orElse(ResponseEntity.notFound().build());
    }
}