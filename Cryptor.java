package test;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 * This class is used for every single encryption and decryption.
 * @author Srideep Banerjee
 */
public class Cryptor {
    private Cipher en=null,de=null;
    /**
     * Constructor that :<br>
     * 1.Formats the key string passed as parameter to be 16 characters long.<br>
     * 2.Generates a SecretKeySpec with formatted key string.
     * 3.Initializes and configures the encryptor and decryptor Cipher objects.
     * @param k The key that is used for encryption and decryption
     */
    public Cryptor(String k){
        try{
            //creating a unique key of length 16
            String s=k;
            while(k.length()<16)k=k+s;
            if(k.length()>16)k=k.substring(k.length()-16);
            Key key=new SecretKeySpec(k.getBytes(),"AES");
            en=Cipher.getInstance("AES");
            de=Cipher.getInstance("AES");
            en.init(Cipher.ENCRYPT_MODE, key);
            de.init(Cipher.DECRYPT_MODE, key);
        }catch(Exception e){System.out.println(e.getMessage());}
    }
    /**
     * This function is called to encrypt a byte array using the 'en' cipher object
     * @param b The byte array to be encrypted
     * @return The encrypted byte array
     */
    public byte[] encrypt(byte []b){
        if(b.length==0)return b;
        try{b=en.doFinal(b);}catch(Exception e){System.out.println(e.getMessage());}
        return b;
    }
    /**
     * This function is called to decrypt a byte array using the 'de' cipher object
     * @param b The byte array to be decrypted
     * @return The decrypted byte array
     */
    public byte[] decrypt(byte []b){
        if(b.length==0)return b;
        try{b=de.doFinal(b);}catch(Exception e){System.out.println(e.getMessage());}
        return b;
    }
}
