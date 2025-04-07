/*
 * Integrantes do grupo:
 * Ana Carolina Silva Borges
 * Gabriel Sateles de Andrade Rangel
 * Marcos Kazu Yamara Watanabe
 * Matheus Guerra Martins
*/

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

class Filosofo extends Thread {
    //Variáveis de um filósofo
    int idFilosofo;
    private Semaphore hashiE, hashiD, garcom;
    private List<String> logDeAcao;
    private int contadorPensarFilosofo = 0;
    private int contadorComerFilosofo = 0;
    private long tempoGastoComendo = 0; 
    private long tempoGastoPensando = 0;
    private AtomicBoolean execucao;

    // Métodos acessores
    public int getContadorPensarFilosofo() {
        return contadorPensarFilosofo;
    }
    public int getContadorComerFilosofo() {
        return contadorComerFilosofo;
    }
    public long getTempoGastoPensando() {
        return tempoGastoPensando;
    }
    public long getTempoGastoComendo() {
        return tempoGastoComendo;
    }
    
   //Construtor do filósofo
    public Filosofo(int id, Semaphore hashiEsquerda, Semaphore hashiDireita, Semaphore garcom, List<String> log, AtomicBoolean running) {
        this.idFilosofo = id;
        this.hashiE = hashiEsquerda;
        this.hashiD = hashiDireita;
        this.garcom = garcom;
        this.logDeAcao = log;
        this.execucao = running;
    }

    // Gera valores de 1 até 1000, nesse contexto de milissegundos, o filósofo pode pensar de 1ms até 1s
    private void pensar() throws InterruptedException {
        long inicio = System.currentTimeMillis(); // Inicia a verificação do tempo gasto para pensar
        logDeAcao.add("Filósofo " + idFilosofo + " está pensando.");
        contadorPensarFilosofo++;
        Thread.sleep((int) (Math.random() * 1000) + 1);
        tempoGastoPensando += System.currentTimeMillis() - inicio; // Encerra a verificação do tempo gasto para pensar
    }

    // Gera valores de 5 até 500, nesse contexto de milissegundos, o filósofo pode comer de 5ms até 500ms
    private void comer() throws InterruptedException {
        long inicio = System.currentTimeMillis(); // Inicia a verificação do tempo gasto para comer
        logDeAcao.add("Filósofo " + idFilosofo + " está comendo.");
        contadorComerFilosofo++;
        Thread.sleep((int) (Math.random() * 496) + 5);
        logDeAcao.add("Filósofo " + idFilosofo + " terminou de comer.");
        tempoGastoComendo += System.currentTimeMillis() - inicio; //Encerra a verificação do tempo gasto para comer
    }

    public void run() {
        try {
            //Executa enquanto tempo de execução selecionado estiver ativo, quando o tempo encerrar, o loop também encerra
            while (execucao.get()) {
                // Filósofo inicia pensando
                pensar();

                // Verifica com o garçom se pode pegar um hashi, pois, o garçom só 
                // permite 4 filósofos pegar hashis ao mesmo tempo, isso evita o deadlock
                garcom.acquire();

                // O filósofo tenta pegar o hashi esquerdo, que só pode ser pego se não estiver 
                // em uso por outro filósofo
                hashiE.acquire();
                logDeAcao.add("Filósofo " + idFilosofo + " pegou o hashi esquerdo.");

                // O filósofo tenta pegar o hashi direito, que só pode ser pego se não estiver 
                // em uso por outro filósofo
                hashiD.acquire();
                logDeAcao.add("Filósofo " + idFilosofo + " pegou o hashi direito.");

                // Se o filósofo conseguiu pergar os dois hashis, então ele come.
                comer();

                // Libera o hashi direito para que outros filósofos também possão usar
                hashiD.release();
                logDeAcao.add("Filósofo " + idFilosofo + " largou o hashi direito.");

                // Libera o hashi esquerdo para que outros filósofos também possão usar
                hashiE.release();
                logDeAcao.add("Filósofo " + idFilosofo + " largou o hashi esquerdo.");

                // Libera o garçom para que outro filósofo possa tentar comer
                garcom.release();

                logDeAcao.add("Filósofo " + idFilosofo + " voltou a pensar.");
            }
        } catch (InterruptedException e) {
            // Em caso ocorra algum erro que gere uma interrupção, ele reinterrompe a thread e registra no log
            Thread.currentThread().interrupt();
            logDeAcao.add("Filósofo " + idFilosofo + " foi interrompido.");
        }
    }
}
