package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.Categoria;

// DTO é a sigla para Data Transfer Object, que é um padrão de projeto de software que permite a transferência
// de dados entre camadas de uma aplicação

public record SerieDTO(Long id,
                       String titulo,
                       Integer totalTemporadas,
                       Double avaliacao,
                       Categoria genero,
                       String atores,
                       String poster,
                       String sinopse)
{}
