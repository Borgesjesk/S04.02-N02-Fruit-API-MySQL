package cat.itacademy.s04.t02.n02.fruit_api_mysql.service;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.ProviderResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.exception.ProviderAlreadyExistsException;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.exception.ProviderHasFruitsException;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.mapper.ProviderMapper;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Provider;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.ProviderRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {
    private final ProviderRepository providerRepository;
    private final FruitRepository fruitRepository;


    @Override
    @Transactional
    public ProviderResponseDto create(ProviderRequestDto dto) {
        if (providerRepository.existsByName(dto.getName())) {
            throw new ProviderAlreadyExistsException(dto.getName());
        }
        Provider provider = ProviderMapper.toEntity(dto);
        return ProviderMapper.toResponseDto(providerRepository.save(provider));
    }

    @Override
    @Transactional
    public ProviderResponseDto update(Long id, ProviderRequestDto dto) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException(id));

        if (providerRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new ProviderAlreadyExistsException(dto.getName());
        }

        provider.setName(dto.getName());
        provider.setCountry(dto.getCountry());

        return ProviderMapper.toResponseDto(providerRepository.save(provider));
    }

    @Override
    public void delete(Long id) {
        if (!providerRepository.existsById(id)) {
            throw new ProviderNotFoundException(id);
        }
        if (!fruitRepository.findByProviderId(id).isEmpty()) {
            throw new ProviderHasFruitsException(id);
        }
        providerRepository.deleteById(id);

    }

    @Override
    @Transactional(readOnly = true)
    public ProviderResponseDto findById(Long id) {
        return providerRepository.findById(id)
                .map(ProviderMapper::toResponseDto)
                .orElseThrow(() -> new ProviderNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponseDto> getAll() {
        return providerRepository.findAll().stream()
                .map(ProviderMapper::toResponseDto)
                .toList();
    }
}