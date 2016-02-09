package pl.agh.edu.server.commands;

class NoSuchCommandException extends IllegalArgumentException {

    public NoSuchCommandException() {
        super("500 Command does not exist");
    }
}
