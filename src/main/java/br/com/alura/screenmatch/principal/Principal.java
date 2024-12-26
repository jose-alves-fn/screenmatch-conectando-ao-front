// Projeto finalizando, indo ao front-end

package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import java.util.*;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = System.getenv("OMDB_API_KEY");
    private List<DadosSerie> dadosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series = new ArrayList<>();
    // Atributo Optional criado por ser utilizado em mais de um método
    private Optional<Serie> serieBusca;
    // Construtor necessário visto que o Spring está no autowired do repositório
    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }


    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1 - Buscar séries
                    2 - Buscar episódios
                    3 - Listar Séries buscadas
                    4 - Buscar série por título
                    5 - Buscar séries por ator ou atriz
                    6 - Top 5 Séries
                    7 - Buscar séries por gênero
                    8 - Filtrar séries
                    9 - Buscar episódio por trecho
                    10 - Top 5 episódios
                    11 - Buscar episódios a partir de uma data
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtorAtriz();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriesPorGenero();
                    break;
                case 8:
                    filtrarSeries();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosPorData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie(); // Record que esta recebendo e traduzindo a desserialização
        Serie serie = new Serie(dados); // Instanciando e populando um objeto Serie
        // dadosSeries.add(dados);
        repositorio.save(serie); // A definição esta para salvar uma Serie em SerieRepository
        System.out.println(dados);
    }

//        try {
//            DadosSerie dados = getDadosSerie(); // Pode lançar NoSuchElementException
//            Serie serie = new Serie(dados); // Instancia e popula o objeto Serie
//            repositorio.save(serie); // Salva a Série no repositório
//            System.out.println("Série encontrada e salva: " + dados);
//        } catch (NoSuchElementException e) {
//            System.out.println("Série não encontrada. Por favor, verifique o título e tente novamente.");
//        } catch (Exception e) {
//            System.err.println("Ocorreu um erro inesperado: " + e.getMessage());
//        }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
//        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }


    private void buscarEpisodioPorSerie() {
//      DadosSerie dadosSerie = getDadosSerie(); Se se quiser buscar direto na web
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {

            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .toList();

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        } else {
            System.out.println("Série não encontrada");
        }
    }

    private void listarSeriesBuscadas() {

        // Forma de exibir as informaçõrs buscando no Banco de Dados
        // O objeto reposito sinaliza o banco de dados, justamente sendo uma abstração da JPA
        series = repositorio.findAll(); // findAll devolve um list de um tipo genérico, mas nós definimos o tipo como Serie
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da 'serie: " + serieBusca.get());
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSeriePorAtorAtriz() {
        System.out.println("Escolha o ator ou atriz para realizar uma busca: ");
        var nomeAtorAtriz = leitura.nextLine();
        System.out.println("E qual a avaliação escolhida?: ");
        var avaliacaoEscolhida = leitura.nextDouble();
        List<Serie> serieEncontrada = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtorAtriz, avaliacaoEscolhida);

        System.out.println("Série(s) em que " + nomeAtorAtriz + " atuou: ");
        serieEncontrada.forEach(s ->
                System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s ->
                System.out.println("Avaliação: " + s.getAvaliacao() + " | " + s.getTitulo()));
    }

//    private void buscarSeriesPorGenero() {
//        System.out.println("Deseja procurar séries de qual gênero? ");
//        var nomeGenero = leitura.nextLine();
//        Categoria categoria = Categoria.fromPortugues(nomeGenero);
//        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
//        System.out.println("Séries da categoria " + nomeGenero + ":");
//        seriesPorCategoria.forEach(s ->
//                System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
//    }

    private void buscarSeriesPorGenero() {
        System.out.println("Deseja procurar séries de qual gênero? ");
        var nomeGenero = leitura.nextLine();

        try {
            Categoria categoria = Categoria.fromPortugues(nomeGenero);
            List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

            if (seriesPorCategoria.isEmpty()) {
                System.out.println("Nenhuma série encontrada para o gênero: " + nomeGenero);
            } else {
                System.out.println("Séries da categoria " + nomeGenero + ":");
                seriesPorCategoria.forEach(s ->
                        System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Gênero inválido: " + nomeGenero);
        }
    }

    private void filtrarSeries() {
        System.out.println("Deseja procurar séries com quantas temporadas? ");
        var qtTemporadas = leitura.nextInt();
        System.out.println("Deseja procurar séries com avaliação (ou maior que)? ");
        var avaliacao = leitura.nextDouble();

        List<Serie> serieFiltrada = repositorio.seriesPorTemporadaEAlaviacao(qtTemporadas, avaliacao);
        System.out.println("--- Séries Filtradas ---");
        serieFiltrada.forEach(s ->
                System.out.println(s.getTitulo() +
                        " | Temporadas: " + s.getTotalTemporadas() +
                        " | Avaliação: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite um trecho do episódio para a busca: ");
        var trecho = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorTrecho(trecho);
        episodiosEncontrados.forEach(e ->
                System.out.println("Título: " + e.getTitulo() +
                        " | Série: " + e.getSerie().getTitulo() +
                        " | Data de lançamento: " + e.getDataLancamento() +
                        " | Temporada: " + e.getTemporada()));

    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get(); // Puxando a série escolhida de dentro do Optional
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            System.out.println("*** TOP 5 EPISÓDIOS de " + serie.getTitulo() + " ***");

            topEpisodios.forEach(e ->
                    System.out.println("Título: " + e.getTitulo() +
                            " | Nota: " + e.getAvaliacao() +
                            " | Data de lançamento: " + e.getDataLancamento() +
                            " | Temporada: " + e.getTemporada()));
        }
    }

    private void buscarEpisodiosPorData() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get(); // Puxando a série escolhida de dentro do Optional
            System.out.println("Digite o ano limite do lançamento: ");
            var anoLancamento = leitura.nextInt();
            leitura.nextLine(); // Necessário após um next Int

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serie, anoLancamento);
            System.out.println("*** Episódios por data após: " + anoLancamento + " ***");

            episodiosAno.forEach(e ->
                    System.out.println("Título: " + e.getTitulo() +
                            " | Nota: " + e.getAvaliacao() +
                            " | Data de lançamento: " + e.getDataLancamento() +
                            " | Temporada: " + e.getTemporada()));
        }
    }


}















