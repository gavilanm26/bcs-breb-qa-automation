package com.bcs.breb.utils.crypto;

import com.bcs.breb.utils.config.ConfigReader;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class Crypto {

    public static String encrypt(String json) {
        try {
            String rawKey = ConfigReader.get("crypto.key"); // Clave desde .properties
            String keyHex = getKeyHex(rawKey);              // Convertir a hex según backend
            byte[] key = hexStringToByteArray(keyHex);      // Convertir hex a bytes

            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));

            byte[] cipherText = cipher.doFinal(json.getBytes(StandardCharsets.UTF_8));
            byte[] authTag = new byte[16];
            System.arraycopy(cipherText, cipherText.length - 16, authTag, 0, 16);
            byte[] encryptedData = new byte[cipherText.length - 16];
            System.arraycopy(cipherText, 0, encryptedData, 0, encryptedData.length);

            String encrypted = String.format("%s:%s:%s", toHex(iv), toHex(authTag), toHex(encryptedData));

            return encrypted;
        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar", e);
        }
    }

    private static String getKeyHex(String key) {
        // Repetir hasta 32 bytes, cortar, pasar a hex, pad a 64 caracteres
        String repeated = key.repeat((int) Math.ceil(32.0 / key.length())).substring(0, 32);
        String hex = repeated.getBytes(StandardCharsets.UTF_8).length == 32
                ? bytesToHex(repeated.getBytes(StandardCharsets.UTF_8))
                : String.format("%064x", new java.math.BigInteger(1, repeated.getBytes(StandardCharsets.UTF_8)));
        return hex.length() < 64 ? String.format("%-64s", hex).replace(' ', '0') : hex;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xff & aByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        return data;
    }
}
