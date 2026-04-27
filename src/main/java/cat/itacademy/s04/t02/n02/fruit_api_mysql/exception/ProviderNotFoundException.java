package cat.itacademy.s04.t02.n02.fruit_api_mysql.exception;

public class ProviderNotFoundException extends RuntimeException {
    public ProviderNotFoundException(Long id) {
        super(String.format("Provider with ID %d not found.", id));
    }
}