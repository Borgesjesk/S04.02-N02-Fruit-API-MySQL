package cat.itacademy.s04.t02.n02.fruit_api_mysql.service;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderResponseDto;

import java.util.List;

public interface ProviderService {
    ProviderResponseDto create(ProviderRequestDto dto);

    ProviderResponseDto update(Long id, ProviderRequestDto dto);

    void delete(Long id);

    ProviderResponseDto findById(Long id);

    List<ProviderResponseDto> getAll();
}