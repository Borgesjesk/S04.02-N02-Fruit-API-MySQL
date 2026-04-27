package cat.itacademy.s04.t02.n02.fruit_api_mysql.mapper;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Provider;

public class FruitMapper {

    private FruitMapper() {
    }

    public static Fruit toEntity(FruitRequestDto dto, Provider provider) {
        if (dto == null || provider == null) return null;
        return new Fruit(dto.getName(), dto.getWeightInKilos(), provider);
    }

    public static FruitResponseDto toResponseDto(Fruit fruit) {
        if (fruit == null) return null;

        return new FruitResponseDto(
                fruit.getId(),
                fruit.getName(),
                fruit.getWeightInKilos(),
                ProviderMapper.toResponseDto(fruit.getProvider()));
    }
}