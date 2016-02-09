package pl.agh.edu.server.session

import java.nio.file.Paths

class SessionManagerTest extends spock.lang.Specification {
    SessionManager session;

    def setup() {
        session = new SessionManager();
    }

    def "test setCurrentDirectory"() {
        when:
        session.setCurrentDirectory(given);
        then:
        session.currentDirectory == Paths.get(expected);

        where:
        given                      | expected
        "/dir/to/file"             | "FTP/dir/to/file"
        "/dir/to spaced name/file" | "FTP/dir/to spaced name/file"
        "/"                        | "FTP"
    }

    def "test resolveRelativePath"() {
        given:
        session.setCurrentDirectory(current);
        expect:
        session.resolveRelativePath(given) == Paths.get(expected).toFile();

        where:
        given              | current | expected
        "file"             | "/path" | "FTP/path/file"
        "file with spaces" | "/path" | "FTP/path/file with spaces"
        "file with spaces" | "/"     | "FTP/file with spaces"
    }

}
