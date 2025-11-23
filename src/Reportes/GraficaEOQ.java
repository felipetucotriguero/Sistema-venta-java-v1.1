package Reportes;

import Modelo.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;
import java.awt.*;

public class GraficaEOQ {
    
    // Clase interna para almacenar datos de productos EOQ
    static class DatoEOQ {
        int id;
        String nombre;
        double demandaAnual;
        double costoOrden;
        double costoMantener;
        double precioUnidad;
        
        public DatoEOQ(int id, String nombre, double demandaAnual, double costoOrden, 
                      double costoMantener, double precioUnidad) {
            this.id = id;
            this.nombre = nombre;
            this.demandaAnual = demandaAnual;
            this.costoOrden = costoOrden;
            this.costoMantener = costoMantener;
            this.precioUnidad = precioUnidad;
        }
    }
    
    public static void GraficarEOQ() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Conexion cn = new Conexion();
        
        try {
            // Consulta para obtener productos con datos EOQ
            // Valida que la demanda no sea igual a 0 porque no tiene sentido calcular
            String sql = "SELECT ie.id, p.nombre, ie.demanda_anual, ie.costo_orden, " +
            "ie.costo_mantener, ie.precio_unitario " +
            "FROM inventario_eoq ie " +
            "INNER JOIN productos p ON ie.id_producto = p.id " +
            "WHERE ie.activo = 1 AND ie.demanda_anual > 0 " +  // ← AGREGAR ESTO
            "ORDER BY p.nombre";
            
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // Lista para almacenar los productos
            List<DatoEOQ> productos = new ArrayList<>();
            
            // Leer todos los productos
            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double demandaAnual = rs.getDouble("demanda_anual");
                double costoOrden = rs.getDouble("costo_orden");
                double costoMantener = rs.getDouble("costo_mantener");
                double precioUnidad = rs.getDouble("precio_unitario");
                
                productos.add(new DatoEOQ(id, nombre, demandaAnual, costoOrden, 
                                         costoMantener, precioUnidad));
            }
            
            // Verificar si hay productos
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No hay productos EOQ registrados en la base de datos.\n" +
                        "Por favor, configure los datos EOQ primero.",
                        "Sin datos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Si hay un solo producto, mostrar directamente
            /*if (productos.size() == 1) {
                DatoEOQ producto = productos.get(0);
                mostrarGraficaProducto(producto, cn);
                return;
            }*/
            
            // Si hay múltiples productos, mostrar selector
            mostrarSelectorProductos(productos, cn);
            
        } catch (SQLException e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,
                    "Error al cargar datos EOQ:\n" + e.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar recursos
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.toString());
            }
        }
    }
    
    private static void mostrarSelectorProductos(List<DatoEOQ> productos, Conexion cn) {
    // Crear diálogo de selección
    JDialog dialogo = new JDialog();
    dialogo.setTitle("Seleccionar Producto para Análisis EOQ");
    dialogo.setSize(500, 400);
    dialogo.setLocationRelativeTo(null);
    dialogo.setModal(true);
    dialogo.setLayout(new BorderLayout(10, 10));
    
    // Panel superior con instrucciones
    JPanel panelTitulo = new JPanel();
    panelTitulo.setBackground(new Color(240, 248, 255));
    JLabel lblTitulo = new JLabel("Seleccione un producto para ver su análisis EOQ:");
    lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
    panelTitulo.add(lblTitulo);
    dialogo.add(panelTitulo, BorderLayout.NORTH);
    
    // Lista de productos
    DefaultListModel<String> modeloLista = new DefaultListModel<>();
    for (DatoEOQ producto : productos) {
        modeloLista.addElement(producto.nombre + " (Demanda: " + 
                              String.format("%.0f", producto.demandaAnual) + " unidades)");
    }
    
    JList<String> lista = new JList<>(modeloLista);
    lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    lista.setFont(new Font("Arial", Font.PLAIN, 12));
    JScrollPane scrollPane = new JScrollPane(lista);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    dialogo.add(scrollPane, BorderLayout.CENTER);
    
    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    
    JButton btnGraficar = new JButton("Graficar");
    btnGraficar.setPreferredSize(new Dimension(100, 30));
    btnGraficar.addActionListener(e -> {
        int selectedIndex = lista.getSelectedIndex();
        if (selectedIndex != -1) {
            DatoEOQ productoSeleccionado = productos.get(selectedIndex);
            // NO cerrar el diálogo aquí, solo ocultarlo
            dialogo.setVisible(false);
            mostrarGraficaProducto(productoSeleccionado, cn, dialogo); // Pasar el diálogo
        } else {
            JOptionPane.showMessageDialog(dialogo,
                    "Por favor, seleccione un producto.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
    });
    
    JButton btnCancelar = new JButton("Cancelar");
    btnCancelar.setPreferredSize(new Dimension(100, 30));
    btnCancelar.addActionListener(e -> dialogo.dispose());
    
    panelBotones.add(btnGraficar);
    panelBotones.add(btnCancelar);
    dialogo.add(panelBotones, BorderLayout.SOUTH);
    
    dialogo.setVisible(true);
}
    
    private static void mostrarGraficaProducto(DatoEOQ producto, Conexion cn, JDialog dialogoSelector) {
    // Calcular EOQ óptimo
    double eoq = Math.sqrt((2 * producto.demandaAnual * producto.costoOrden) / 
                           producto.costoMantener);
    double costoTotalMinimo = calcularCostoTotal(producto, eoq);
    
    // Actualizar EOQ en la base de datos
    actualizarEOQenBD(producto.id, eoq, costoTotalMinimo, cn);
    
    // Crear la gráfica
    JFreeChart chart = crearGrafica(producto, eoq);
    
    // Crear frame con información adicional
    JFrame frame = new JFrame();
    frame.setTitle("Análisis EOQ - " + producto.nombre);
    frame.setSize(1100, 750);
    frame.setLocationRelativeTo(null);
    frame.setLayout(new BorderLayout(10, 10));
    
    // Configurar el comportamiento al cerrar
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
            // Volver a mostrar el diálogo de selección
            if (dialogoSelector != null) {
                dialogoSelector.setVisible(true);
            }
        }
    });
    
    // Panel de información
    JPanel panelInfo = crearPanelInformacion(producto, eoq, costoTotalMinimo);
    frame.add(panelInfo, BorderLayout.NORTH);
    
    // Panel de gráfica
    org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart);
    chartPanel.setPreferredSize(new Dimension(1000, 550));
    frame.add(chartPanel, BorderLayout.CENTER);
    
    frame.setVisible(true);
}
    
    private static JPanel crearPanelInformacion(DatoEOQ producto, double eoq, double costoTotalMinimo) {
        JPanel panelPrincipal = new JPanel(new BorderLayout(5, 5));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.setBackground(new Color(240, 248, 255));
        
        // Título
        JLabel lblTitulo = new JLabel("Producto: " + producto.nombre);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);
        
        // Panel de datos
        JPanel panelDatos = new JPanel(new GridLayout(3, 2, 10, 5));
        panelDatos.setBackground(new Color(240, 248, 255));
        panelDatos.setBorder(BorderFactory.createTitledBorder("Parámetros del Modelo"));
        
        panelDatos.add(new JLabel("  Demanda Anual (D): " + 
                       String.format("%.0f", producto.demandaAnual) + " unidades"));
        panelDatos.add(new JLabel("  Costo por Orden (S): Bs " + 
                       String.format("%.2f", producto.costoOrden)));
        panelDatos.add(new JLabel("  Costo de Mantener (H): Bs " + 
                       String.format("%.2f", producto.costoMantener) + "/unidad/año"));
        panelDatos.add(new JLabel("  Precio por Unidad (P): Bs " + 
                       String.format("%.2f", producto.precioUnidad)));
        
        // EOQ calculado
        JLabel lblEOQ = new JLabel("  EOQ Óptimo: " + String.format("%.0f", eoq) + " unidades");
        lblEOQ.setFont(new Font("Arial", Font.BOLD, 14));
        lblEOQ.setForeground(new Color(0, 100, 0));
        panelDatos.add(lblEOQ);
        
        // Costo total mínimo
        JLabel lblCostoMin = new JLabel("  Costo Total Mínimo: Bs " + 
                                       String.format("%.2f", costoTotalMinimo));
        lblCostoMin.setFont(new Font("Arial", Font.BOLD, 14));
        lblCostoMin.setForeground(new Color(0, 0, 150));
        panelDatos.add(lblCostoMin);
        
        panelPrincipal.add(panelDatos, BorderLayout.CENTER);
        
        // Panel de información adicional
        JPanel panelExtra = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelExtra.setBackground(new Color(240, 248, 255));
        
        double numeroOrdenes = producto.demandaAnual / eoq;
        double tiempoEntreOrdenes = 365 / numeroOrdenes;
        
        JLabel lblExtra = new JLabel(String.format(
            "Número de órdenes al año: %.1f  |  Días entre órdenes: %.1f  |  Punto de reorden: %.0f unidades", 
            numeroOrdenes, tiempoEntreOrdenes, eoq / 2));
        lblExtra.setFont(new Font("Arial", Font.PLAIN, 12));
        panelExtra.add(lblExtra);
        
        panelPrincipal.add(panelExtra, BorderLayout.SOUTH);
        
        return panelPrincipal;
    }
    
    private static JFreeChart crearGrafica(DatoEOQ producto, double eoq) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // Series para cada tipo de costo
        XYSeries serieOrdenar = new XYSeries("Costo de Ordenar");
        XYSeries serieMantener = new XYSeries("Costo de Mantener");
        XYSeries serieTotal = new XYSeries("Costo Total");
        
        // Rango dinámico basado en EOQ
        int cantidadMin = Math.max(10, (int)(eoq * 0.2));
        int cantidadMax = (int)(eoq * 3);
        int incremento = Math.max(1, (cantidadMax - cantidadMin) / 200);
        
        // Calcular costos para cada cantidad
        for (int q = cantidadMin; q <= cantidadMax; q += incremento) {
            double costoOrdenar = calcularCostoOrdenar(producto, q);
            double costoMantener = calcularCostoMantener(producto, q);
            double costoTotal = costoOrdenar + costoMantener;
            
            serieOrdenar.add(q, costoOrdenar);
            serieMantener.add(q, costoMantener);
            serieTotal.add(q, costoTotal);
        }
        
        dataset.addSeries(serieOrdenar);
        dataset.addSeries(serieMantener);
        dataset.addSeries(serieTotal);
        
        // Crear gráfica
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Análisis de Costos - Modelo EOQ",
            "Cantidad de Orden (Q)",
            "Costo Anual (Bs)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Personalizar apariencia
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        // Personalizar líneas
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        // Costo de Ordenar - Azul
        renderer.setSeriesPaint(0, new Color(0, 102, 204));
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(0, false);
        
        // Costo de Mantener - Naranja
        renderer.setSeriesPaint(1, new Color(255, 102, 0));
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(1, false);
        
        // Costo Total - Verde
        renderer.setSeriesPaint(2, new Color(0, 153, 51));
        renderer.setSeriesStroke(2, new BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(2, false);
        
        plot.setRenderer(renderer);
        
        return chart;
    }
    
    private static void actualizarEOQenBD(int id, double eoqCalc, double costoTotal, Conexion cn) {
        Connection con = null;
        PreparedStatement ps = null;
        
        try {
            String sql = "UPDATE inventario_eoq SET eoq_calculado = ?, " +
                        "costo_total_minimo = ?, fecha_calculo = NOW() WHERE id = ?";
            
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setDouble(1, eoqCalc);
            ps.setDouble(2, costoTotal);
            ps.setInt(3, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar EOQ en BD: " + e.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.toString());
            }
        }
    }
    
    // Costo de Ordenar = (D/Q) * S
    private static double calcularCostoOrdenar(DatoEOQ producto, double q) {
        return (producto.demandaAnual / q) * producto.costoOrden;
    }
    
    // Costo de Mantener = (Q/2) * H
    private static double calcularCostoMantener(DatoEOQ producto, double q) {
        return (q / 2) * producto.costoMantener;
    }
    
    // Costo Total = Costo de Ordenar + Costo de Mantener
    private static double calcularCostoTotal(DatoEOQ producto, double q) {
        return calcularCostoOrdenar(producto, q) + calcularCostoMantener(producto, q);
    }
}