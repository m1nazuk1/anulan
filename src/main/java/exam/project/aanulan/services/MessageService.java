//package exam.project.aanulan.services;
//
//import exam.project.aanulan.models.Chat;
//import exam.project.aanulan.models.Message;
//import exam.project.aanulan.models.Person;
//import exam.project.aanulan.repositories.MessageRepository;
//import exam.project.aanulan.repositories.PeopleRepository;
//import exam.project.aanulan.repositories.ChatRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class MessageService {
//
//    private final MessageRepository messageRepository;
//    private final ChatRepository chatRepository;
//
//    public MessageService(MessageRepository messageRepository, ChatRepository chatRepository) {
//        this.messageRepository = messageRepository;
//        this.chatRepository = chatRepository;
//    }
//
//    public Message sendMessage(int chatId, Person sender, String content) {
//        Chat chat = chatRepository.findById(chatId)
//                .orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));
//
//        Message message = new Message();
//        message.setChat(chat);
//        message.setSender(sender);
//        message.setContent(content);
//        message.setTimestamp(LocalDateTime.now());
//
//        return messageRepository.save(message);
//    }
//
//    public List<Message> getMessages(int chatId) {
//        return messageRepository.findByChat_IdOrderByTimestampAsc(chatId);
//    }
//}