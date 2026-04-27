package cat.itacademy.s04.t02.n02.fruit_api_mysql.mapper;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Provider;

public class ProviderMapper {

    private ProviderMapper() {
    }

    public static Provider toEntity(ProviderRequestDto dto) {
        if (dto == null) return null;
        return new Provider(dto.getName(), dto.getCountry());
    }

    public static ProviderResponseDto toResponseDto(Provider provider) {
        if (provider == null) return null;
        return new ProviderResponseDto(provider.getId(), provider.getName(), provider.getCountry());
    }
}