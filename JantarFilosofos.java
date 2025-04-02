import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

class Filosofo extends Thread {
    int id;
    private Semaphore hashiEsquerda, hashiDireita;
    private List<String> log;
    private int contPensar = 0;
    private int contComer = 0;
    private AtomicBoolean running;

    public Filosofo(int id, Semaphore hashiEsquerda, Semaphore hashiDireita, List<String> log, AtomicBoolean running) {
        this.id = id;
        this.hashiEsquerda = hashiEsquerda;
        this.hashiDireita = hashiDireita;
        this.log = log;
        this.running = running;
    }

    private void pensar() throws InterruptedException {
        log.add("Filósofo " + id + " está pensando.");
        contPensar++;
        Thread.sleep((int) (Math.random() * 1000)); // Simula o tempo pensando
    }

    private void comer() throws InterruptedException {
        log.add("Filósofo " + id + " está comendo.");
        contComer++;
        Thread.sleep((int) (Math.random() * 1000)); // Simula o tempo comendo
    }

    public int getContPensar() {
        return contPensar;
    }

    public int getContComer() {
        return contComer;
    }

    @Override
    public void run() {
        try {
            while (running.get()) {
                pensar();

                // Ordem de aquisição dos hashis:
                if (id == 4) {
                    hashiDireita.acquire();
                    hashiEsquerda.acquire();
                } else {
                    hashiEsquerda.acquire();
                    hashiDireita.acquire();
                }

                comer();

                // Libera os hashis e registra a ação
                hashiDireita.release();
                log.add("Filósofo " + id + " largou o hashi direito.");
                hashiEsquerda.release();
                log.add("Filósofo " + id + " largou o hashi esquerdo.");
            }
        } catch (InterruptedException e) {
            // Se for interrompido, encerra a thread
        }
    }
}

public class JantarFilosofos {
    public static void main(String[] args) throws InterruptedException {
        int numFilosofos = 5;
        int tempoExecucao = 30000; // tempo de execução em milissegundos (ex: 10 segundos)
        Semaphore[] hashis = new Semaphore[numFilosofos];
        Filosofo[] filosofos = new Filosofo[numFilosofos];

        // Lista sincronizada para registrar as ações
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        // Flag para controle de execução
        AtomicBoolean running = new AtomicBoolean(true);

        // Inicializa os hashis (semáforos binários)
        for (int i = 0; i < numFilosofos; i++) {
            hashis[i] = new Semaphore(1);
        }

        // Cria e inicia os filósofos
        for (int i = 0; i < numFilosofos; i++) {
            filosofos[i] = new Filosofo(i, hashis[i], hashis[(i + 1) % numFilosofos], log, running);
            filosofos[i].start();
        }

        // Deixa o jantar ocorrer por um tempo determinado
        Thread.sleep(tempoExecucao);
        running.set(false); // Sinaliza para as threads encerrarem

        // Aguarda o término de todas as threads
        for (Filosofo f : filosofos) {
            f.join();
        }

        // Exibe o log de ações (opcional)
        System.out.println("Log de ações:");
        for (String s : log) {
            System.out.println(s);
        }

        // Exibe as estatísticas
        System.out.println("\nEstatísticas:");
        for (Filosofo f : filosofos) {
            System.out.println("Filósofo " + f.id + " - Pensou: " + f.getContPensar() + " vezes, Comeu: " + f.getContComer() + " vezes.");
        }
    }
}
