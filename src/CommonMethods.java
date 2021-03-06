import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/*
Clase que implementé para poder guardar inicio y fin en cada entrada en el vector, algo así como un struct
 */

class IndexesInImage implements Serializable {  // puse esto de serializable para que se pueda pasar a archivo
    public int inicio;    // Donde empieza a mapear el pedazo del mensaje
    public int fin;    // Donde termina de mapear el pedazo del mensaje
    public boolean valido;

    public IndexesInImage(int inicio, int fin, boolean valor) {
        this.inicio = inicio;
        this.fin = fin;
        this.valido = valor;
    }
    public IndexesInImage(boolean valor) {
        this.valido = valor;
    }
}

public class CommonMethods {

    public static byte[] urlToFinalByteArray(String url){
        byte[] finalByteArray = null;
        try {
            BufferedImage bImage = urlToBufferedImage(new URL(url));
            byte[] imagePixels = imageToByteArray(bImage);
            LinkedList missingBytes = getMissingBytes(imagePixels);
            finalByteArray = createFinalByteArray(imagePixels, missingBytes);
        } catch (MalformedURLException e) {
            System.out.println("URL no válida");
        }
        return finalByteArray;
    }

    public static BufferedImage urlToBufferedImage(URL url) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            System.out.println("No se pudo sacar la imagen del URL");
        }
        return image;
    }

    public static byte[] imageToByteArray(BufferedImage bufferedImage) {
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
        return ( data.getData() );
    }

    // devuelve una lista con los bytes que faltan en el byteArray
    public static LinkedList getMissingBytes(byte[] byteArray) {
        LinkedList<Byte> missingByteList = new LinkedList<Byte>();
        for (byte b = Byte.MIN_VALUE; b < Byte.MAX_VALUE; b++) {
            missingByteList.add(b);
        }
        missingByteList.add((byte) 127);
        for (int i = 0; i < byteArray.length; i++) {
            //quita los bytes del array de la lista (porque no hacen falta)
            if (missingByteList.contains((Byte) byteArray[i])) {
                missingByteList.remove((Byte) byteArray[i]);
            }
        }
        return missingByteList;
    }

    public static byte[] createFinalByteArray(byte[] imageBytes, LinkedList missingBytes) {
        byte finalByteArray[] = new byte[imageBytes.length + missingBytes.size()];
        int i = 0;
        for (; i < imageBytes.length; i++) {
            finalByteArray[i] = imageBytes[i];
        }
        int missingBytesSize = missingBytes.size();
        for (int x = 0; x < missingBytesSize; x++) {
            finalByteArray[i] = (byte) missingBytes.remove();
            i++;
        }
        return finalByteArray;
    }

    //Text to byte array--------------------------------------------------------------------------------------


    public static byte[] stringToByteArray(String str) {
        byte[] byteArr = str.getBytes(StandardCharsets.UTF_8);

        return byteArr;
    }

    public static String byteArrayToString(byte[] byteArr) {
        String str = new String(byteArr, StandardCharsets.UTF_8);

        return str;
    }

    public static String interpretVector (Vector vector, byte[] imagen)
    {
        LinkedList<IndexesInImage> indexesList = vector.getVector();
        String mensaje = "";
        byte[] byteArr;
        for (IndexesInImage iter : indexesList) {
            //System.out.println(iter.inicio +  " -- " + iter.fin);
            if (iter.inicio == iter.fin) {
                byteArr = new byte[1];
            }
            else
            {
                byteArr = new byte[iter.fin - iter.inicio + 1];
            }
            for (int i = 0; i < byteArr.length; i++)
            {
                byteArr[i] = imagen[i+iter.inicio];
            }

            mensaje = mensaje + byteArrayToString(byteArr);
        }

        return mensaje;
    }

}