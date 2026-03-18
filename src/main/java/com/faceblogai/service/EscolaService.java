package com.faceblogai.service;

import com.faceblogai.domain.Escola;
import com.faceblogai.repository.EscolaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class EscolaService {

    private final EscolaRepository escolaRepository;

    public EscolaService(EscolaRepository escolaRepository) {
        this.escolaRepository = escolaRepository;
    }

    public List<Escola> listarTodas() {
        return escolaRepository.findAll();
    }

    public Page<Escola> listarPaginado(int page, int size) {
        int safeSize = Math.min(size, 100);
        return escolaRepository.findAll(
                PageRequest.of(page, safeSize, Sort.by("nome")));
    }

    public Optional<Escola> buscarPorId(Long id) {
        return escolaRepository.findById(id);
    }

    @Transactional
    public Escola criar(String nome) {
        Escola escola = new Escola(nome);
        return escolaRepository.save(escola);
    }

    @Transactional
    public Optional<Escola> atualizar(Long id, String novoNome) {
        return escolaRepository
                .findById(id)
                .map(escola -> {
                    escola.setNome(novoNome);
                    return escolaRepository.save(escola);
                });
    }

    @Transactional
    public void deletar(Long id) {
        escolaRepository.deleteById(id);
    }
}
