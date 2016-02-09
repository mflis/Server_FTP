package pl.agh.edu.database.model;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@EqualsAndHashCode(of = {"name"})
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "users")
@NoArgsConstructor
public class User {


    private static final long serialVersionUID = 3L;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
            , inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "group_id"))
    @Getter
    Set<Group> groups = new HashSet<>();
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;
    @Getter
    @Setter
    @Column(name = "username", length = 10, nullable = false, unique = true, updatable = false)
    private String name;
    @Getter
    @Column(name = "password", nullable = false)
    private String password;

    public Optional<Group> getGroupIfExists(String groupName) {
        Optional<Group> foundGroup = Optional.empty();
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                foundGroup = Optional.of(group);
                break;
            }
        }
        return foundGroup;
    }

}
