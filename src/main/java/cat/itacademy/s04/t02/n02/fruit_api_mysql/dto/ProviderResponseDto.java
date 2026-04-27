package cat.itacademy.s04.t02.n02.fruit_api_mysql.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderResponseDto {
    private Long id;
    private String name;
    private String country;
}