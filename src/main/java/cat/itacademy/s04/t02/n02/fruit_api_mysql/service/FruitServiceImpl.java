package cat.itacademy.s04.t02.n02.fruit_api_mysql.service;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.exception.FruitNotFoundException;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.exception.ProviderNotFoundException;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.mapper.FruitMapper;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Fruit;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.model.Provider;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.FruitRepository;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FruitServiceImpl implements FruitService {

    private final FruitRepository fruitRepository;
    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public FruitResponseDto create(FruitRequestDto dto) {
        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException(dto.getProviderId()));

        Fruit fruit = FruitMapper.toEntity(dto, provider);
        return FruitMapper.toResponseDto(fruitRepository.save(fruit));
    }

    @Override
    @Transactional
    public FruitResponseDto update(Long id, FruitRequestDto dto) {
        Fruit fruit = fruitRepository.findById(id)
                .orElseThrow(() -> new FruitNotFoundException(id));

        Provider provider = providerRepository.findById(dto.getProviderId())
                .orElseThrow(() -> new ProviderNotFoundException(dto.getProviderId()));

        fruit.setName(dto.getName());
        fruit.setWeightInKilos(dto.getWeightInKilos());
        fruit.setProvider(provider);

        return FruitMapper.toResponseDto(fruitRepository.save(fruit));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!fruitRepository.existsById(id)) {
            throw new FruitNotFoundException(id);
        }
        fruitRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public FruitResponseDto findById(Long id) {
        return fruitRepository.findById(id)
                .map(FruitMapper::toResponseDto)
                .orElseThrow(() -> new FruitNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDto> getAll() {
        return fruitRepository.findAll().stream()
                .map(FruitMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FruitResponseDto> getByProvider(Long providerId) {
        if (!providerRepository.existsById(providerId)) {
            throw new ProviderNotFoundException(providerId);
        }
        return fruitRepository.findByProviderId(providerId).stream()
                .map(FruitMapper::toResponseDto)
                .toList();
    }
}=