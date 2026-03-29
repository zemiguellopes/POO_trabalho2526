package domuscontrol.model.dispositivos;

import java.time.LocalDateTime;

/**
 * Lâmpada inteligente com controlo de intensidade (dimmer).
 * A intensidade varia entre 0 e 100 (percentagem).
 */
public class Lampada extends Dispositivo {

    private int intensidade; // 0 a 100

    public Lampada() {
        super();
        this.intensidade = 100;
    }

    public Lampada(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.intensidade = 100;
    }

    public Lampada(String id, String marca, String modelo, double consumoWh, int intensidade) {
        super(id, marca, modelo, consumoWh);
        this.setIntensidadeSemTempo(intensidade);
    }

    public Lampada(Lampada outra) {
        super(outra);
        this.intensidade = outra.getIntensidade();
    }

    // ==================== Getters e Setters ====================

    public int getIntensidade() {
        return this.intensidade;
    }

    /**
     * Altera a intensidade. Recebe o tempo actual para acumular o consumo
     * do período anterior antes de mudar.
     */
    public void setIntensidade(int intensidade, LocalDateTime agora) {
        if (intensidade < 0 || intensidade > 100) {
            throw new IllegalArgumentException("Intensidade deve estar entre 0 e 100.");
        }
        if (isLigado()) {
            acumularConsumo(agora);
        }
        this.intensidade = intensidade;
    }

    /**
     * Setter sem tempo — usado apenas na criação do objecto (construtores).
     * Não deve ser usado durante a simulação.
     */
    private void setIntensidadeSemTempo(int intensidade) {
        if (intensidade < 0 || intensidade > 100) {
            throw new IllegalArgumentException("Intensidade deve estar entre 0 e 100.");
        }
        this.intensidade = intensidade;
    }

    // ==================== Métodos abstractos implementados ====================

    @Override
    public double getConsumoAtual() {
        if (!isLigado()) return 0.0;
        return getConsumoWh() * (this.intensidade / 100.0);
    }

    @Override
    public String estadoEspecifico() {
        return "Intensidade: " + this.intensidade + "%";
    }

    @Override
    public Dispositivo clone() {
        return new Lampada(this);
    }
}
