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

    @GetMapping("/senderId/contactId")
    public Map<String, Integer> sc(){


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person1= personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());
        return Map.of(
                "first", person1.getId(),
                "second", ProfileController.contactId
                );

    }

    @PostMapping("/messages/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        Person sender = personRepository.findById(messageDto.getSenderId()).orElse(null);
        Person receiver = personRepository.findById(messageDto.getReceiverId()).orElse(null);

        if (sender == null || receiver == null) {
            return ResponseEntity.badRequest().body("Sender or receiver not found");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());

        System.out.println(messageDto.getContent());
        Message message = new Message();
        message.setSenderName(person.getFirstname());
        message.setReceiverName(receiver.getFirstname());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(messageDto.getContent());
        message.setSendTime(LocalDateTime.now());

        messageRepository.save(message);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/{userId}/{contactId}")
    public ResponseEntity<List<Message>> getMessagesWithContact(@PathVariable int userId, @PathVariable int contactId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = personDetailsService.loadPersonByUsername(((PersonDetails) authentication.getPrincipal()).getUsername());
        if (person.getId() != userId){
            contactId = userId;
            userId = person.getId();
        }
        List<Message> messages = messageRepository.findMessagesBetweenUsers(userId, contactId);


        return ResponseEntity.ok(messages);
    }


}
