package pl.agh.edu.AdminPanel.model;

import lombok.*;
import lombok.experimental.Accessors;
import pl.agh.edu.AdminPanel.utils.PasswordHash;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of = {"name"})
@ToString
@Entity
@Accessors(chain = true)
@Table(name = "users")
@NoArgsConstructor
public class User {

    static final long serialVersionUID = 3L;

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

    public User setPassword(String password) {
        try {
            this.password = PasswordHash.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            System.out.println("creating password hash failed");
        }
        return this;
    }


}
