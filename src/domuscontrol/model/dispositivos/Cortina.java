package domuscontrol.model.dispositivos;

/**
 * Cortina inteligente com controlo de grau de abertura.
 * 0 = totalmente fechada, 100 = totalmente aberta.
 * O consumo é mínimo — a cortina só gasta energia enquanto o motor se move,
 * mas na simulação simplificamos com um consumo fixo de standby.
 */
public class Cortina extends Dispositivo {

    private int abertura; // 0 a 100

    public Cortina() {
        super();
        this.abertura = 0;
    }

    public Cortina(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.abertura = 0;
    }

    public Cortina(String id, String marca, String modelo, double consumoWh, int abertura) {
        super(id, marca, modelo, consumoWh);
        this.setAbertura(abertura);
    }

    public Cortina(Cortina outra) {
        super(outra);
        this.abertura = outra.getAbertura();
    }

    // ==================== Getters e Setters ====================

    public int getAbertura() {
        return this.abertura;
    }

    /**
     * Mudar a abertura não afecta o consumo contínuo — não precisa de tempo.
     */
    public void setAbertura(int abertura) {
        if (abertura < 0 || abertura > 100) {
            throw new IllegalArgumentException("Abertura deve estar entre 0 e 100.");
        }
        this.abertura = abertura;
    }

    public void abrir() {
        this.abertura = 100;
    }

    public void fechar() {
        this.abertura = 0;
    }

    // ==================== Métodos abstractos implementados ====================

    @Override
    public double getConsumoAtual() {
        if (!isLigado()) return 0.0;
        return getConsumoWh() * 0.05; // standby mínimo
    }

    @Override
    public String estadoEspecifico() {
        if (this.abertura == 0) return "Abertura: Fechada";
        if (this.abertura == 100) return "Abertura: Aberta";
        return "Abertura: " + this.abertura + "%";
    }

    @Override
    public Dispositivo clone() {
        return new Cortina(this);
    }
}
