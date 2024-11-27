//package exam.project.aanulan.services;
//
//import exam.project.aanulan.models.Video;
//import exam.project.aanulan.repositories.VideoRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class VideoService {
//    private final VideoRepository videoRepository;
//
//    public VideoService(VideoRepository videoRepository) {
//        this.videoRepository = videoRepository;
//    }
//
//    public void saveVideo(String name, String contentType, byte[] content) {
//        Video video = new Video();
//        video.setName(name);
//        video.setContentType(contentType);
//        video.setContent(content);
//        videoRepository.save(video);
//    }
//
//    public Optional<Video> getVideo(Long id) {
//        return videoRepository.findById(id);
//    }
//}