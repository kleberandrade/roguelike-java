package br.unisal.aula.io;

import br.unisal.aula.domain.*;
import br.unisal.aula.generation.MundoBuilder;

import br.unisal.aula.domain.*;

/** Renders rows without terminal escapes; the player takes precedence over zombies. */
public final class ConsoleRenderer {
    public String render(Mundo mundo) {
        char[][] rows = new char[mundo.getAltura()][mundo.getLargura()];
        for (int y = 0; y < rows.length; y++) {
            for (int x = 0; x < rows[y].length; x++) rows[y][x] = mundo.simbolo(x, y);
        }
        for (Personagem creature : mundo.getCriaturas()) place(rows, creature);
        place(rows, mundo.getJogador());
        StringBuilder result = new StringBuilder();
        for (char[] row : rows) result.append(row).append('\n');
        return result.toString();
    }

    private void place(char[][] rows, Personagem character) {
        Ponto2D p = character.getPosicao();
        rows[p.getY()][p.getX()] = character.getSimbolo();
    }
}
