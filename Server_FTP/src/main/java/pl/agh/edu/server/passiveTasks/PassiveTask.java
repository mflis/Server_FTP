package pl.agh.edu.server.passiveTasks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

@Getter
@Setter
@RequiredArgsConstructor
public class PassiveTask {
    private final TypeOfTask typeOfTask;
    private Optional<InputStream> inputStream = Optional.empty();
    private Optional<OutputStream> outputStream = Optional.empty();

}
