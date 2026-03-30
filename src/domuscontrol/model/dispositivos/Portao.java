package domuscontrol.model.dispositivos;

/**
 * Portão de garagem inteligente com controlo de grau de abertura.
 * 0 = totalmente fechado, 100 = totalmente aberto.
 * Consumo similar à cortina — só o motor gasta energia ao mover.
 */
public class Portao extends Dispositivo {

    private int abertura; // 0 a 100

    public Portao() {
        super();
        this.abertura = 0;
    }

    public Portao(String id, String marca, String modelo, double consumoWh) {
        super(id, marca, modelo, consumoWh);
        this.abertura = 0;
    }

    public Portao(String id, String marca, String modelo, double consumoWh, int abertura) {
        super(id, marca, modelo, consumoWh);
        this.setAbertura(abertura);
    }

    public Portao(Portao outro) {
        super(outro);
        this.abertura = outro.getAbertura();
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
        return getConsumoWh() * 0.05;
    }

    @Override
    public String estadoEspecifico() {
        if (this.abertura == 0) return "Abertura: Fechado";
        if (this.abertura == 100) return "Abertura: Aberto";
        return "Abertura: " + this.abertura + "%";
    }

    @Override
    public Portao clone() {
        return new Portao(this);
    }
}
