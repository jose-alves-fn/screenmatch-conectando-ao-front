package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// Interface para persistir dados da serie no Banco, é necessário que ela herde da JpaRepository
public interface SerieRepository extends JpaRepository<Serie, Long> {

    // Derived Query Methods ou Query Methods
    // A JPA precisa que o nome do método faça sentido em ingles, sobre o que se deseja, ela vai inferir a demanda
    // verbo introdutório + palavra-chave “By” + critérios de busca

    Optional<Serie> findByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String nomeAtorAtriz, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findBytotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(int totalTemporadas, Double avaliacao);

    // JPQL: Java Persistent Query Language
    // s: sinaliza o objeto por vez, : na frente do parametro para sinalizar que é um parametro
    @Query("SELECT s FROM Serie s WHERE s.totalTemporadas <= :totalTemporadas AND s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAlaviacao(int totalTemporadas, Double avaliacao);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:trecho%")
    List<Episodio> episodioPorTrecho(String trecho);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.avaliacao DESC LIMIT 5")
    List<Episodio> topEpisodiosPorSerie(Serie serie);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie AND YEAR(e.dataLancamento) = :anoLancamento")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoLancamento);

    // Buscar as top 5 series ordenando pela data de lançamento, atentar para os atributos presentes na classe Serie
    List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();

//    @Query("SELECT s FROM Serie s JOIN s.episodios e GROUPY BY s ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
//    List<Serie> encontrarSeriesComEpisodiosMaisRecentes();

}




