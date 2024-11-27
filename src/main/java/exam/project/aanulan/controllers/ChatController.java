//package exam.project.aanulan.controllers;
//
//import exam.project.aanulan.models.Chat;
//import exam.project.aanulan.models.Person;
//import exam.project.aanulan.models.forChats.ChatCreationRequest;
//import exam.project.aanulan.models.forChats.ChatRequest;
//import exam.project.aanulan.repositories.ChatRepository;
//import exam.project.aanulan.repositories.PeopleRepository;
//import exam.project.aanulan.security.PersonDetails;
//import exam.project.aanulan.services.ChatService;
//import exam.project.aanulan.services.PersonDetailsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//@RestController
//@RequestMapping("/chats")
//public class ChatController {
//
//    private final ChatService chatService;
//    private final PeopleRepository personRepository;
//
//    public ChatController(ChatService chatService, PeopleRepository personRepository) {
//        this.chatService = chatService;
//        this.personRepository = personRepository;
//    }
//
//    @PostMapping("/create")
//    public ResponseEntity<Chat> createChat(@RequestBody ChatRequest chatRequest) {
//        // Получаем пользователя по ID из запроса
//        int userId = chatRequest.getUserId();
//        Person user = personRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found"));
//
//        // Создаем чат через сервис
//        Chat chat = chatService.createChat(chatRequest, user);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(chat);
//    }
//
//    @GetMapping("/{chatId}")
//    public ResponseEntity<Chat> getChat(@PathVariable int chatId) {
//        Chat chat = chatService.getChat(chatId);
//        return ResponseEntity.ok(chat);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<Chat>> getUserChats(@PathVariable int userId) {
//        List<Chat> chats = chatService.getChatsForUser(userId);
//        return ResponseEntity.ok(chats);
//    }
//}