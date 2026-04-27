package cat.itacademy.s04.t02.n02.fruit_api_mysql.exception;

public class ProviderHasFruitsException extends RuntimeException {
    public ProviderHasFruitsException(Long id) {
        super(String.format("Integrity Constraint Violation: Provider ID %d cannot be deleted while associated fruits exist.", id));
    }
}