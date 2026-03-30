package domuscontrol.model.dispositivos;

import java.time.LocalDateTime;

/**
 * Coluna de som inteligente com controlo de volume e canal/estação.
 */
public class ColunaSom extends Dispositivo {

    private int volume;   // 0 a 100
    private String canal; // nome do canal ou estação

    public ColunaSom() {
        super();
        this.volume = 0;
        this.canal = "";
    }

    public ColunaSom(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.volume = 0;
        this.canal = "";
    }

    public ColunaSom(String id, String marca, String modelo, double consumoWh,
                     int volume, String canal) {
        super(id, marca, modelo, consumoWh);
        this.setVolumeSemTempo(volume);
        this.canal = canal;
    }

    public ColunaSom(ColunaSom outra) {
        super(outra);
        this.volume = outra.getVolume();
        this.canal = outra.getCanal();
    }

    // ==================== Getters e Setters ====================

    public int getVolume() {
        return this.volume;
    }

    /**
     * Altera o volume. Recebe o tempo porque o volume afecta o consumo.
     */
    public void setVolume(int volume, LocalDateTime agora) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume deve estar entre 0 e 100.");
        }
        if (isLigado()) {
            acumularConsumo(agora);
        }
        this.volume = volume;
    }

    private void setVolumeSemTempo(int volume) {
        if (volume < 0 || volume > 100) {
            throw new IllegalArgumentException("Volume deve estar entre 0 e 100.");
        }
        this.volume = volume;
    }

    public String getCanal() {
        return this.canal;
    }

    /**
     * Mudar o canal não afecta o consumo — não precisa de tempo.
     */
    public void setCanal(String canal) {
        this.canal = canal;
    }

    // ==================== Métodos abstractos implementados ====================

    @Override
    public double getConsumoAtual() {
        if (!isLigado()) return 0.0;
        double base = getConsumoWh() * 0.3;
        double variavel = getConsumoWh() * 0.7 * (this.volume / 100.0);
        return base + variavel;
    }

    @Override
    public String estadoEspecifico() {
        String info = "Volume: " + this.volume + "%";
        if (this.canal != null && !this.canal.isEmpty()) {
            info += " | Canal: " + this.canal;
        }
        return info;
    }

    @Override
    public Dispositivo clone() {
        return new ColunaSom(this);
    }
}
