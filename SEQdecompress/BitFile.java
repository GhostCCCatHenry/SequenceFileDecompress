package SEQdecompress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import static SEQdecompress.App.is;

public class BitFile {
    private static int bitCount = 0;
    private static int bitBuffer= 0;
    //private static int len = 0;

    //public static void setByt(byte[] byt) {
        //BitFile.byt = byt;
    //}

    //private static byte[] byt=new byte[1<<22];


    public static int getInt(int num, int offset) {
        String numOfString = Integer.toBinaryString(num);
        String result;

        while (numOfString.length() < 32) {
            numOfString = "0".concat(numOfString);
        }
        char[] data= numOfString.toCharArray();
        result = new String(data, (8 * offset), 8);

        return (Integer.parseInt(result, 2));
    }

    public static String getString(String string, int flag) {
        int num = flag - string.length();
        for (int i = 0; i < num; i ++) {
            string = "0".concat(string);
        }

        return string;
    }

    public static int bitFileGetBit() {
        int returnValue;
        int Buffer = bitBuffer;
        int Count = bitCount;

        if (Count == 0) {
            if ((returnValue = is.read()) != -1) {
                Buffer = returnValue;
                Count = 8;
            } else {
                return -1;
            }
        }

        Count --;
        bitBuffer= Buffer;
        bitCount = Count;

        returnValue = ((Buffer >> Count) & 0x01);

        return returnValue;
    }

    public static int bitFileGetChar() {
        int returnValue = -1;
        int tmp;
        int Buffer = bitBuffer;
        int Count = bitCount;

        returnValue = is.read();

        if (Count == 0) {
            return returnValue;
        }

        if (returnValue != -1) {
            tmp = returnValue >> Count;
            tmp = tmp | getInt(Buffer << (8 - Count), 3);
            Buffer = returnValue;
            bitBuffer=Buffer;
            returnValue = tmp;
        }

        return returnValue;
    }

    public static int bitFileGetBitsInt(int count) {
        int returnValue;
        int remaining = count;
        int tmp = 0;
        String str = "";
        int flag = 0;

        while (remaining >= 8) {
            int num = bitFileGetChar();
            if (num != -1) {
                str = getString(Integer.toBinaryString(num), 8).concat(str);
                remaining -= 8;
            } else {
                return -1;
            }
        }

        if (remaining != 0) {
            while (remaining > 0) {
                returnValue = bitFileGetBit();
                if (remaining != -1) {
                    tmp <<= 1;
                    tmp |= (returnValue & 0x01);
                    remaining --;
                    flag ++;
                } else {
                    return -1;
                }
            }
            str = getString(Integer.toBinaryString(tmp), flag).concat(str);
        }

        return Integer.parseInt(str, 2);
    }
}
