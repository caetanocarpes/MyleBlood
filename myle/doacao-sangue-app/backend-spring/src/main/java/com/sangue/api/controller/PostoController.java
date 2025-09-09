package com.sangue.api.controller;

import com.sangue.api.entity.Posto;
import com.sangue.api.repository.PostoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/postos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PostoController {

    private final PostoRepository postoRepository;

    @GetMapping
    public ResponseEntity<List<Posto>> listarPostos() {
        return ResponseEntity.ok(postoRepository.findAll());
    }
}
