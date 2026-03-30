package domuscontrol.model.dispositivos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Fechadura inteligente com código PIN, registo de acessos e bloqueio automático.
 * Permite trancar/destrancar, mudar o PIN e consultar o histórico de quem abriu e quando.
 */
public class FechaduraInteligente extends Dispositivo {

    private boolean trancada;
    private String codigoPin;
    private int tentativasFalhadas;
    private boolean bloqueada;           // bloqueia após 3 tentativas falhadas
    private List<String> historicoAcessos; // registo de acessos

    private static final int MAX_TENTATIVAS = 3;

    public FechaduraInteligente() {
        super();
        this.trancada = true;
        this.codigoPin = "0000";
        this.tentativasFalhadas = 0;
        this.bloqueada = false;
        this.historicoAcessos = new ArrayList<>();
    }

    public FechaduraInteligente(String id, String marca, String modelo, double consumoWh,
                                 String codigoPin) {
        super(id, marca, modelo, consumoWh);
        this.trancada = true;
        this.setCodigoPinSemValidacao(codigoPin);
        this.tentativasFalhadas = 0;
        this.bloqueada = false;
        this.historicoAcessos = new ArrayList<>();
    }

    public FechaduraInteligente(FechaduraInteligente outra) {
        super(outra);
        this.trancada = outra.isTrancada();
        this.codigoPin = outra.getCodigoPin();
        this.tentativasFalhadas = outra.getTentativasFalhadas();
        this.bloqueada = outra.isBloqueada();
        this.historicoAcessos = new ArrayList<>(outra.getHistoricoAcessos());
    }

    // ==================== Getters ====================

    public boolean isTrancada() {
        return this.trancada;
    }

    public String getCodigoPin() {
        return this.codigoPin;
    }

    public int getTentativasFalhadas() {
        return this.tentativasFalhadas;
    }

    public boolean isBloqueada() {
        return this.bloqueada;
    }

    /**
     * Retorna uma copia do histórico de acessos.
     */
    public List<String> getHistoricoAcessos() {
        return new ArrayList<>(this.historicoAcessos);
    }

    // ==================== Operações ====================

    /**
     * Tenta destrancar a fechadura com um código PIN.
     * Se o código estiver errado, conta como tentativa falhada.
     * Após 3 tentativas falhadas, a fechadura bloqueia.
     *
     * @return true se destrancou com sucesso
     */
    public boolean destrancar(String pin, String utilizadorId, LocalDateTime agora) {
        if (!isLigado()) {
            return false;
        }

        if (this.bloqueada) {
            registarAcesso("BLOQUEADA — tentativa de " + utilizadorId, agora);
            return false;
        }

        if (!this.trancada) {
            return true; // já está destrancada
        }

        if (this.codigoPin.equals(pin)) {
            this.trancada = false;
            this.tentativasFalhadas = 0;
            registarAcesso("Destrancada por " + utilizadorId, agora);
            return true;
        } else {
            this.tentativasFalhadas++;
            registarAcesso("PIN errado — tentativa " + this.tentativasFalhadas + " de " + utilizadorId, agora);
            if (this.tentativasFalhadas >= MAX_TENTATIVAS) {
                this.bloqueada = true;
                registarAcesso("BLOQUEIO ACTIVADO após " + MAX_TENTATIVAS + " tentativas", agora);
            }
            return false;
        }
    }

    /**
     * Tranca a fechadura. Não precisa de PIN.
     */
    public void trancar(String utilizadorId, LocalDateTime agora) {
        if (!isLigado()) {
            return;
        }

        if (!this.trancada) {
            this.trancada = true;
            registarAcesso("Trancada por " + utilizadorId, agora);
        }
    }

    /**
     * Desbloqueia a fechadura (reset de segurança). Só deve ser feito pelo admin.
     */
    public void desbloquear(String utilizadorId, LocalDateTime agora) {
        if (!isLigado()) {
            return;
        }

        if (this.bloqueada) {
            this.bloqueada = false;
            this.tentativasFalhadas = 0;
            registarAcesso("Desbloqueada (reset) por " + utilizadorId, agora);
        }
    }

    /**
     * Altera o código PIN. Exige o PIN actual para confirmar.
     *
     * @return true se o PIN foi alterado com sucesso
     */
    public boolean alterarPin(String pinActual, String pinNovo, String utilizadorId,
                               LocalDateTime agora) {
        if (!isLigado()) {
            return false;
        }

        if (!this.codigoPin.equals(pinActual)) {
            registarAcesso("Tentativa de alterar PIN falhada por " + utilizadorId, agora);
            return false;
        }
        if (pinNovo == null || pinNovo.length() < 4) {
            throw new IllegalArgumentException("PIN deve ter pelo menos 4 caracteres.");
        }
        this.codigoPin = pinNovo;
        registarAcesso("PIN alterado por " + utilizadorId, agora);
        return true;
    }

    /**
     * Retorna os últimos N acessos.
     */
    public List<String> ultimosAcessos(int n) {
        int inicio = Math.max(0, this.historicoAcessos.size() - n);
        return new ArrayList<>(this.historicoAcessos.subList(inicio, this.historicoAcessos.size()));
    }

    // ==================== Auxiliar ====================

    private void registarAcesso(String descricao, LocalDateTime agora) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String registo = "[" + agora.format(fmt) + "] " + descricao;
        this.historicoAcessos.add(registo);
    }

    private void setCodigoPinSemValidacao(String pin) {
        if (pin == null || pin.length() < 4) {
            throw new IllegalArgumentException("PIN deve ter pelo menos 4 caracteres.");
        }
        this.codigoPin = pin;
    }

    // ==================== Métodos abstractos implementados ====================

    @Override
    public double getConsumoAtual() {
        if (!isLigado()) return 0.0;
        return getConsumoWh(); // consumo fixo de standby
    }

    @Override
    public String estadoEspecifico() {
        String estado = this.trancada ? "Trancada" : "Destrancada";
        if (this.bloqueada) estado += " | BLOQUEADA";
        estado += " | Acessos: " + this.historicoAcessos.size();
        return estado;
    }

    @Override
    public Dispositivo clone() {
        return new FechaduraInteligente(this);
    }
}
