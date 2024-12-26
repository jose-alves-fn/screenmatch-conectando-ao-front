package br.com.alura.screenmatch.service.traducao;

public class TesteTraducao {
    public static void main(String[] args) {
        String textoOriginal = "This is a test sentence for translation.";
        System.out.println("Texto original: " + textoOriginal);

        String textoTraduzido = ConsultaMyMemory.obterTraducao(textoOriginal);
        System.out.println("Texto traduzido: " + textoTraduzido);
    }
}