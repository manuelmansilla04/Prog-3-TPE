import java.util.List;

public class Main {
    public static void main(String[] args) {

        Servicios servicios = new Servicios("Camiones.csv", "Paquetes.csv");

        // ------------------------------------------------------------------

        //PRIMERA PARTE: Servicios de búsqueda y consulta

        // Servicio 1: Buscar paquete por código
        System.out.println("--- Servicio 1: Búsqueda por código ---");
        imprimirPaquete("P002", servicios.servicio1("P002"));
        imprimirPaquete("P999", servicios.servicio1("P999"));

        // Servicio 2: Filtrar paquetes por contenido de alimentos
        System.out.println("\n--- Servicio 2: Filtrado por alimentos ---");
        System.out.println("Con alimentos:");
        for (Paquete p : servicios.servicio2(true)) {
            System.out.println("  " + p.getCodigo() + " | " + p.getPesoKg() + "kg");
        }
        System.out.println("Sin alimentos:");
        for (Paquete p : servicios.servicio2(false)) {
            System.out.println("  " + p.getCodigo() + " | " + p.getPesoKg() + "kg");
        }

        // Servicio 3: Buscar paquetes en un rango de urgencia
        System.out.println("\n--- Servicio 3: Rango de urgencia [10, 85] ---");
        List<Paquete> rangoUrgencias = servicios.servicio3(10, 85);
        if (rangoUrgencias.isEmpty()) {
            System.out.println("  No se encontraron paquetes en ese rango.");
        } else {
            for (Paquete p : rangoUrgencias) {
                System.out.println("  " + p.getCodigo() + " | Urgencia: " + p.getNivelUrgencia());
            }
        }

        // ------------------------------------------------------------------

        // SEGUNDA PARTE: Compulsa de técnicas algorítmicas

        System.out.println("\n--- Segunda Parte: Asignación de paquetes a camiones ---\n");

        Solucion solBacktracking = servicios.backtracking();
        solBacktracking.imprimirResultado("Backtracking");

        Solucion solGreedy = servicios.greedy();
        solGreedy.imprimirResultado("Greedy");
    }

    private static void imprimirPaquete(String codigo, Paquete p) {
        if (p != null) {
            System.out.println("  [OK] " + codigo + " -> Peso: " + p.getPesoKg() + "kg | Alimentos: " + (p.isContieneAlimentos() ? "Sí" : "No") + " | Urgencia: " + p.getNivelUrgencia());
        } else {
            System.out.println("  [NO ENCONTRADO] " + codigo);
        }
    }
}