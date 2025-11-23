package Modelo;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// Para generar QR
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.filechooser.FileSystemView;

public class VentaDao {

    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;

    public int IdVenta() {
        int id = 0;
        String sql = "SELECT MAX(id) FROM ventas";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return id;
    }

    public int RegistrarVenta(Venta v) {
        String sql = "INSERT INTO ventas (cliente, vendedor, total, fecha) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, v.getCliente());
            ps.setString(2, v.getVendedor());
            ps.setDouble(3, v.getTotal());
            ps.setString(4, v.getFecha());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }

    public int RegistrarDetalle(Detalle Dv) {
        String sql = "INSERT INTO detalle (id_pro, cantidad, precio, id_venta) VALUES (?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, Dv.getId_pro());
            ps.setInt(2, Dv.getCantidad());
            ps.setDouble(3, Dv.getPrecio());
            ps.setInt(4, Dv.getId());
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return r;
    }

    public boolean ActualizarStock(int cant, int id) {
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, cant);
            ps.setInt(2, id);
            ps.execute();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public List Listarventas() {
        List<Venta> ListaVenta = new ArrayList();
        String sql = "SELECT c.id AS id_cli, c.nombre, v.* FROM clientes c INNER JOIN ventas v ON c.id = v.cliente";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Venta vent = new Venta();
                vent.setId(rs.getInt("id"));
                vent.setNombre_cli(rs.getString("nombre"));
                vent.setVendedor(rs.getString("vendedor"));
                vent.setTotal(rs.getDouble("total"));
                ListaVenta.add(vent);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaVenta;
    }

    public Venta BuscarVenta(int id) {
        Venta cl = new Venta();
        String sql = "SELECT * FROM ventas WHERE id = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                cl.setId(rs.getInt("id"));
                cl.setCliente(rs.getInt("cliente"));
                cl.setTotal(rs.getDouble("total"));
                cl.setVendedor(rs.getString("vendedor"));
                cl.setFecha(rs.getString("fecha"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return cl;
    }

    // Metodo añadido para mostrar la prediccion
    // Método para obtener las últimas N ventas
    public List<Integer> obtenerUltimasVentas(int limite) {
        List<Integer> ventas = new ArrayList<>();
        String sql = "SELECT total FROM ventas ORDER BY id DESC LIMIT ?";

        try (Connection con = new Conexion().getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ventas.add(rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener ventas: " + e.getMessage());
        }
        return ventas;
    }

    public void pdfV(int idventa, int Cliente, double total, String usuario) {
    try {
        Date date = new Date();
        FileOutputStream archivo;
        String url = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
        File salida = new File(url + "/ticket_venta_" + idventa + ".pdf");
        archivo = new FileOutputStream(salida);
        
        // Tamaño de ticket: 80mm de ancho, altura AUTO-AJUSTABLE
        Rectangle ticketSize = new Rectangle(226, 800); // Altura grande inicial, se ajustará
        Document doc = new Document(ticketSize, 8, 8, 8, 8); // Márgenes mínimos
        
        PdfWriter.getInstance(doc, archivo);
        doc.open();
        
        // Fuentes para ticket
        Font titulo = new Font(Font.FontFamily.COURIER, 10, Font.BOLD, BaseColor.BLACK);
        Font normal = new Font(Font.FontFamily.COURIER, 8, Font.NORMAL, BaseColor.BLACK);
        Font pequeña = new Font(Font.FontFamily.COURIER, 7, Font.NORMAL, BaseColor.BLACK);
        Font negrita = new Font(Font.FontFamily.COURIER, 8, Font.BOLD, BaseColor.BLACK);
        
        // Logo centrado (más pequeño para ticket)
        try {
            Image img = Image.getInstance(getClass().getResource("/Img/logo_pdf.png"));
            img.scaleToFit(50, 50);
            img.setAlignment(Element.ALIGN_CENTER);
            doc.add(img);
            doc.add(new Paragraph(" ", pequeña)); // Espaciado mínimo
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo: " + e.toString());
        }
        
        // Info empresa
        String config = "SELECT * FROM config";
        String mensaje = "";
        String nitEmpresa = "";
        
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(config);
            rs = ps.executeQuery();
            if (rs.next()) {
                mensaje = rs.getString("mensaje");
                nitEmpresa = rs.getString("dni_empresa");
                
                Paragraph empresa = new Paragraph();
                empresa.add(new Chunk(rs.getString("nombre") + "\n", titulo));
                empresa.add(new Chunk("NIT: " + nitEmpresa + "\n", pequeña));
                empresa.add(new Chunk("Tel: " + rs.getString("telefono") + "\n", pequeña));
                empresa.add(new Chunk(rs.getString("direccion") + "\n", pequeña));
                empresa.setAlignment(Element.ALIGN_CENTER);
                empresa.setSpacingAfter(3f);
                doc.add(empresa);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        
        // Línea separadora
        Paragraph linea1 = new Paragraph("======================================", pequeña);
        linea1.setAlignment(Element.ALIGN_CENTER);
        linea1.setSpacingAfter(3f);
        doc.add(linea1);
        
        // Datos de la venta
        Paragraph datosVenta = new Paragraph();
        datosVenta.add(new Chunk("FACTURA N°: " + idventa + "\n", titulo));
        datosVenta.add(new Chunk("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date) + "\n", pequeña));
        datosVenta.add(new Chunk("Cajero: " + usuario + "\n", pequeña));
        datosVenta.setAlignment(Element.ALIGN_CENTER);
        
        doc.add(datosVenta);
        
        // Cliente
        String prove = "SELECT * FROM clientes WHERE id = ?";
        try {
            ps = con.prepareStatement(prove);
            ps.setInt(1, Cliente);
            rs = ps.executeQuery();
            
            Paragraph cliente = new Paragraph();
            if (rs.next()) {
                
                // Línea separadora
                linea1.setAlignment(Element.ALIGN_CENTER);
                linea1.setSpacingAfter(3f);
                doc.add(linea1);
                
                
                cliente.add(new Chunk("Cliente: " + rs.getString("nombre") + "\n", pequeña));
                cliente.add(new Chunk("Tel: " + rs.getString("telefono") + "\n", pequeña));

                
            } else {
                cliente.add(new Chunk("Cliente: Público General\n", pequeña));
            }
            cliente.setAlignment(Element.ALIGN_CENTER);
            cliente.setSpacingAfter(3f);
            doc.add(cliente);
            
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        
        // Línea separadora
        Paragraph linea2 = new Paragraph("======================================", pequeña);
        linea2.setAlignment(Element.ALIGN_CENTER);
        linea2.setSpacingAfter(2f);
        doc.add(linea2);
        
        // TABLA DE PRODUCTOS CON FORMATO MEJORADO
        PdfPTable tablaProductos = new PdfPTable(4);
        tablaProductos.setWidthPercentage(100);
        float[] columnWidths = new float[]{12f, 45f, 20f, 23f}; // Anchos proporcionales
        tablaProductos.setWidths(columnWidths);
        
        // Encabezados
        PdfPCell hCant = new PdfPCell(new Phrase("Cant", negrita));
        PdfPCell hDesc = new PdfPCell(new Phrase("Descripción", negrita));
        PdfPCell hPU = new PdfPCell(new Phrase("P.U.", negrita));
        PdfPCell hTotal = new PdfPCell(new Phrase("Total", negrita));
        
        // Estilo de encabezados
        hCant.setHorizontalAlignment(Element.ALIGN_CENTER);
        hDesc.setHorizontalAlignment(Element.ALIGN_LEFT);
        hPU.setHorizontalAlignment(Element.ALIGN_RIGHT);
        hTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        hCant.setBorder(Rectangle.BOTTOM);
        hDesc.setBorder(Rectangle.BOTTOM);
        hPU.setBorder(Rectangle.BOTTOM);
        hTotal.setBorder(Rectangle.BOTTOM);
        
        hCant.setPadding(2);
        hDesc.setPadding(2);
        hPU.setPadding(2);
        hTotal.setPadding(2);
        
        tablaProductos.addCell(hCant);
        tablaProductos.addCell(hDesc);
        tablaProductos.addCell(hPU);
        tablaProductos.addCell(hTotal);
        
        // Productos
        String product = "SELECT d.id, d.id_pro, d.id_venta, d.precio, d.cantidad, p.id, p.nombre FROM detalle d INNER JOIN productos p ON d.id_pro = p.id WHERE d.id_venta = ?";
        
        try {
            ps = con.prepareStatement(product);
            ps.setInt(1, idventa);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                double precio = rs.getDouble("precio");
                double subTotal = cantidad * precio;
                String nombre = rs.getString("nombre");
                
                // Celdas de datos
                PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(cantidad), normal));
                PdfPCell cellDesc = new PdfPCell(new Phrase(nombre, normal));
                PdfPCell cellPrecio = new PdfPCell(new Phrase(String.format("%.2f", precio), normal));
                PdfPCell cellTotal = new PdfPCell(new Phrase(String.format("%.2f", subTotal), normal));
                
                // Alineación
                cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellDesc.setHorizontalAlignment(Element.ALIGN_LEFT);
                cellPrecio.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
                
                // Sin bordes laterales
                cellCant.setBorder(Rectangle.NO_BORDER);
                cellDesc.setBorder(Rectangle.NO_BORDER);
                cellPrecio.setBorder(Rectangle.NO_BORDER);
                cellTotal.setBorder(Rectangle.NO_BORDER);
                
                // Padding mínimo
                cellCant.setPadding(2);
                cellDesc.setPadding(2);
                cellPrecio.setPadding(2);
                cellTotal.setPadding(2);
                
                tablaProductos.addCell(cellCant);
                tablaProductos.addCell(cellDesc);
                tablaProductos.addCell(cellPrecio);
                tablaProductos.addCell(cellTotal);
            }
            
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        
        tablaProductos.setSpacingAfter(3f);
        doc.add(tablaProductos);
        
        // Línea separadora
        Paragraph linea3 = new Paragraph("======================================", pequeña);
        linea3.setAlignment(Element.ALIGN_CENTER);
        linea3.setSpacingAfter(2f);
        doc.add(linea3);
        
        // Total con formato mejorado
        Paragraph totalPar = new Paragraph();
        totalPar.add(new Chunk(String.format("TOTAL Bs:          %10.2f", total), titulo));
        totalPar.setAlignment(Element.ALIGN_RIGHT);
        totalPar.setSpacingAfter(5f);
        doc.add(totalPar);
        
        // Línea separadora
        linea3.setSpacingAfter(5f);
        doc.add(linea3);
        
        // QR Code centrado
        try {
            String datosQR = "NIT:" + nitEmpresa
                    + "|Factura:" + idventa
                    + "|Fecha:" + new SimpleDateFormat("dd/MM/yyyy").format(date)
                    + "|Total:" + total;
            
            byte[] qrBytes = generarQR(datosQR);
            Image qrImage = Image.getInstance(qrBytes);
            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scaleAbsolute(70, 70); // QR compacto
            qrImage.setSpacingBefore(2f);
            qrImage.setSpacingAfter(2f);
            doc.add(qrImage);
            
            Paragraph textoQR = new Paragraph("Escanea para verificar", pequeña);
            textoQR.setAlignment(Element.ALIGN_CENTER);
            textoQR.setSpacingAfter(5f);
            doc.add(textoQR);
            
        } catch (WriterException e) {
            System.out.println("Error generando QR: " + e.toString());
        }
        
        doc.close();
        archivo.close();
        Desktop.getDesktop().open(salida);
        
    } catch (DocumentException | IOException e) {
        System.out.println(e.toString());
    }
}

    private byte[] generarQR(String texto) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(texto, BarcodeFormat.QR_CODE, 200, 200, hints);

        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 200);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
