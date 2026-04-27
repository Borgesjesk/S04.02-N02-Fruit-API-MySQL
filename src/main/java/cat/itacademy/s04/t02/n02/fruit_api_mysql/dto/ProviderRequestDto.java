package cat.itacademy.s04.t02.n02.fruit_api_mysql.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderRequestDto {

    @NotBlank(message = "Provider name is required")
    private String name;

    @NotBlank(message = "Country is required")
    private String country;
}