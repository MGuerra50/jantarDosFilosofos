import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {

    /*Método para gerar arquivo log.txt com o log de todas as ações feitas pelos filósofos*/
    private static void salvarLogEmArquivo(List<String> log, String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (String linha : log) {
                writer.write(linha); //Escreve cada linha no arquivo log.txt
                writer.newLine(); //Gera a quebra de linha no arquivo
            }
            System.out.println("Log salvo em: " + "\"" + nomeArquivo + "\"");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o log: " + e.getMessage());
        }
    }

    /*Método de execução da aplicação*/
    public static void main(String[] args) throws InterruptedException {
        int numeroFilosofos = 5;
        int tempoExecucao = 180000; // Tempo de execução em milissegundos (3 minutos = 180000ms) da aplicação

        //Os hashis são Semaphore para evitar que mais de um filósofo pege o mesmo hashi de uma vez
        Semaphore[] hashis = new Semaphore[numeroFilosofos]; 

        Filosofo[] filosofos = new Filosofo[numeroFilosofos];

        // Nossa lista de log é feita com synchronizedList devido a ser um recurso que é acessado em um ambiente concorrente, e esse recurso ser pensado para ambientes de concorrencia
        List<String> logAcao = Collections.synchronizedList(new ArrayList<>());
        
        //É um verificador do booleano que diz se as Treads deve continuar em execução ou encerrar seu funcionamento
        AtomicBoolean execucao = new AtomicBoolean(true);

        // O garçom é tem a função de evitar que mais de 4 filósofos pegue os hashis simultaneamente, e é usado para evitar deadlock
        Semaphore garcom = new Semaphore(numeroFilosofos - 1);

        // Inicializa os hashis (um para cada posição na mesa)
        for (int i = 0; i < numeroFilosofos; i++) {
            hashis[i] = new Semaphore(1);
        }

        // Cria e inicia os filósofos
        for (int i = 0; i < numeroFilosofos; i++) {
            filosofos[i] = new Filosofo(i, hashis[i], hashis[(i + 1) % numeroFilosofos], garcom, logAcao, execucao);
            filosofos[i].start();
        }

        // Permite que a aplicação rode, até que running.set receba o valor false
        Thread.sleep(tempoExecucao);
        execucao.set(false);

        // Fica esperando as execuções das Threads dos filósofos se encerrarem para permitir o programa continuar a execução
        for (Filosofo f : filosofos) {
            f.join();
        }

        // Se o tamanhho do log for menor que 1007 linhas, imprime no console, do contrário, salva no arquivo log.txt
        if (logAcao.size() < 1007) {
            System.out.println("Log de ações:");
            for (String s : logAcao) {
                System.out.println(s);
            }
        } else {
            System.out.println("\nO Log de ações foi passado para o arquivo \"log.txt\", pois não há espaço suficiente no console para o log completo.");
            salvarLogEmArquivo(logAcao, "log.txt");
        }

        // Exibe as estatísticas de execução
        System.out.println("\nEstatísticas:");
        for (Filosofo f : filosofos) {
            System.out.println("Filósofo " + f.idFilosofo + " - Pensou: " + f.getContadorPensarFilosofo() + " vezes (" + f.getTempoGastoPensando() + " ms), Comeu: " + f.getContadorComerFilosofo() + " vezes (" + f.getTempoGastoComendo() + " ms).");
        }
    }
}