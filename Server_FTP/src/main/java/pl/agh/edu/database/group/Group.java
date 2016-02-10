package pl.agh.edu.database.group;

import lombok.*;
import lombok.experimental.Accessors;
import pl.agh.edu.database.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = {"name"})
@ToString
@Accessors(chain = true)
@Entity
@Table(name = "groups")
@NoArgsConstructor
/**
 * POJO class representing group entity form database
 */
public class Group {
    private static final long serialVersionUID = 2L;
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private int groupId;
    @Getter
    @Setter
    @Column(name = "group_name", length = 10, nullable = false, unique = true)
    private String name;
    @Getter
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "groups")
    private Set<User> users = new HashSet<>();

}
