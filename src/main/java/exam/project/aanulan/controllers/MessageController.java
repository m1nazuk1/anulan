package exam.project.aanulan.controllers;

import exam.project.aanulan.dto.MessageDto;
import exam.project.aanulan.models.Message;
import exam.project.aanulan.models.Person;
import exam.project.aanulan.repositories.MessageRepository;
import exam.project.aanulan.repositories.PeopleRepository;
import exam.project.aanulan.security.PersonDetails;
import exam.project.aanulan.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final PeopleRepository personRepository;
    private final PersonDetailsService personDetailsService;

    @Autowired
    public MessageController(MessageRepository messageRepository, PeopleRepository personRepository, PersonDetailsService personDetailsService) {
        this.messageRepository = messageRepository;
        this.personRepository = personRepository;
        this.personDetailsService = personDetailsService;
    }

    /**
     * Получение ID текущего пользователя и выбранного контакта.
     */
    @GetMapping("/ids")
    public ResponseEntity<?> getUserAndContactIds() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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

    /**
     * Отправка сообщения.
     */
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

    /**
     * Получение переписки между текущим пользователем и выбранным контактом.
     */
    @GetMapping("/{userId}/{contactId}")
    public ResponseEntity<?> getMessagesWithContact(@PathVariable int userId, @PathVariable int contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentUser = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        if (currentUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        // Получаем сообщения
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, contactId);
        return ResponseEntity.ok(messages);
    }
}