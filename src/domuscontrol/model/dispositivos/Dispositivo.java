package domuscontrol.model.dispositivos;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Classe abstracta que representa um dispositivo genérico no sistema DomusControl.
 * Todos os dispositivos concretos herdam desta classe.
 */
public abstract class Dispositivo implements Serializable, Cloneable {

    private String id;
    private String marca;
    private String modelo;
    private double consumoWh;            
    private boolean ligado;
    private long tempoTotalLigado;       // tempo total ligado em minutos
    private int numActivacoes;           // vezes que foi ligado
    private LocalDateTime instanteUltimaAtualizacao; // referência temporal para contabilizar consumo
    private double consumoAcumuladoWh;   // energia total gasta desde sempre

    // ==================== Construtores ====================

    public Dispositivo() {
        this.id = "";
        this.marca = "";
        this.modelo = "";
        this.consumoWh = 0.0;
        this.ligado = false;
        this.tempoTotalLigado = 0;
        this.numActivacoes = 0;
        this.instanteUltimaAtualizacao = null;
        this.consumoAcumuladoWh = 0.0;
    }

    public Dispositivo(String id, String marca, String modelo, double consumoWh) {
        this.id = id;
        this.marca = marca;
        this.modelo = modelo;
        this.consumoWh = consumoWh;
        this.ligado = false;
        this.tempoTotalLigado = 0;
        this.numActivacoes = 0;
        this.instanteUltimaAtualizacao = null;
        this.consumoAcumuladoWh = 0.0;
    }

    public Dispositivo(Dispositivo outro) {
        this.id = outro.getId();
        this.marca = outro.getMarca();
        this.modelo = outro.getModelo();
        this.consumoWh = outro.getConsumoWh();
        this.ligado = outro.isLigado();
        this.tempoTotalLigado = outro.getTempoTotalLigado();
        this.numActivacoes = outro.getNumActivacoes();
        this.instanteUltimaAtualizacao = outro.getInstanteUltimaAtualizacao();
        this.consumoAcumuladoWh = outro.getConsumoAcumuladoWh();
    }

    // ==================== Getters ====================

    public String getId() { return this.id; }
    public String getMarca() { return this.marca; }
    public String getModelo() { return this.modelo; }
    public double getConsumoWh() { return this.consumoWh; }
    public boolean isLigado() { return this.ligado; }
    public long getTempoTotalLigado() { return this.tempoTotalLigado; }
    public int getNumActivacoes() { return this.numActivacoes; }
    public LocalDateTime getInstanteUltimaAtualizacao() { return this.instanteUltimaAtualizacao; }
    public double getConsumoAcumuladoWh() { return this.consumoAcumuladoWh; }

    // ==================== Setters ====================

    public void setId(String id) { this.id = id; }
    public void setMarca(String marca) { this.marca = marca; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public void setConsumoWh(double consumoWh) {
        if (consumoWh < 0) throw new IllegalArgumentException("Consumo não pode ser negativo.");
        this.consumoWh = consumoWh;
    }

    // ==================== Gestão de tempo e energia ====================

    /**
     * Liga o dispositivo.
     */
    public void ligar(LocalDateTime agora) {
        if (!this.ligado) {
            this.ligado = true;
            this.instanteUltimaAtualizacao = agora;
            this.numActivacoes++;
        }
    }

    /**
     * Desliga o dispositivo. Acumula o consumo e tempo do período actual.
     */
    public void desligar(LocalDateTime agora) {
        if (this.ligado) {
            acumularConsumo(agora);
            this.ligado = false;
            this.instanteUltimaAtualizacao = null;
        }
    }

    /**
     * Actualiza o tempo e consumo de um dispositivo que continua ligado.
     * Chamado quando o simulador avança o tempo.
     */
    public void actualizarTempo(LocalDateTime agora) {
        if (this.ligado && this.instanteUltimaAtualizacao != null) {
            acumularConsumo(agora);
        }
    }

    /**
     * Acumula o consumo energético do período entre a última actualização e agora.
     * Usa o getConsumoAtual() para obter o consumo real (que depende do estado).
     * Deve ser chamado antes de alterar qualquer propriedade que afecte o consumo.
     * Protegido para que as subclasses possam chamar nos seus setters.
     */
    protected void acumularConsumo(LocalDateTime agora) {
        if (this.instanteUltimaAtualizacao != null) {
            long minutos = Duration.between(this.instanteUltimaAtualizacao, agora).toMinutes();
            if (minutos > 0) {
                double horas = minutos / 60.0;
                this.consumoAcumuladoWh += getConsumoAtual() * horas;
                this.tempoTotalLigado += minutos;
            }
            this.instanteUltimaAtualizacao = agora;
        }
    }

    /**
     * Retorna o consumo total acumulado em kWh.
     * Usado para estatísticas.talvez tenha que somar o consumo do período actual se estiver ligado.
     */
    public double consumoTotalKWh() {
        return this.consumoAcumuladoWh / 1000.0;
    }

    // ==================== Métodos abstractos ====================

    /**
     * Retorna o consumo REAL por hora neste momento (em Wh).
     * Depende do estado actual do dispositivo.
     * Ex: lâmpada a 50% retorna metade do consumo nominal.
     */
    public abstract double getConsumoAtual();

    /**
     * Retorna uma descrição do estado específico do dispositivo.
     * Ex: "Intensidade: 80%" para uma lâmpada.
     */
    public abstract String estadoEspecifico();

    /**
     * Cria uma cópia profunda do dispositivo.
     */
    public abstract Dispositivo clone();

    // ==================== equals, hashCode, toString ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Dispositivo that = (Dispositivo) o;
        return this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName());
        sb.append(" | ID: ").append(this.id);
        sb.append(" | ").append(this.marca).append(" ").append(this.modelo);
        sb.append(" | ").append(this.ligado ? "LIGADO" : "DESLIGADO");
        sb.append(" | ").append(this.estadoEspecifico());
        sb.append(" | Consumo actual: ").append(String.format("%.1f", getConsumoAtual())).append(" Wh");
        sb.append(" | Total: ").append(String.format("%.4f", consumoTotalKWh())).append(" kWh");
        sb.append(" | Activações: ").append(this.numActivacoes);
        sb.append(" | Tempo ligado: ").append(this.tempoTotalLigado).append(" min");
        return sb.toString();
    }
}
