package trabalho2.batalhanaval;

import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;

public class Batalhanaval {

    static final int TAMANHOMAPA = 10;
    static final char AGUA = 'o';
    static final char BARQUINHO = 'X';
    static final char ACERTARTIRO = '0';
    static final char ERRARTIRO = ' ';
    static final int[] TAMANHOBARCO = { 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };
    
    Scanner ler = new Scanner(System.in);
    Random aleatorio = new Random();

    char[][] mapajogador1;
    char[][] mapajogador2;
    boolean contraComputador;

    public Batalhanaval() {
        mapajogador1 = new char[TAMANHOMAPA][TAMANHOMAPA];
        mapajogador2 = new char[TAMANHOMAPA][TAMANHOMAPA];
        for (char[] linha : mapajogador1) {
            Arrays.fill(linha, AGUA);
        }
        for (char[] linha : mapajogador2) {
            Arrays.fill(linha, AGUA);
        }
    }

    public void iniciarJogo() {
        System.out.println("Bem-vindo ao jogo Batalha Naval do FDS!");
        definirModoJogo();
        alocarBarcos(mapajogador1);
        if (contraComputador) {
            alocarBarcosComputador(mapajogador2);
        } else {
            alocarBarcos(mapajogador2);
        }
        jogar();
    }

    private void definirModoJogo() {
        System.out.println("Escolha o modo de jogo:");
        System.out.println("1 - Jogar contra o computador");
        System.out.println("2 - Jogar contra outro jogador");
        System.out.println("3 - Sair");
        int opcao = lerInteiroEntre(1, 3);
        switch (opcao) {
            case 1:
                contraComputador = true;
                break;
            case 2:
                contraComputador = false;
                break;
            case 3:
                System.exit(0);
        }
    }

    private void alocarBarcos(char[][] mapa) {
        for (int tamanho : TAMANHOBARCO) {
            exibirMapa(mapa);
            System.out.println("Aloque um barco de tamanho " + tamanho + ".");
            boolean alocado = false;

            while (!alocado) {
                System.out.println("Informe a posição inicial (linha coluna):");
                int linha = lerInteiroEntre(0, TAMANHOMAPA - 1);
                int coluna = lerInteiroEntre(0, TAMANHOMAPA - 1);

                System.out.println("Informe a orientação (1 - horizontal, 2 - vertical):");
                int orientacao = lerInteiroEntre(1, 2);

                if (verificarDisponibilidade(mapa, linha, coluna, tamanho, orientacao)) {
                    alocarBarco(mapa, linha, coluna, tamanho, orientacao);
                    alocado = true;
                } else {
                    System.out.println("Posição inválida! Tente novamente.");
                }
            }
        }
    }

    private boolean verificarDisponibilidade(char[][] mapa, int linha, int coluna, int tamanho, int orientacao) {
        if (orientacao == 1 && coluna + tamanho <= TAMANHOMAPA) {
            for (int i = coluna; i < coluna + tamanho; i++) {
                if (mapa[linha][i] != AGUA) {
                    return false;
                }
            }
            return true;
        } else if (orientacao == 2 && linha + tamanho <= TAMANHOMAPA) {
            for (int i = linha; i < linha + tamanho; i++) {
                if (mapa[i][coluna] != AGUA) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void alocarBarco(char[][] mapa, int linha, int coluna, int tamanho, int orientacao) {
        if (orientacao == 1) {
            for (int i = coluna; i < coluna + tamanho; i++) {
                mapa[linha][i] = BARQUINHO;
            }
        } else if (orientacao == 2) {
            for (int i = linha; i < linha + tamanho; i++) {
                mapa[i][coluna] = BARQUINHO;
            }
        }
    }

    private void alocarBarcosComputador(char[][] mapa) {
        for (int tamanho : TAMANHOBARCO) {
            int linha, coluna, orientacao;
            do {
                linha = aleatorio.nextInt(TAMANHOMAPA);
                coluna = aleatorio.nextInt(TAMANHOMAPA);
                orientacao = aleatorio.nextInt(2) + 1;
            } while (!verificarDisponibilidade(mapa, linha, coluna, tamanho, orientacao));
            alocarBarco(mapa, linha, coluna, tamanho, orientacao);
        }
    }

    private void jogar() {
    boolean jogoAcabou = false;
    boolean turnoJogador1 = true;

    while (!jogoAcabou) {
        char[][] mapaAtaque;
        char[][] mapaDefesa;
        String jogador;
        if (turnoJogador1) {
            mapaAtaque = mapajogador1;
            mapaDefesa = mapajogador2;
            jogador = "Jogador 1";
            exibirMapa(mapajogador1);  // Exibe o mapa do Jogador 1
        } else {
            mapaAtaque = mapajogador2;
            mapaDefesa = mapajogador1;
            jogador = (contraComputador) ? "Computador" : "Jogador 2";
            exibirMapa(mapajogador2);  // Exibe o mapa do Jogador 2
        }

        System.out.println(jogador + ", é sua vez de atacar.");
        System.out.println("Informe a posição do ataque (linha coluna):");
        int linha = lerInteiroEntre(0, TAMANHOMAPA - 1);
        int coluna = lerInteiroEntre(0, TAMANHOMAPA - 1);

        if (mapaDefesa[linha][coluna] == BARQUINHO) {
            mapaDefesa[linha][coluna] = ACERTARTIRO;
            mapaAtaque[linha][coluna] = ACERTARTIRO;
            System.out.println("Você acertou um navio!");
            if (verificarFimJogo(mapaDefesa)) {
                jogoAcabou = true;
                System.out.println(jogador + " venceu o jogo!");
            }
        } else if (mapaDefesa[linha][coluna] == AGUA) {
            mapaDefesa[linha][coluna] = ERRARTIRO;
            mapaAtaque[linha][coluna] = ERRARTIRO;
            System.out.println("Você acertou na água!");
            turnoJogador1 = !turnoJogador1;
        } else {
            System.out.println("Você já atirou nessa posição. Tente novamente.");
        }

        if (!jogoAcabou && contraComputador && !turnoJogador1) {
            realizarAtaqueComputador();
        }
    }
}

    private void realizarAtaqueComputador() {
        int linha, coluna;
        do {
            linha = aleatorio.nextInt(TAMANHOMAPA);
            coluna = aleatorio.nextInt(TAMANHOMAPA);
        } while (mapajogador1[linha][coluna] == ACERTARTIRO || mapajogador1[linha][coluna] == ERRARTIRO);

        System.out.println("O computador está atacando...");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        if (mapajogador1[linha][coluna] == BARQUINHO) {
            mapajogador1[linha][coluna] = ACERTARTIRO;
            mapajogador2[linha][coluna] = ACERTARTIRO;
            System.out.println("O computador acertou um navio!");
            if (verificarFimJogo(mapajogador1)) {
                System.out.println("O computador venceu o jogo!");
            }
        } else {
            mapajogador1[linha][coluna] = ERRARTIRO;
            mapajogador2[linha][coluna] = ERRARTIRO;
            System.out.println("O computador acertou na água!");
        }
    }

    private boolean verificarFimJogo(char[][] mapa) {
        for (char[] linha : mapa) {
            for (char celula : linha) {
                if (celula == BARQUINHO) {
                    return false;
                }
            }
        }
        return true;
    }

    private void exibirMapa(char[][] mapa) {
        int il = 0;
        int ic = 0;
        
        System.out.println("Mapa:");
        
        System.out.print("  ");
        for (ic = 0; ic < 10; ic++){
            System.out.printf("%d ",ic);
        }
        System.out.println("");
        
        for (char[] linha : mapa) {
            System.out.printf( "%d ",il++);
            for (char celula : linha) {
                System.out.print(celula + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private int lerInteiroEntre(int min, int max) {
        int valor;
        do {
            System.out.print("Escolha uma opção: ");
            while(!ler.hasNextInt()){
                System.out.print("Escolha um caracter valido!!!");
                ler.next();
            }
            valor = ler.nextInt();
        } while (valor < min || valor > max);
        return valor;
    }

    public static void main(String... args) {
        Batalhanaval jogo = new Batalhanaval();
        jogo.iniciarJogo();
    }
}
