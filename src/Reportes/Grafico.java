package Reportes;

import Modelo.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class Grafico {

    public static void Graficar(String fecha) {
        Connection con;
        Conexion cn = new Conexion();
        PreparedStatement ps;
        ResultSet rs;

        try {
            String sql = "SELECT total FROM ventas WHERE fecha = ?";
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, fecha);
            rs = ps.executeQuery();

            DefaultPieDataset dataset = new DefaultPieDataset();
            int contador = 0;

            while (rs.next()) {
                dataset.setValue("Venta " + (contador + 1) + ": " +rs.getString("total"), rs.getDouble("total"));
                contador++;
            }

            // ✅ Si no se encontró ningún registro
            if (contador == 0) {
                JOptionPane.showMessageDialog(null,
                        "No hay registros de ventas para la fecha seleccionada.",
                        "Sin datos",
                        JOptionPane.WARNING_MESSAGE);
                return; // salir del método
            }

            // ✅ Si hay datos, mostrar el gráfico
            JFreeChart jf = ChartFactory.createPieChart("Reporte de Venta", dataset);
            ChartFrame f = new ChartFrame("Total de Ventas por día", jf);
            f.setSize(1000, 500);
            f.setLocationRelativeTo(null);
            f.setVisible(true);

        } catch (SQLException e) {
            System.out.println(e.toString());
            JOptionPane.showMessageDialog(null,
                    "Error al generar el gráfico:\n" + e.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
