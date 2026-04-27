package cat.itacademy.s04.t02.n02.fruit_api_mysql.controller;

import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitRequestDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.dto.FruitResponseDto;
import cat.itacademy.s04.t02.n02.fruit_api_mysql.service.FruitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fruits")
@RequiredArgsConstructor
public class FruitController {

    private final FruitService service;

    @PostMapping
    public ResponseEntity<FruitResponseDto> create(@Valid @RequestBody FruitRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FruitResponseDto> update(@PathVariable Long id, @Valid @RequestBody FruitRequestDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FruitResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<FruitResponseDto>> getAll(@RequestParam(required = false) Long providerId) {
        if (providerId != null) {
            return ResponseEntity.ok(service.getByProvider(providerId));
        }
        return ResponseEntity.ok(service.getAll());
    }
}