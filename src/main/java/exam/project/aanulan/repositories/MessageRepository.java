package exam.project.aanulan.repositories;

import exam.project.aanulan.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.*;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :contactId) OR (m.sender.id = :contactId AND m.receiver.id = :userId) ORDER BY m.sendTime ASC")
    List<Message> findMessagesBetweenUsers(int userId, int contactId);
}
