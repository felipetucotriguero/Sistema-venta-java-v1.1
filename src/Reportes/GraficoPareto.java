package Reportes;

import Modelo.Conexion;
import java.awt.BasicStroke;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class GraficoPareto {
    
    // Clase auxiliar para almacenar datos de productos
    private static class DatoProducto {
        String nombre;
        double valorTotal;
        
        public DatoProducto(String nombre, double valorTotal) {
            this.nombre = nombre;
            this.valorTotal = valorTotal;
        }
    }
    
    /**
     * Genera un diagrama de Pareto de TODOS los productos
     * Analiza el valor total del inventario (precio * stock)
     * Esto te muestra qué productos representan el 80% del valor de tu inventario
     */
    public static void GraficarPareto() {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Conexion cn = new Conexion();
        
        try {
            // Consulta que calcula el valor total de cada producto
            String sql = "SELECT nombre, precio, stock, (precio * stock) as valor_total " +
                        "FROM productos " +
                        "ORDER BY valor_total DESC";
            
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // Lista para almacenar los productos
            List<DatoProducto> productos = new ArrayList<>();
            double totalGeneral = 0;
            
            // Leer todos los productos
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                double valorTotal = rs.getDouble("valor_total");
                
                productos.add(new DatoProducto(nombre, valorTotal));
                totalGeneral += valorTotal;
            }
            
            // Verificar si hay productos
            if (productos.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No hay productos registrados en la base de datos.",
                        "Sin datos",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Crear datasets para el gráfico
            DefaultCategoryDataset datasetBarras = new DefaultCategoryDataset();
            DefaultCategoryDataset datasetLinea = new DefaultCategoryDataset();
            
            // Llenar datasets con datos y porcentaje acumulado
            double acumulado = 0;
            for (DatoProducto producto : productos) {
                // Barras: valor total del producto
                datasetBarras.addValue(producto.valorTotal, "Valor Total", producto.nombre);
                
                // Línea: porcentaje acumulado
                acumulado += producto.valorTotal;
                double porcentajeAcumulado = (acumulado / totalGeneral) * 100;
                datasetLinea.addValue(porcentajeAcumulado, "% Acumulado", producto.nombre);
            }
            
            // Crear el gráfico
            JFreeChart chart = crearGraficoPareto(datasetBarras, datasetLinea, totalGeneral);
            
            // Mostrar el gráfico
            ChartFrame frame = new ChartFrame("Análisis de Pareto - Inventario de Productos", chart);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
        } catch (SQLException e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,
                    "Error al generar el diagrama de Pareto:\n" + e.getMessage(),
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
    
    /**
     * Método auxiliar para crear y configurar el gráfico de Pareto
     */
    private static JFreeChart crearGraficoPareto(DefaultCategoryDataset datasetBarras,
                                                  DefaultCategoryDataset datasetLinea,
                                                  double totalGeneral) {
        
        // Crear gráfico de barras base
        JFreeChart chart = ChartFactory.createBarChart(
                "Diagrama de Pareto - Análisis ABC de Inventario",
                "Productos (ordenados por valor: Precio × Stock)",
                "Valor Total en Inventario (Bs)",
                datasetBarras,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        
        // Personalizar el gráfico
        chart.setBackgroundPaint(Color.WHITE);
        
        // Obtener el plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);
        plot.setOutlineVisible(true);
        
        // Configurar el eje de categorías (productos)
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setMaximumCategoryLabelLines(2);
        
        // Configurar el eje Y izquierdo (valores)
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setNumberFormatOverride(new DecimalFormat("#,##0.00"));
        
        // Personalizar las barras
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();
        barRenderer.setSeriesPaint(0, new Color(70, 130, 180)); // Azul acero
        barRenderer.setDrawBarOutline(true);
        barRenderer.setSeriesOutlinePaint(0, new Color(30, 70, 120));
        barRenderer.setShadowVisible(false);
        
        // Agregar el segundo eje Y para el porcentaje acumulado
        NumberAxis axis2 = new NumberAxis("Porcentaje Acumulado (%)");
        axis2.setRange(0.0, 100.0);
        axis2.setNumberFormatOverride(new DecimalFormat("0'%'"));
        plot.setRangeAxis(1, axis2);
        plot.setDataset(1, datasetLinea);
        plot.mapDatasetToRangeAxis(1, 1);
        
        // Configurar el renderer para la línea acumulada
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, new Color(220, 20, 60)); // Rojo intenso
        lineRenderer.setSeriesStroke(0, new BasicStroke(3.0f));
        lineRenderer.setSeriesShapesVisible(0, true);
        lineRenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-4, -4, 8, 8));
        lineRenderer.setSeriesShapesFilled(0, true);
        plot.setRenderer(1, lineRenderer);
        
        // Configurar el orden de renderizado
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        
        // Añadir línea de referencia al 80% (Principio de Pareto - Regla 80/20)
        ValueMarker marker80 = new ValueMarker(80.0, 
                new Color(255, 10, 0), // Naranja
                new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                               10.0f, new float[]{10.0f, 5.0f}, 0.0f));
        marker80.setLabel("80% - Productos Clase A");
        marker80.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker80.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addRangeMarker(1, marker80, Layer.FOREGROUND);
        
        // Añadir línea de referencia al 15% (Principio de Pareto - Regla 80/20)
        ValueMarker marker15 = new ValueMarker(15.0, 
                new Color(10, 255, 0), // 
                new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                               10.0f, new float[]{10.0f, 5.0f}, 0.0f));
        marker15.setLabel("15% - Productos Clase B");
        marker15.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker15.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addRangeMarker(1, marker15, Layer.FOREGROUND);
        
        // Añadir línea de referencia al 80% (Principio de Pareto - Regla 80/20)
        ValueMarker marker5 = new ValueMarker(5.0, 
                new Color(225, 240, 0), // 
                new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                               10.0f, new float[]{10.0f, 5.0f}, 0.0f));
        marker5.setLabel("5% - Productos Clase C");
        marker5.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        marker5.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addRangeMarker(1, marker5, Layer.FOREGROUND);
        
        return chart;
    }
}