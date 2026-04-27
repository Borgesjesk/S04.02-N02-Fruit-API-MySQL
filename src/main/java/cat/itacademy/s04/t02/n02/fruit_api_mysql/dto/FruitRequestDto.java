package cat.itacademy.s04.t02.n02.fruit_api_mysql.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FruitRequestDto {

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotBlank(message = "Fruit name is required")
    private String name;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be a positive number")
    private Integer weightInKilos;
}