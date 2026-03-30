package domuscontrol.model.dispositivos;

/**
 * Lâmpada com controlo de temperatura de cor.
 * Herda a intensidade da Lampada e acrescenta a temperatura de cor (Kelvin).
 * 2700K = luz quente/amarela, 4000K = luz fria/branca.
 */
public class LampadaCor extends Lampada {

    private int temperaturaK; // 2700 a 4000

    public LampadaCor() {
        super();
        this.temperaturaK = 2700;
    }

    public LampadaCor(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.temperaturaK = 2700;
    }

    public LampadaCor(String id, String marca, String modelo, double consumoWh,
                       int intensidade, int temperaturaK) {
        super(id, marca, modelo, consumoWh, intensidade);
        this.setTemperaturaK(temperaturaK);
    }

    public LampadaCor(LampadaCor outra) {
        super(outra);
        this.temperaturaK = outra.getTemperaturaK();
    }

    // ==================== Getters e Setters ====================

    public int getTemperaturaK() {
        return this.temperaturaK;
    }

    public void setTemperaturaK(int temperaturaK) {
        if (temperaturaK < 2700 || temperaturaK > 4000) {
            throw new IllegalArgumentException("Temperatura de cor deve estar entre 2700K e 4000K.");
        }
        this.temperaturaK = temperaturaK;
    }

    // ==================== Métodos ====================

    public String descricaoTemperatura() {
        if (this.temperaturaK <= 3000) return "Quente";
        if (this.temperaturaK <= 3500) return "Neutro";
        return "Frio";
    }

    @Override
    public String estadoEspecifico() {
        return super.estadoEspecifico() + " | Cor: " + this.temperaturaK + "K (" + descricaoTemperatura() + ")";
    }

    @Override
    public Dispositivo clone() {
        return new LampadaCor(this);
    }
}
