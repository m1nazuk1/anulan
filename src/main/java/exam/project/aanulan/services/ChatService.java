//package exam.project.aanulan.services;
//
//import exam.project.aanulan.models.Chat;
//import exam.project.aanulan.models.Person;
//import exam.project.aanulan.models.forChats.ChatRequest;
//import exam.project.aanulan.repositories.ChatRepository;
//import exam.project.aanulan.repositories.PeopleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class ChatService {
//
//    private final ChatRepository chatRepository;
//
//    public ChatService(ChatRepository chatRepository) {
//        this.chatRepository = chatRepository;
//    }
//
//    public Chat createChat(ChatRequest chatRequest, Person user) {
//        // Создаем новый чат
//        Chat chat = new Chat();
//        chat.setName(chatRequest.getName());
//        chat.getParticipants().add(user); // Добавляем пользователя как участника
//
//        // Сохраняем в базу
//        return chatRepository.save(chat);
//    }
//
//    public Chat getChat(int chatId) {
//        return chatRepository.findById(chatId)
//                .orElseThrow(() -> new IllegalArgumentException("Chat with ID " + chatId + " not found"));    }
//
//    public List<Chat> getChatsForUser(int userId) {
//        return chatRepository.findChatsByParticipants_Id(userId);
//    }
//}