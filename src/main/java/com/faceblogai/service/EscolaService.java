package com.faceblogai.service;

import com.faceblogai.domain.Escola;
import com.faceblogai.repository.EscolaRepository;
import org.springframework.stereotype.Service;

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

    public Optional<Escola> buscarPorId(Long id) {
        return escolaRepository.findById(id);
    }

    public Escola criar(String nome) {
        Escola escola = new Escola(nome);
        return escolaRepository.save(escola);
    }

    public Optional<Escola> atualizar(Long id, String novoNome) {
        return escolaRepository
                .findById(id)
                .map(escola -> {
                    escola.setNome(novoNome);
                    return escolaRepository.save(escola);
                });
    }

    public void deletar(Long id) {
        escolaRepository.deleteById(id);
    }
}
