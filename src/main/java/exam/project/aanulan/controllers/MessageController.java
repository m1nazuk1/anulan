/**
 * @author_Nizami_Alekperov
 */

package exam.project.aanulan.controllers;

import exam.project.aanulan.dto.MessageDto;
import exam.project.aanulan.models.Image;
import exam.project.aanulan.models.Message;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.ImagesRepository;
import exam.project.aanulan.repositories.MessageRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.ImageService;
import exam.project.aanulan.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final PeopleRepository personRepository;
    private final PersonDetailsService personDetailsService;
    private final ImagesRepository imagesRepository;
    private final ImageService imageService;

    @Autowired
    public MessageController(MessageRepository messageRepository, PeopleRepository personRepository, PersonDetailsService personDetailsService, ImagesRepository imagesRepository, ImageService imageService) {
        this.messageRepository = messageRepository;
        this.personRepository = personRepository;
        this.personDetailsService = personDetailsService;
        this.imagesRepository = imagesRepository;
        this.imageService = imageService;
    }

    @GetMapping("/contacts")
    public ResponseEntity<?> getContacts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        List<Message> messages = messageRepository.findAllMessagesByUserId(currentUser.getId());
        List<Integer> contactIds = new ArrayList<>();

        for (Message message : messages) {
            if (message.getSender().getId() != currentUser.getId()) {
                contactIds.add(message.getSender().getId());
            }
            if (message.getReceiver().getId() != currentUser.getId()) {
                contactIds.add(message.getReceiver().getId());
            }
        }

        contactIds = contactIds.stream().distinct().collect(Collectors.toList());

        List<Person> contacts = personRepository.findAllById(contactIds);

        return ResponseEntity.ok(contacts);
    }

    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable int messageId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сообщение не найдено");
        }

        if (message.getSender().getId() != currentUser.getId() && message.getReceiver().getId() != currentUser.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Нет прав на удаление этого сообщения");
        }

        messageRepository.delete(message);

        return ResponseEntity.ok("Сообщение успешно удалено");
    }

    @GetMapping("/ids")
    public ResponseEntity<?> getUserAndContactIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        Person currentUser = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        if (ProfileController.contactId == 0) {
            return ResponseEntity.badRequest().body("Contact ID not set");
        }

        Map<String, Integer> ids = Map.of(
                "userId", currentUser.getId(),
                "contactId", ProfileController.contactId
        );

        return ResponseEntity.ok(ids);
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        Person sender = personRepository.findById(messageDto.getSenderId()).orElse(null);
        Person receiver = personRepository.findById(messageDto.getReceiverId()).orElse(null);

        if (sender == null || receiver == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sender or receiver not found");
        }

        Message message = new Message();
        message.setSenderName(sender.getFirstname());
        message.setReceiverName(receiver.getFirstname());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDto.getContent());
        message.setSendTime(LocalDateTime.now());

        messageRepository.save(message);

        return ResponseEntity.ok("Message sent successfully");
    }

    @PostMapping("/sendMedia")
    public ResponseEntity<?> sendMediaMessage(@RequestParam("media") MultipartFile media, @RequestParam("receiverId") int receiverId) throws IOException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person sender = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        // Проверим, что получатель существует
        Person receiver = personRepository.findById(receiverId).orElse(null);
        if (receiver == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Receiver not found");
        }

        // Сохраняем медиа
        Image image = imageService.saveImage(media, sender);
        int mediaId = image.getId();

        // Создаем сообщение с mediaId
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setSenderName(sender.getFirstname());
        message.setReceiverName(receiver.getFirstname());
        message.setMediaId(mediaId);
        message.setSendTime(LocalDateTime.now());
        message.setMediaUrl("http://localhost:8080/images/id/" + mediaId);
        System.out.println(message.getMediaUrl());
        messageRepository.save(message);

        return ResponseEntity.ok(Map.of("message", "Media sent successfully", "mediaId", mediaId));
    }


    @GetMapping("/{userId}/{contactId}")
    public ResponseEntity<?> getMessagesWithContact(@PathVariable int userId, @PathVariable int contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        if (currentUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, contactId);

        // Загружаем медиа, если есть, и передаем их в ответе
        for (Message message : messages) {
            if (message.getMediaId() != null) {
                Image media = imagesRepository.findById(message.getMediaId()).orElse(null);
                if (media != null) {
                    // Устанавливаем поле с URL для изображения
                    message.setMediaUrl("http://localhost:8080/images/id/" + media.getId());
                    System.out.println(message.getMediaUrl());
                }
            }
        }

        return ResponseEntity.ok(messages);
    }
}