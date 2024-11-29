/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.controllers;

import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.ImagesRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.ImageService;
import exam.project.aanulan.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Controller
public class ImageController {
    private final ImagesRepository imagesRepository;
    private final PeopleRepository peopleRepository;
    private final PersonDetailsService personDetailsService;
    private final ImageService imageService;

    @Autowired
    public ImageController(ImagesRepository imagesRepository, PeopleRepository peopleRepository, PersonDetailsService personDetailsService, ImageService imageService) {
        this.imagesRepository = imagesRepository;
        this.peopleRepository = peopleRepository;
        this.personDetailsService = personDetailsService;
        this.imageService = imageService;
    }

    @GetMapping("/images/id/{imageId}")
    @ResponseBody
    public ResponseEntity<?> getImageById(@PathVariable("imageId") int imageId) {

        Optional<Image> imageOpt = imagesRepository.findById(imageId);

        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if image is not found
        }

        Image image = imageOpt.get();

        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    }

    @Transactional
    @PutMapping("/addImage")
    public ResponseEntity<?> forImage(@RequestParam("image") MultipartFile image) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }
        Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        imageService.enterImageId(person.getUsername(), image);

        return ResponseEntity.ok(Map.of("message", "данные пользователя успешно изменены"));
    }

    @Transactional
    @PutMapping("/removeImage")
    public ResponseEntity<?> removeImage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пользователь не аутентифицирован");
        }

        Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        person.setPreviewImageId(1);
        peopleRepository.save(person);

        return ResponseEntity.ok(Map.of("message", "Аватар успешно удален, установлен дефолтный аватар"));
    }

    @GetMapping("/images/{username}")
    @ResponseBody
    public ResponseEntity<?> getImageId(@PathVariable("username") String username) {
        Optional<Person> personOpt = peopleRepository.findByUsername(username);
        if (personOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return a 404 if the user doesn't exist
        }

        Person person = personOpt.get();
        String id = String.valueOf(person.getPreviewImageId());

        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Invalid id format");
        }

        int imageId = Integer.parseInt(id);
        Optional<Image> imageOpt = imagesRepository.findById(imageId);

        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if image is not found
        }

        Image image = imageOpt.get();

        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    }


    @GetMapping("/images/avatar/{username}")
    @ResponseBody
    public ResponseEntity<?> getAvatar(@PathVariable("username") String username) {
        Optional<Person> userOpt = peopleRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String imageIdStr = String.valueOf(userOpt.get().getPreviewImageId());

        if (!imageIdStr.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Invalid image ID format");
        }

        int imageId = Integer.parseInt(imageIdStr);

        Optional<Image> imageOpt = imagesRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Image image = imageOpt.get();

        return ResponseEntity.ok()
                .header("fileName", image.getOriginalFileName())
                .contentType(MediaType.valueOf(image.getContentType()))
                .contentLength(image.getSize())
                .body(new InputStreamResource(new ByteArrayInputStream(image.getData())));
    }
}