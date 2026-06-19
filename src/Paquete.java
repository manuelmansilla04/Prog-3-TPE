public class Paquete {
    private int id;
    private String codigo;
    private int pesoKg;
    private boolean contieneAlimentos;
    private int nivelUrgencia;

    public Paquete(int id, String codigo, int pesoKg, boolean contieneAlimentos, int nivelUrgencia) {
        this.id = id;
        this.codigo = codigo;
        this.pesoKg = pesoKg;
        this.contieneAlimentos = contieneAlimentos;
        this.nivelUrgencia = nivelUrgencia;
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getPesoKg() {
        return pesoKg;
    }

    public boolean isContieneAlimentos() {
        return contieneAlimentos;
    }

    public int getNivelUrgencia() {
        return nivelUrgencia;
    }

}
