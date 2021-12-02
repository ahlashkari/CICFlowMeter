package edu.ncat.susman;

import cic.cs.unb.ca.Sys;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Parameters {
    // HEADER FIELD BYTE LOCATION #
    public static final int VERSION_LOCATION = 0;
    public static final int PROTOCOL_LOCATION = 0;
    public static final int FLAGS_LOCATION = 1;
    public static final int PADDING_LOCATION = 1;
    public static final int LENGTH_LOCATION = 2;

    // HEADER FIELD SIZE (BYTES) #
    public static final int LENGTH_SIZE = 2;

    // HEADER SIZE (BITS)
    public static final int HEADER_SIZE = 32;
    public static final int BYTE_SIZE = 8;

    // VERSION #
    public static final int DEFAULT_VERSION = 1;

    // PROTOCOLS #
    public static final int SP_PROTOCOL = 0;
    public static final int IDP_PROTOCOL = 1;
    public static final int DVCP_PROTOCOL = 2;
    public static final int DGP_PROTOCOL = 3;

    // FLAGS #
    public static final int SP_REQUEST_FLAG = 0;
    public static final int SP_RESPONSE_FLAG = 1;
    public static final int IDP_REQUEST_FLAG = 0;
    public static final int IDP_RESPONSE_FLAG = 1;
    public static final int DVCP_DET_FLAG = 0;
    public static final int DVCP_ACK_FLAG = 1;
    public static final int DGP_REG_FLAG = 0;
    public static final int DGP_DEV_FLAG = 1;

    // ENCODING STANDARDS (BYTES) #
    public static final int IP_ADDRESS_SIZE = 4;
    public static final int TCP_UDP_PORT_SIZE = 2;
    public static final int IP_PROTOCOL_SIZE = 1;
    public static final int SAMPLE_SIZE = 312;
    public static final int UUID_SIZE = 16;

    // JAVA SPECIFIC ENCODING (BYTES) #
    public static final int FLOAT_SIZE = 4;
    public static final int CHAR_SIZE = 2;

    // SAMPLES #
    public static final int SAMPLE_NUMBER_OF_FLOAT_VALUES = 76;
    public static final int SAMPLE_TOTAL_NUMBER_OF_VALUES = 81;

    // CONNECTED APPLIANCE VARIABLES #
    public static final long CONNECTED_APPLIANCE_STATUS_TIMEOUT = 1000 * 60 * 30;

    // DEFAULT NETWORK INFO
    public static final String IP_ADDRESS_SYS_ADMIN = "192.168.1.50";
    public static final String IP_ADDRESS_VALIDATOR = "192.168.1.49";
    public static final String LOCALHOST = "localhost";
    public static final int BCP_PORT = 1891;

    // ARTIFICIAL IMMUNE SYSTEM
    public static final String DETECTOR_DIRECTORY = System.getProperty("user.dir") + Sys.FILE_SEP + "bin" + Sys.FILE_SEP + "detectors.csv";
    public static final String DATA_SET_DIRECTORY = System.getProperty("user.dir") + Sys.FILE_SEP + "bin" + Sys.FILE_SEP + "min_max.csv";
    public static final String DATA_DIRECTORY = System.getProperty("user.dir") + Sys.FILE_SEP + "bin" + Sys.FILE_SEP;
    public static final int DETECTOR_NUMBER_OF_FLOAT_VALUES = 156;
    public static final long IMMATURE_DETECTOR_LIFESPAN = 1000L * 60L * 60L * 24L;
    public static final long MATURE_DETECTOR_LIFESPAN = IMMATURE_DETECTOR_LIFESPAN * 7L;
    public static final long MEMORY_DETECTOR_LIFESPAN = IMMATURE_DETECTOR_LIFESPAN * 30L;
    public static final long IMMATURE_DETECTOR_INCORRECT_DETECTIONS_THRESHOLD = 100L;
    public static final long MATURE_DETECTOR_INCORRECT_THRESHOLD = IMMATURE_DETECTOR_INCORRECT_DETECTIONS_THRESHOLD * 10L;
    public static final long MEMORY_DETECTOR_INCORRECT_THRESHOLD = MATURE_DETECTOR_INCORRECT_THRESHOLD * 10L;

    // BYTES AND HEX CONVERTER
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /* s must be an even-length string. */
    public static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] ipAddressBytes(String address) {
        byte[] retBytes = new byte[4];
        String[] bytes = address.split("\\.");
        for (int i = 0; i < 4; i++) {
            retBytes[i] = (byte) Integer.parseInt(bytes[i]);
        }

        return retBytes;
    }

    public static byte[] portBytes(String port) {
        byte[] retBytes = new byte[2];
        int portValue = Integer.parseInt(port);

        if (portValue > 254) {
            retBytes[0] = (byte) (portValue - 255);
            retBytes[1] = (byte) 255;
        }
        else {
            retBytes[0] = (byte) 0;
            retBytes[1] = (byte) portValue;
        }

        return retBytes;
    }

    public static float byteArrayToLeFloat(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getFloat();
    }

    public static byte[] leFloatToByteArray(float value) {
        final ByteBuffer bb = ByteBuffer.allocate(Float.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putFloat(value);
        return bb.array();
    }

    public static int byteArrayToLeShort(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort();
    }

    public static byte[] leShortToByteArray(short value) {
        final ByteBuffer bb = ByteBuffer.allocate(Short.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putShort(value);
        return bb.array();
    }

    public static int byteArrayToLeInt(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public static byte[] leIntToByteArray(int value) {
        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(value);
        return bb.array();
    }
}
