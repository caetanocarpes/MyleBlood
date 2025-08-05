package com.sangue.api.controller;

import com.sangue.api.entity.Posto;
import com.sangue.api.repository.PostoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/postos")
@CrossOrigin(origins = "*")
public class PostoController {

    @Autowired
    private PostoRepository postoRepository;

    // Lista todos os postos cadastrados
    @GetMapping
    public List<Posto> listarPostos() {
        return postoRepository.findAll();
    }
}
