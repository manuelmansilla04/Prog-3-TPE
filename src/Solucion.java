import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solucion {
    // Mapa que asocia cada ID de camión con la lista de paquetes asignados
    private Map<Integer, List<Paquete>> asignacion;
    private int pesoNoAsignado;
    private long costoMetrica;

    public Solucion() {
        this.asignacion = new HashMap<>();
        this.pesoNoAsignado = 0;
        this.costoMetrica = 0;
    }

    public Map<Integer, List<Paquete>> getAsignacion() {
        return asignacion;
    }

    public void setAsignacion(Map<Integer, List<Paquete>> asignacion) {
        this.asignacion = asignacion;
    }

    public int getPesoNoAsignado() {
        return pesoNoAsignado;
    }

    public void setPesoNoAsignado(int pesoNoAsignado) {
        this.pesoNoAsignado = pesoNoAsignado;
    }

    public long getCostoMetrica() {
        return costoMetrica;
    }

    public void setCostoMetrica(long costoMetrica) {
        this.costoMetrica = costoMetrica;
    }

    public void imprimirResultado(String tecnica) {
        System.out.println(tecnica);
        System.out.println("Solución obtenida:");
        for (Map.Entry<Integer, List<Paquete>> entrada : asignacion.entrySet()) {
            System.out.print("  Camión ID " + entrada.getKey() + " -> Paquetes: ");
            for (Paquete p : entrada.getValue()) {
                System.out.print("[" + p.getCodigo() + " (" + p.getPesoKg() + "kg)] ");
            }
            System.out.println();
        }
        System.out.println("Peso no asignado: " + pesoNoAsignado + " kg.");
        if (tecnica.equalsIgnoreCase("Backtracking")) {
            System.out.println("Métrica para analizar el costo de la solución (cantidad de estados generados): " + costoMetrica);
        } else {
            System.out.println("Métrica para analizar el costo de la solución (cantidad de candidatos considerados): " + costoMetrica);
        }
        System.out.println("-------------------------------------------------");
    }
}
