package com.dahua.device.common.util;

import org.apache.commons.codec.binary.Hex;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Random;

public class DigestUtils {


    /**
     * 加密遵循RFC2671规范 将相关参数加密生成一个MD5字符串,并返回
     */
    public static String getResponse(
            String username,
            String realm,
            String password,
            String nonce,
            String nc,
            String cnonce,
            String qop,
            String method,
            String uri
    ) {
        String A1 = username + ":" + realm + ":" + password;
        byte[] md5ByteA1 = md5(A1.getBytes());
        String HA1 = new String(Hex.encodeHex(md5ByteA1));

        String A2 = method + ":" + uri;
        byte[] md5ByteA2 = md5(A2.getBytes());
        String HA2 = new String(Hex.encodeHex(md5ByteA2));


        String original = HA1 + ":" + (nonce + ":" + nc + ":" + cnonce + ":" + qop) + ":" + HA2;
        // String original = HA1 + ":" + nonce + ":" + HA2;
        byte[] md5ByteOriginal = md5(original.getBytes());
        String response = new String(Hex.encodeHex(md5ByteOriginal));
        return response;
    }

    public static String getAuthorization(String username, String realm, String nonce, String uri, String qop, String nc, String cnonce, String response, String opaque) {
        String authorization = "Digest username=\"" + username + "\"" +
                ",realm=\"" + realm + "\"" +
                ",nonce=\"" + nonce + "\"" +
                ",uri=\"" + uri + "\"" +
                ",qop=\"" + qop + "\"" +
                ",nc=\"" + nc + "\"" +
                ",cnonce=\"" + cnonce + "\"" +
                ",response=\"" + response + "\"" +
                ",opaque=\"" + opaque;
        return authorization;
    }


    /**
     * 对输入字符串进行md5散列.
     */
    public static byte[] md5(byte[] input) {
        return digest(input, "MD5", null, 1);
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);

            if (salt != null) {
                digest.update(salt);
            }

            byte[] result = digest.digest(input);

            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateSalt2(int length) {
        String val = "";
        Random random = new Random();
        //参数length，表示生成几位随机数
        for (int i = 0; i < length; i++) {
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            //输出字母还是数字
            if ("char".equalsIgnoreCase(charOrNum)) {
                //输出是大写字母还是小写字母
                int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (random.nextInt(26) + temp);
            } else if ("num".equalsIgnoreCase(charOrNum)) {
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase();
    }


}
