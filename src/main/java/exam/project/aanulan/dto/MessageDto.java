package exam.project.aanulan.dto;

import java.time.LocalDateTime;

public class MessageDto {
    private int senderId;
    private int receiverId;
    private String content;
    private LocalDateTime sendTime;



    public MessageDto() {
    }

    public MessageDto(int senderId, int receiverId, String content, LocalDateTime sendTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.sendTime = sendTime;
    }



    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
}
