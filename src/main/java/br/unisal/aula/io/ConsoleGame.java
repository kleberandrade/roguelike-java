package br.unisal.aula.io;

import br.unisal.aula.domain.Mundo;
import java.io.PrintWriter;
import java.util.Scanner;

public final class ConsoleGame {
    private final Scanner input;
    private final PrintWriter output;
    private final ConsoleRenderer renderer = new ConsoleRenderer();

    public ConsoleGame(Readable input, PrintWriter output) {
        this.input = new Scanner(input);
        this.output = output;
    }

    public void executar(Mundo world) {
        while (world.getJogador().vivo()) {
            output.print(renderer.render(world));
            output.print("[A/W/S/D]: ");
            output.flush();
            if (!input.hasNextLine()) { finish("Partida encerrada."); return; }
            String command = input.nextLine();
            if (command.trim().equalsIgnoreCase("Q")) { finish("Partida encerrada."); return; }
            world.atualizar(command);
        }
        finish("Fim de jogo!");
    }

    private void finish(String message) {
        output.println(message);
        output.flush();
    }
}
