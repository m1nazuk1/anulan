/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.controllers;

import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.ImagesRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Optional;

@Controller
public class ImageController {
    private final ImagesRepository imagesRepository;
    private final PeopleRepository peopleRepository;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public ImageController(ImagesRepository imagesRepository, PeopleRepository peopleRepository, PersonDetailsService personDetailsService) {
        this.imagesRepository = imagesRepository;
        this.peopleRepository = peopleRepository;
        this.personDetailsService = personDetailsService;
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
        String id = String.valueOf(peopleRepository.findByUsername(username).get().getPreviewImageId());
        if(!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body("Invalid id format");
        }
        int imageId = Integer.parseInt(id);
        Image image = imagesRepository.findById(imageId).orElse(null);
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