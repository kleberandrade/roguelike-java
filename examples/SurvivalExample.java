import br.unisal.aula.domain.Jogador;
import br.unisal.aula.domain.Mundo;
import br.unisal.aula.domain.Ponto2D;
import br.unisal.aula.generation.MundoBuilder;
import java.util.Random;

/** A tiny arena: walls keep one zombie beside the player until defeat. */
public class SurvivalExample {
    public static void main(String[] args) {
        Mundo world = new MundoBuilder(3, 3, new Random(42))
                .preencher('#', true).criarCaminho(1, 1, 0).criarCriaturas(1).build();
        world.setJogador(new Jogador(3, new Ponto2D(1, 1), 'J'));
        while (world.getJogador().vivo()) world.atualizar("");
        if (world.getJogador().getPassos() != 3) throw new AssertionError("Expected three turns");
        System.out.println("Defeat after " + world.getJogador().getPassos() + " turns.");
    }
}
