package cat.itacademy.s04.t02.n02.fruit_api_mysql.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FruitResponseDto {
    private Long id;
    private String name;
    private Integer weightInKilos;
    private ProviderResponseDto provider;
}