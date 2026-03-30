package domuscontrol.model.dispositivos;

/**
 * Sensor de pluviosidade. Mede a quantidade de chuva em mm por hora.
 * Usado como condição para automações (ex: se chove > 5mm/h, fechar cortinas).
 * Consumo fixo e muito baixo — é um sensor passivo.
 */
public class SensorPluviosidade extends Dispositivo {

    private double valorMmH; // milímetros por hora

    public SensorPluviosidade() {
        super();
        this.valorMmH = 0.0;
    }

    public SensorPluviosidade(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.valorMmH = 0.0;
    }

    public SensorPluviosidade(SensorPluviosidade outro) {
        super(outro);
        this.valorMmH = outro.getValorMmH();
    }

    // ==================== Getters e Setters ====================

    public double getValorMmH() {
        return this.valorMmH;
    }

    /**
     * Define o valor actual do sensor.
     * Na simulação, o utilizador define manualmente.
     * Não afecta o consumo — não precisa de tempo.
     */
    public void setValorMmH(double valorMmH) {
        if (valorMmH < 0) {
            throw new IllegalArgumentException("Valor de pluviosidade não pode ser negativo.");
        }
        this.valorMmH = valorMmH;
    }

    // ==================== Métodos ====================

    public String descricaoNivel() {
        if (this.valorMmH == 0) return "Sem chuva";
        if (this.valorMmH <= 2.5) return "Chuva fraca";
        if (this.valorMmH <= 7.5) return "Chuva moderada";
        return "Chuva forte";
    }

    @Override
    public double getConsumoAtual() {
        if (!isLigado()) return 0.0;
        return getConsumoWh();
    }

    @Override
    public String estadoEspecifico() {
        return "Pluviosidade: " + this.valorMmH + " mm/h (" + descricaoNivel() + ")";
    }

    @Override
    public Dispositivo clone() {
        return new SensorPluviosidade(this);
    }
}

