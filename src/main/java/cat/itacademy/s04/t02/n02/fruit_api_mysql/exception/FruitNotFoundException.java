package cat.itacademy.s04.t02.n02.fruit_api_mysql.exception;

public class FruitNotFoundException extends RuntimeException {
    public FruitNotFoundException(Long id) {
        super(String.format("Fruit with ID %d was not found in the inventory.", id));
    }
}