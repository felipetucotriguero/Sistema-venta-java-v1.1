package Modelo;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class Eventos {

    /* Permite letras (a–z, A–Z), espacio y Backspace.
        Bloquea números y símbolos.
    El beep suena solo si el campo tiene texto, así no molesta al borrar todo.*/
    public void textKeyPress(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();

        // Permite letras, espacio y Backspace
        if (!Character.isLetter(car) && car != KeyEvent.VK_BACK_SPACE && car != KeyEvent.VK_SPACE) {
            evt.consume();

            // Solo hacer beep si hay algo escrito
            /*if (!textField.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }*/
        }
    }

    /*
        Permite números y Backspace normalmente.
        Bloquea cualquier otro carácter.
       El beep solo suena si hay texto en el campo, así que no molesta al borrar todo.
     */
    public void numberKeyPress(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();

        // Permite números y Backspace
        if (!Character.isDigit(car) && car != KeyEvent.VK_BACK_SPACE) {
            evt.consume();

            // Solo hacer beep si hay algo escrito
            /*if (!textField.getText().isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }*/
        }
    }

    /* Permite  Permite:

    Letras (a-z, A-Z)
    Números (0-9)
    Espacio
    Backspace
    Bloquea cualquier otro carácter (@, #, !, etc.).

    El beep suena solo si el campo tiene texto, así no molesta al borrar todo. */
    public void textAndNumberKeyPress(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();

        // Permite letras, números, espacio, Backspace, '-' y '_'
        if (!Character.isLetterOrDigit(car) && car != KeyEvent.VK_BACK_SPACE
                && car != KeyEvent.VK_SPACE && car != '-' && car != '_') {
            evt.consume();
        }
    }

    public void numberDecimalKeyPress(KeyEvent evt, JTextField textField) {
// declaramos una variable y le asignamos un evento
        char car = evt.getKeyChar();
        if ((car < '0' || car > '9') && textField.getText().contains(".") && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        } else if ((car < '0' || car > '9') && (car != '.') && (car != (char) KeyEvent.VK_BACK_SPACE)) {
            evt.consume();
        }
    }

    // PARA TELEFONOS, ACEPTA NUMEROS PERO TAMBIEN EN + PARA LOS PAISES
    public void numberPlusSpaceKeyPress(KeyEvent evt, JTextField textField) {
        char car = evt.getKeyChar();
        String text = textField.getText();

        // Permitir Backspace siempre
        if (car == KeyEvent.VK_BACK_SPACE) {
            return;
        }

        // Permitir '+' solo al inicio
        if (car == '+' && text.isEmpty()) {
            return;
        }

        // Permitir espacio solo si NO está al inicio y no hay otro espacio consecutivo
        if (car == ' ') {
            if (text.isEmpty() || text.endsWith(" ") || text.contains("  ")) {
                evt.consume();
                Toolkit.getDefaultToolkit().beep();
            }
            return;
        }

        // Permitir solo números
        if (!Character.isDigit(car)) {
            evt.consume();
            if (!text.isEmpty()) {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
