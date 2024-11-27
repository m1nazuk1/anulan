//package exam.project.aanulan.models;
//
//import javax.persistence.*;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@Entity
//public class Chat {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @ManyToMany
//    @JoinTable(
//            name = "chat_participants",
//            joinColumns = @JoinColumn(name = "chat_id"),
//            inverseJoinColumns = @JoinColumn(name = "person_id")
//    )
//    private Set<Person> participants = new HashSet<>();
//
//    // Getters and setters
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Set<Person> getParticipants() {
//        return participants;
//    }
//
//    public void setParticipants(Set<Person> participants) {
//        this.participants = participants;
//    }
//}