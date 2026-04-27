package cat.itacademy.s04.t02.n02.fruit_api_mysql.service;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitResponseDto;

import java.util.List;

public interface FruitService {
    FruitResponseDto create(FruitRequestDto dto);

    FruitResponseDto update(Long id, FruitRequestDto dto);

    void delete(Long id);

    FruitResponseDto findById(Long id);

    List<FruitResponseDto> getAll();

    List<FruitResponseDto> getByProvider(Long providerId);
}