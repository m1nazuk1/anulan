package exam.project.aanulan.repositories;

import exam.project.aanulan.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChat_IdOrderByTimestampAsc(int chatId);
}