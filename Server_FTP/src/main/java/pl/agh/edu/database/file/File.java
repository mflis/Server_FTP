package pl.agh.edu.database.file;

import lombok.*;
import lombok.experimental.Accessors;
import pl.agh.edu.database.group.Group;
import pl.agh.edu.database.user.User;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(of = {"pathToFile"})
@NoArgsConstructor
@Entity
@Accessors(chain = true)
@Table(name = "files")
/**
 * POJO class representing file entity form database
 */
public class File {

    private static final long serialVersionUID = 1L;
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private int fileId;
    @Column(name = "filename", nullable = false, unique = true)
    private String pathToFile;
    @ManyToOne
    private User owner;
    @ManyToOne
    private Group group;
    @Column(name = "user_read")
    private boolean userRead = true;
    @Column(name = "user_write")
    private boolean userWrite = true;
    @Column(name = "group_read")
    private boolean groupRead = false;
    @Column(name = "group_write")
    private boolean groupWrite = false;
}
