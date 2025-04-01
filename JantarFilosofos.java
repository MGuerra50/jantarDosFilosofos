import java.util.concurrent.Semaphore;

class Filosofo extends Thread {
    private int id;
    private Semaphore hashiEsquerda, hashiDireita;

    public Filosofo(int id, Semaphore hashiEsquerda, Semaphore hashiDireita) {
        this.id = id;
        this.hashiEsquerda = hashiEsquerda;
        this.hashiDireita = hashiDireita;
    }

    private void pensar() throws InterruptedException {
        System.out.println("Filósofo " + id + " está pensando.");
        Thread.sleep((int) (Math.random() * 1000)); // Simula o tempo pensando
    }

    private void comer() throws InterruptedException {
        System.out.println("Filósofo " + id + " está comendo.");
        Thread.sleep((int) (Math.random() * 1000)); // Simula o tempo comendo
    }

    @Override
    public void run() {
        try {
            while (true) {
                pensar();

                // No loop principal do filósofo:
                if (id == 4) {
                    hashiDireita.acquire();
                    hashiEsquerda.acquire();
                } else {
                    hashiEsquerda.acquire();
                    hashiDireita.acquire();
                }

                comer();

                // Libera os hashis
                hashiDireita.release();
                System.out.println("Filósofo " + id + " largou o hashi direito.");

                hashiEsquerda.release();
                System.out.println("Filósofo " + id + " largou o hashi esquerdo.");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class JantarFilosofos {
    public static void main(String[] args) {
        int numFilosofos = 5;
        Semaphore[] hashis = new Semaphore[numFilosofos];
        Filosofo[] filosofos = new Filosofo[numFilosofos];

        // Inicializa os hashis como semáforos binários
        for (int i = 0; i < numFilosofos; i++) {
            hashis[i] = new Semaphore(1); // Apenas um filósofo pode usar cada hashi
        }

        // Cria e inicia os filósofos
        for (int i = 0; i < numFilosofos; i++) {
            filosofos[i] = new Filosofo(i, hashis[i], hashis[(i + 1) % numFilosofos]);
            filosofos[i].start();
        }
    }
}
