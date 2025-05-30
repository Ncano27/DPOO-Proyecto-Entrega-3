package GUI;

import java.awt.image.BufferedImage;
import net.glxn.qrgen.javase.QRCode;
import net.glxn.qrgen.core.image.ImageType;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

public class QRUtils {
	public static BufferedImage generarQR(String texto, int ancho, int alto) {
		BufferedImage imagenQR_interna = null;
		try {
			ByteArrayOutputStream baos = QRCode.from(texto).to(ImageType.PNG).withSize(ancho, alto).stream();
			byte[] qrImageData = baos.toByteArray();
			imagenQR_interna = ImageIO.read(new ByteArrayInputStream(qrImageData));
		} catch (Exception e) {
			System.err.println("Error al generar código QR: " + e.getMessage());
			imagenQR_interna = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
			java.awt.Graphics2D g = imagenQR_interna.createGraphics();
			g.setColor(java.awt.Color.LIGHT_GRAY);
			g.fillRect(0, 0, ancho, alto);
			g.setColor(java.awt.Color.BLACK);
			g.drawString("Error QR", ancho / 2 - 20, alto / 2);
			g.dispose();
		}
		return imagenQR_interna;
	}
}