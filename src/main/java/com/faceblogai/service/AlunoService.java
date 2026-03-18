package com.faceblogai.service;

import com.faceblogai.domain.Aluno;
import com.faceblogai.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Page<Aluno> listarPaginado(int page, int size) {
        int safeSize = Math.min(size, 100);
        return alunoRepository.findAll(
                PageRequest.of(page, safeSize, Sort.by("nome")));
    }

    public Optional<Aluno> buscarPorId(Long id) {
        return alunoRepository.findById(id);
    }

    @Transactional
    public Aluno criar(String nome, String matricula) {
        Aluno aluno = new Aluno(nome, matricula);
        return alunoRepository.save(aluno);
    }

    @Transactional
    public Optional<Aluno> atualizar(Long id, String nome) {
        return alunoRepository
                .findById(id)
                .map(aluno -> {
                    aluno.setNome(nome);
                    return alunoRepository.save(aluno);
                });
    }

    @Transactional
    public void deletar(Long id) {
        alunoRepository.deleteById(id);
    }
}
