package ua.kneu.aviakvytok.exception;

/** Запитаний об'єкт не знайдено в базі даних. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Long id) {
        super(entity + " з id=" + id + " не знайдено");
    }
}
