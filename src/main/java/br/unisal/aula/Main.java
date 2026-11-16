package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import br.unisal.aula.io.ConsoleGame;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int status = run(args, new InputStreamReader(System.in, StandardCharsets.UTF_8), new PrintWriter(System.out, true));
        if (status != 0) System.exit(status);
    }

    static int run(String[] args, Readable input, PrintWriter output) {
        if (args.length == 1 && args[0].equals("--help")) { usage(output); return 0; }
        Random random;
        try {
            random = parseRandom(args);
        } catch (IllegalArgumentException e) {
            output.println("Invalid arguments: " + e.getMessage());
            usage(output);
            return 2;
        }
        new ConsoleGame(input, output).executar(new Jogo(100, 30, random).criarMundo());
        return 0;
    }

    private static Random parseRandom(String[] args) {
        if (args.length == 0) return new Random();
        if (args.length == 2 && args[0].equals("--seed")) return new Random(Long.parseLong(args[1]));
        throw new IllegalArgumentException("expected --seed <long> or --help");
    }

    private static void usage(PrintWriter output) {
        output.println("Usage: java -jar target/roguelike-java-1.0.0.jar [--seed <long> | --help]");
        output.println("W/A/S/D + Enter: move. Q + Enter: quit. Other input: wait.");
        output.flush();
    }
}
