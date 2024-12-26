package br.com.alura.screenmatch.service.traducao;

import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.model.DadosSerie;

public class TesteTraducaoDetalhado {
    public static void main(String[] args) {
        // Teste direto do método de tradução
        String textoOriginal = "This is a test sentence for translation.";
        System.out.println("Teste direto de tradução:");
        System.out.println("Original: " + textoOriginal);
        String textoTraduzido = ConsultaMyMemory.obterTraducao(textoOriginal);
        System.out.println("Traduzido: " + textoTraduzido);
        System.out.println();

        // Teste com a classe Serie
        DadosSerie dadosSerie = new DadosSerie(
                "Breaking Bad",
                5,
                "9.5",
                "Drama, Crime",
                "Bryan Cranston, Aaron Paul",
                "https://example.com/poster.jpg",
                "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine in order to secure his family's future."
        );

        System.out.println("Teste com a classe Serie:");
        System.out.println("Sinopse original: " + dadosSerie.sinopse());

        Serie serie = new Serie(dadosSerie) {
            @Override
            public String toString() {
                return "Serie{" +
                        "titulo='" + getTitulo() + '\'' +
                        ", sinopse='" + getSinopse() + '\'' +
                        '}';
            }
        };

        System.out.println("Objeto Serie criado: " + serie);
        System.out.println("getSinopse(): " + serie.getSinopse());
    }
}