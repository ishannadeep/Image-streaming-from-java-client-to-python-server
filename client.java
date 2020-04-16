/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reading_eye;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Base64;
import javax.imageio.ImageIO;
import static reading_eye.segment_Img.imageToBufferedImage;

/**
 *
 * @author ishan
 */
public class Reading_eye {
 
 public static final String REMOTE_HOST = "192.168.10.13";
 public static final int REMOTE_PORT = 65495;
 
    /**
     * @param args the command line arguments
     */
    private static String encodeFileToBase64Binary(File file) throws Exception{
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            String encodedString=Base64.getEncoder().encodeToString(bytes);
            return encodedString;
    }
    private static long power( long a, long b, long P) 
    {  
        if (b == 1) 
            return a; 
        else
            return (a^ b) % P; 
    } 
    public static void main(String[] args) throws IOException {
      //P and G are random numbers for diffie hellman key exchange
      long P=230;
      long G=99;
      Socket socket = new Socket(REMOTE_HOST, REMOTE_PORT);
    
      BufferedWriter sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
      BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      // Write the text to the remote process.
      //creating client generated key
      String gen_key =""+power(G, 48, P);
      //sending client generated key to the server
      sockOut.write(gen_key, 0, gen_key.length());
      sockOut.newLine();
      sockOut.flush();
 
      // Read the generated key of the server from the remote process.
      String response = sockIn.readLine();

      if (response == null) {
        System.out.println("server didn't get the key");
       
      }else
      {
        long server_gen_key = Integer.parseInt(response);
        //generating the secret key
        String sec_key=""+(server_gen_key^48)%P;

         Aes_hybrid AES=new Aes_hybrid();
         //Adding generated secret key to the key to get 256-bit key 
        String key = sec_key+"gdbtiomagRDbsHLkdfhsdgfereijd";
        //converting key and plain text to byte arrays
        byte key_arr[] = key.getBytes("UTF8");
        //expand key
        int[][] keyarray= AES._expand_key(key_arr);
        //encrypting plaintext

        //Provide number of rows and column
        int row = 5;
        int col = 5;
        BufferedImage originalImgage = ImageIO.read(new File("C:\\Users\\ishan\\Documents\\NetBeansProjects\\opencv_test_2\\src\\opencv_test_2\\book_2.jpg"));
        //resizing the image into 1000*1000 pixels
        Image newImage = originalImgage.getScaledInstance(1000, 1000, Image.SCALE_DEFAULT);
        BufferedImage originalImgage_2 = imageToBufferedImage(newImage);
        //total width and total height of an imageaas
        int tWidth = originalImgage_2.getWidth();
        int tHeight = originalImgage_2.getHeight();
        //width and height of each piece
        int eWidth = tWidth / col;
        int eHeight = tHeight / row;
        int x = 0;
        int y = 0;
        int round =0;
        for (int i = 0; i < row; i++) {
            y = 0;
            for (int j = 0; j < col; j++) {
                try {
                    
                    DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
                    DataInputStream din=new DataInputStream(socket.getInputStream());
                    
                    
                    BufferedImage SubImgage = originalImgage_2.getSubimage(y, x, eWidth, eHeight);
                    File outputfile = new File("C:\\Users\\ishan\\Documents\\NetBeansProjects\\opencv_test_2\\src\\opencv_test_2\\1000pixel"+i+j+".jpg");
                    ImageIO.write(SubImgage, "jpg", outputfile);
                    
                    //String gh="helloaishannadeepkalansooriya";
                    String encodstring = encodeFileToBase64Binary(outputfile);
                    byte text_arr[] = encodstring.getBytes("UTF8");
                    byte[] cipher=AES.encrypt(text_arr);

                    dOut.write(cipher);

                    round++;
                    y += eWidth;
                    //receving server acknowledgement
                    byte receive= din.readByte();
                  
                    //53 is the byte format of servers response
                    if(receive==53){
                      //empting input and output data stream  
                      dOut=null;
                      din=null;
                      continue;
                    }else{
                      break;
                    }
   
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            x += eHeight;
        }
      }   
    }
}
