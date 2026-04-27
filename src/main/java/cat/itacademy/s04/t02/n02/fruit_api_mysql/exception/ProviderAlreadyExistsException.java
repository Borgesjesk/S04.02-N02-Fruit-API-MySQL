package cat.itacademy.s04.t02.n02.fruit_api_mysql.exception;

public class ProviderAlreadyExistsException extends RuntimeException {
    public ProviderAlreadyExistsException(String name) {
        super(String.format("Provider '%s' is already registered in the system.", name));
    }
}