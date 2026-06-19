import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Servicios {
    private List<Camion> listaCamiones;
    private List<Paquete> listaPaquetes;

    private Map<String, Paquete> paquetesPorCodigo;
    private List<Paquete> paquetesConAlimentos;
    private List<Paquete> paquetesSinAlimentos;
    private Map<Integer, List<Paquete>> paquetesPorUrgencia;

    //Variables globales auxiliares para el Backtracking
    private Map<Integer, List<Paquete>> mejorAsignacion;
    private int menorPesoNoAsignado;
    private long estadosGenerados;

    /*
    Complejidad Temporal: O(C+P). C es la cantidad de líneas en el archivo de Camiones y P la cantidad en Paquetes.
    Esto porque se leen ambos archivos de manera secuencial en una única pasada (O(C) y O(P)).
    Las inserciones en las listas y los HashMaps asociados toman tiempo constante O(1) promedio.
    */
    public Servicios(String pathCamiones, String pathPaquetes) {
        this.listaCamiones = new ArrayList<>();
        this.listaPaquetes = new ArrayList<>();

        this.paquetesPorCodigo = new HashMap<>();
        this.paquetesConAlimentos = new ArrayList<>();
        this.paquetesSinAlimentos = new ArrayList<>();
        this.paquetesPorUrgencia = new HashMap<>();

        cargarCamiones(pathCamiones);
        cargarPaquetes(pathPaquetes);
    }

    private void cargarCamiones(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea = br.readLine();
            if (linea == null) {
                return;
            }
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                String[] datos = linea.split(";");
                int id = Integer.parseInt(datos[0].trim());
                String patente = datos[1].trim();
                // En el ejemplo viene como 1 o 0
                boolean estaRefrigerado = datos[2].trim().equals("1");
                int capacidadKg = Integer.parseInt(datos[3].trim());
                this.listaCamiones.add(new Camion(id, patente, estaRefrigerado, capacidadKg));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al procesar el archivo de camiones: " + e.getMessage());
        }
    }

    public List<Camion> getListaCamiones() {
        return this.listaCamiones;
    }

    private void cargarPaquetes(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String linea = br.readLine();
            if (linea == null) {
                return;
            }

            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }

                String[] datos = linea.split(";");
                int id = Integer.parseInt(datos[0].trim());
                String codigo = datos[1].trim();
                int pesoKg = Integer.parseInt(datos[2].trim());
                boolean contieneAlimentos = datos[3].trim().equals("1");
                int nivelUrgencia = Integer.parseInt(datos[4].trim());

                Paquete paquete = new Paquete(id, codigo, pesoKg, contieneAlimentos, nivelUrgencia);
                this.listaPaquetes.add(paquete);

                //Servicio 1
                this.paquetesPorCodigo.put(codigo, paquete);

                //Servicio 2
                if (contieneAlimentos) {
                    this.paquetesConAlimentos.add(paquete);
                } else {
                    this.paquetesSinAlimentos.add(paquete);
                }

                //Servicio 3
                this.paquetesPorUrgencia.putIfAbsent(nivelUrgencia, new ArrayList<>());
                this.paquetesPorUrgencia.get(nivelUrgencia).add(paquete);
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al procesar el archivo de paquetes: " + e.getMessage());
        }
    }

    public List<Paquete> getListaPaquetes() {
        return this.listaPaquetes;
    }

    /*
    Complejidad Temporal: O(1) en promedio pues la recuperación de un objeto desde un HashMap a través de su clave
    (código de paquete) se resuelve en tiempo constante mediante su función Hash. Es importante notar que esto es en
    promedio; excepcionalmente con las claves colisionando se llegaría a O(P) con P correspondiendo a la cantidad de
    los paquetes presentados.
    */
    public Paquete servicio1(String codigoPaquete) {
        return this.paquetesPorCodigo.get(codigoPaquete);
    }

    /*
    Complejidad Temporal: O(1). Esto debido a que retorna de forma directa la referencia a la lista correspondiente que
    ya fue previamente filtrada y construida durante la etapa de carga.
    */
    public List<Paquete> servicio2(boolean contieneAlimentos) {
        if (contieneAlimentos) {
            return this.paquetesConAlimentos;
        } else {
            return this.paquetesSinAlimentos;
        }
    }

    /*
    Complejidad Temporal: O(R+K). R es el rango de urgencia querido (max-min+1) y K son los paquetes devueltos.
    Esto pues solo iteramos por las claves del mapa que están dentro del rango y volcamos sus listas directamente,
    evitando recorrer el total de paquetes (P) del sistema.
    */
    public List<Paquete> servicio3(int urgenciaMinima, int urgenciaMaxima) {
        List<Paquete> resultado = new ArrayList<>();
        for (int u = urgenciaMinima; u <= urgenciaMaxima; u++) {
            List<Paquete> paquetesNivel = this.paquetesPorUrgencia.get(u);
            if (paquetesNivel != null) {
                resultado.addAll(paquetesNivel);
            }
        }
        return resultado;
    }

    /*
    ESTRATEGIA 1: BACKTRACKING
    Se explora el espacio de soluciones de forma exhaustiva mediante un árbol de decisión  adaptado del problema de la
    Mochila (Knapsack) multidimensional. Para cada paquete, se evalúan recursivamente todas las opciones posibles: no
    asignarlo a ningún camión, o intentar asignarlo a cada uno de los camiones disponibles que cumplan las restricciones
    de peso y refrigeración. Se aplica una poda elemental controlando si la solución parcial actual ya es peor que la
    mejor solución global encontrada hasta el momento.
    Complejidad: O(C^P) siendo C los camiones y P los paquetes.
    */
    public Solucion backtracking() {
        this.mejorAsignacion = new HashMap<>();
        this.estadosGenerados = 0;
        Map<Integer, List<Paquete>> asignacionActual = new HashMap<>();
        int[] cargasActualesCamiones = new int[listaCamiones.size()];

        int pesoTotalInicial = 0;
        for (Paquete p : listaPaquetes) {
            pesoTotalInicial += p.getPesoKg();
        }
        this.menorPesoNoAsignado = pesoTotalInicial;

        for (Camion listaCamione : listaCamiones) {
            asignacionActual.put(listaCamione.getId(), new ArrayList<>());
        }

        backtrackingPaso(0, asignacionActual, cargasActualesCamiones, pesoTotalInicial);

        Solucion sol = new Solucion();
        sol.setAsignacion(this.mejorAsignacion);
        sol.setPesoNoAsignado(this.menorPesoNoAsignado);
        sol.setCostoMetrica(this.estadosGenerados);
        return sol;
    }

    private void backtrackingPaso(int indicePaquete, Map<Integer, List<Paquete>> asignacionActual, int[] cargasCamiones, int pesoNoAsignadoActual) {
        this.estadosGenerados++;

        if (pesoNoAsignadoActual > this.menorPesoNoAsignado) {
            return;
        }

        if (indicePaquete == listaPaquetes.size()) {
            this.menorPesoNoAsignado = pesoNoAsignadoActual;
            this.mejorAsignacion = copia(asignacionActual);
            return;
        }

        Paquete paquete = listaPaquetes.get(indicePaquete);

        //Intento de ubicar el paquete en algún camión
        for (int i = 0; i < listaCamiones.size(); i++) {
            Camion camion = listaCamiones.get(i);

            if (paquete.isContieneAlimentos() && !camion.isRefrigerado()) {
                continue;
            }

            if (cargasCamiones[i] + paquete.getPesoKg() <= camion.getCapacidadKg()) {
                cargasCamiones[i] += paquete.getPesoKg();
                asignacionActual.get(camion.getId()).add(paquete);

                backtrackingPaso(indicePaquete + 1, asignacionActual, cargasCamiones, pesoNoAsignadoActual - paquete.getPesoKg());

                cargasCamiones[i] -= paquete.getPesoKg();
                asignacionActual.get(camion.getId()).removeLast();
            }
        }
        //La otra opcion es no asignar este paquete a ningún camión
        backtrackingPaso(indicePaquete + 1, asignacionActual, cargasCamiones, pesoNoAsignadoActual);
    }

    /*
    ESTRATEGIA 2: GREEDY
    Dado que el objetivo es minimizar el peso no asignado, el algoritmo ordena prioritariamente los paquetes de mayor a
    menor peso. Para cada paquete (candidato), busca el primer camión que disponga de espacio suficiente y cumpla con la
    condición de refrigeración si el paquete lleva alimentos. Si encuentra un camión apto, lo asigna definitivamente,
    actualizando la carga del camión de inmediato.
    Complejidad: O(P log P + P*C) siendo C los camiones y P los paquetes.
    */
    public Solucion greedy() {
        long candidatosConsiderados = 0;
        Map<Integer, List<Paquete>> asignacionGreedy = new HashMap<>();
        int[] cargasCamiones = new int[listaCamiones.size()];
        List<Paquete> candidatos = new ArrayList<>(listaPaquetes);

        for (Camion c : listaCamiones) {
            asignacionGreedy.put(c.getId(), new ArrayList<>());
        }

        candidatos.sort((p1, p2) -> Integer.compare(p2.getPesoKg(), p1.getPesoKg()));
        int pesoNoAsignadoTotal = 0;
        for (Paquete paquete : candidatos) {
            candidatosConsiderados++;
            boolean asignado = false;

            for (int i = 0; i < listaCamiones.size(); i++) {
                Camion camion = listaCamiones.get(i);
                if (paquete.isContieneAlimentos() && !camion.isRefrigerado()) {
                    continue;
                }

                if (cargasCamiones[i] + paquete.getPesoKg() <= camion.getCapacidadKg()) {
                    cargasCamiones[i] += paquete.getPesoKg();
                    asignacionGreedy.get(camion.getId()).add(paquete);
                    asignado = true;
                    break;
                }
            }

            if (!asignado) {
                pesoNoAsignadoTotal += paquete.getPesoKg();
            }
        }

        Solucion sol = new Solucion();
        sol.setAsignacion(asignacionGreedy);
        sol.setPesoNoAsignado(pesoNoAsignadoTotal);
        sol.setCostoMetrica(candidatosConsiderados);
        return sol;
    }

    private Map<Integer, List<Paquete>> copia(Map<Integer, List<Paquete>> original) {
        Map<Integer, List<Paquete>> copia = new HashMap<>();
        for (Map.Entry<Integer, List<Paquete>> e : original.entrySet()) {
            copia.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
        return copia;
    }
}

