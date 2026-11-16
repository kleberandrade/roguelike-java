package br.unisal.aula;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import br.unisal.aula.io.ConsoleGame;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsoleTest {
    private Mundo world() {
        Mundo world = new MundoBuilder(3, 3, new Random(1)).preencher('#', true)
                .criarCaminho(1, 1, 0).criarCriaturas(1).build();
        world.setJogador(new Jogador(3, new Ponto2D(1, 1), 'J'));
        return world;
    }
    private String play(Mundo world, String commands) {
        StringWriter output = new StringWriter();
        new ConsoleGame(new StringReader(commands), new PrintWriter(output)).executar(world);
        return output.toString();
    }
    @Test void rendersAsciiAndQuitDoesNotConsumeATurn() {
        Mundo world = world();
        String text = play(world, " q \n");
        assertTrue(text.contains("###\n#J#\n###\n"));
        assertTrue(text.contains("[A/W/S/D]: "));
        assertTrue(text.contains("Partida encerrada."));
        assertEquals(0, world.getJogador().getPassos());
    }
    @Test void eofExitsCleanlyWithoutAFreeZombieTurn() {
        Mundo world = world();
        assertTrue(play(world, "").contains("Partida encerrada."));
        assertEquals(3, world.getJogador().getVidas());
    }
    @Test void threeWaitingTurnsEndInDefeatAndIgnoreRemainingInput() {
        Mundo world = world();
        String text = play(world, "\n\n\nD\n");
        assertTrue(text.contains("Fim de jogo!"));
        assertEquals(3, world.getJogador().getPassos());
        assertFalse(world.getJogador().vivo());
    }
    @Test void helpAndInvalidSeedDoNotStartAGame() {
        StringWriter output = new StringWriter();
        assertEquals(0, Main.run(new String[]{"--help"}, new StringReader(""), new PrintWriter(output)));
        assertTrue(output.toString().contains("--seed"));
        assertEquals(2, Main.run(new String[]{"--seed", "invalid"}, new StringReader(""), new PrintWriter(output)));
        assertEquals(2, Main.run(new String[]{"--unknown"}, new StringReader(""), new PrintWriter(output)));
    }
    @Test void cliSeedReplaysTheSameSession() {
        StringWriter a = new StringWriter(), b = new StringWriter();
        assertEquals(0, Main.run(new String[]{"--seed", "42"}, new StringReader("d\nw\nq\n"), new PrintWriter(a)));
        assertEquals(0, Main.run(new String[]{"--seed", "42"}, new StringReader("d\nw\nq\n"), new PrintWriter(b)));
        assertEquals(a.toString(), b.toString());
    }
}
