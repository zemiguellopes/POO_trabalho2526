package domuscontrol.model.dispositivos;

/**
 * Relé - o dispositivo mais simples. Apenas liga ou desliga.
 */
public class Rele extends Dispositivo {

    public Rele() {
        super();
    }

    public Rele(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
    }

    public Rele(Rele outro) {
        super(outro);
    }

   @Override
public double getConsumoAtual() {
    if (!isLigado()) return 0.0;
    return getConsumoWh();
}

    @Override
    public String estadoEspecifico() {
        return "Relé simples";
    }

    @Override
    public Dispositivo clone() {
        return new Rele(this);
    }
}