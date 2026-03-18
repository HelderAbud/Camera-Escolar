package com.faceblogai.service;

import com.faceblogai.domain.Escola;
import com.faceblogai.domain.Turma;
import com.faceblogai.repository.EscolaRepository;
import com.faceblogai.repository.TurmaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final EscolaRepository escolaRepository;

    public TurmaService(TurmaRepository turmaRepository, EscolaRepository escolaRepository) {
        this.turmaRepository = turmaRepository;
        this.escolaRepository = escolaRepository;
    }

    public List<Turma> listarPorEscola(Long escolaId) {
        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada"));
        return turmaRepository.findByEscola(escola);
    }

    public Optional<Turma> buscarPorId(Long id) {
        return turmaRepository.findById(id);
    }

    @Transactional
    public Turma criar(Long escolaId, String nome, String serie) {
        Escola escola = escolaRepository.findById(escolaId)
                .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada"));
        Turma turma = new Turma(escola, nome, serie);
        return turmaRepository.save(turma);
    }

    @Transactional
    public Optional<Turma> atualizar(Long id, String nome, String serie) {
        return turmaRepository
                .findById(id)
                .map(turma -> {
                    turma.setNome(nome);
                    turma.setSerie(serie);
                    return turmaRepository.save(turma);
                });
    }

    @Transactional
    public void deletar(Long id) {
        turmaRepository.deleteById(id);
    }
}
