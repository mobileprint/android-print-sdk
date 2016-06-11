package com.hp.impulselib.bt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Obex {
    static final int OPCODE_CONNECT = 0x80;
    static final int OPCODE_PUT = 0x02;
    static final int OPCODE_PUT_FINAL = 0x82;

    static final int RESPONSE_SUCCESS = 0xA0;
    static final int RESPONSE_CONTINUE = 0x90;
    static final int RESPONSE_NOT_FOUND = 0xC4;

    static final int HEADER_COUNT = 0xC0;
    static final int HEADER_NAME = 0x01;
    static final int HEADER_TYPE = 0x42;
    static final int HEADER_LENGTH = 0xC3;
    static final int HEADER_TIME_ISO = 0x44;
    static final int HEADER_TIME_COMPAT = 0xC4;
    static final int HEADER_DESCRIPTION = 0x05;
    static final int HEADER_TARGET = 0x46;
    static final int HEADER_HTTP = 0x47;
    static final int HEADER_BODY = 0x48;
    static final int HEADER_END_OF_BODY = 0x49;
    static final int HEADER_WHO = 0x4A;
    static final int HEADER_CONNECTION_ID = 0xCB;
    static final int HEADER_APP_PARAMETERS = 0x4C;
    static final int HEADER_AUTH_CHALLENGE = 0x4D;
    static final int HEADER_AUTH_RESPONSE = 0x4E;
    static final int HEADER_CREATOR_ID = 0xCF;
    static final int HEADER_WAN_UUID = 0x50;
    static final int HEADER_OBJECT_CLASS = 0x51;
    static final int HEADER_SESSION_PARAMETERS = 0x52;
    static final int HEADER_SESSION_SEQUENCE_NUMBER = 0x93;

    static final int HEADER_FORMAT_MASK = 0xC0;
    static final int HEADER_FORMAT_STRING = 0x00;
    static final int HEADER_FORMAT_BYTES = 0x40;
    static final int HEADER_FORMAT_BYTE = 0x80;
    static final int HEADER_FORMAT_INT = 0xC0;

    abstract static class Header {
        private final int mId;

        protected Header(int id) {
            mId = id;
        }

        /**
         * Return the header identifier (HEADER_*)
         */
        public int getId() {
            return mId;
        }

        public static Header read(ByteBuffer in) {
            if (in.remaining() < 2) return null;
            int id = in.get() & 0xFF;
            switch (id & HEADER_FORMAT_MASK) {
                case HEADER_FORMAT_STRING:
                case HEADER_FORMAT_BYTES:
                    if (in.remaining() < 2) return null;
                    int length = in.getShort();
                    byte bytes[] = new byte[length];
                    if (in.remaining() < length) return null;
                    in.get(bytes);
                    if ((id & HEADER_FORMAT_MASK) == HEADER_FORMAT_STRING) {
                        return new StringHeader(id, bytes);
                    } else {
                        return new BytesHeader(id, bytes);
                    }

                case HEADER_FORMAT_BYTE:
                    return new ByteHeader(id, in.get());

                case HEADER_FORMAT_INT:
                    if (in.remaining() < 4) return null;
                    return new IntHeader(id, in.getInt());
            }
            return null; // unreachable
        }

        public void write(ByteBuffer out) {
            out.put((byte) mId);
            writeValue(out);
        }

        protected abstract void writeValue(ByteBuffer out);

        public abstract int length();

    }

    static class IntHeader extends Header {
        private final int mInt;

        public IntHeader(int id, int anInt) {
            super(id);
            mInt = anInt;
        }

        public int getInt() {
            return mInt;
        }

        @Override
        public int length() {
            return 5;
        }

        @Override
        protected void writeValue(ByteBuffer out) {
            out.putInt(mInt);
        }
    }

    static class ByteHeader extends Header {
        private final byte mByte;
        public ByteHeader(int id, byte aByte) {
            super(id);
            mByte = aByte;
        }

        @Override
        public int length() {
            return 2;
        }

        @Override
        protected void writeValue(ByteBuffer out) {
            out.put(mByte);
        }
    }

    static class BytesHeader extends Header {
        private final byte mBytes[];

        public BytesHeader(int id, byte bytes[]) {
            super(id);
            mBytes = bytes;
        }

        @Override
        public int length() {
            return 3 + mBytes.length;
        }

        @Override
        protected void writeValue(ByteBuffer out) {
            out.putShort((short)(mBytes.length + 3));
            out.put(mBytes);
        }
    }

    static class StringHeader extends BytesHeader {
        private final String mString;

        public StringHeader(int id, byte[] bytes) {
            super(id, bytes);
            mString = new String(bytes, StandardCharsets.UTF_16BE);
        }

        public StringHeader(int id, String string) {
            super(id, nullTerminated(string));
            mString = string;
        }

        private static byte[] nullTerminated(String string) {
            byte unterminated[] = string.getBytes(StandardCharsets.UTF_16BE);
            byte terminated[] = new byte[unterminated.length + 2];
            System.arraycopy(unterminated, 0, terminated, 0, unterminated.length);
            return terminated;
        }

        public String getString() {
            return mString;
        }
    }

    static class Packet {
        /** Operation code or response code */
        private final int mCode;

        /** Additional bytes between opcode+len and headers if any, may be null*/
        private final byte[] mExtras;

        /** Headers accompanying this packet */
        private final List<Header> mHeaders;

        protected Packet(int code, byte[] extras, List<Header> headers) {
            this.mCode = code;
            this.mExtras = extras;
            this.mHeaders = headers;
        }
        protected Packet(int code, byte[] extras) {
            this(code, extras, new ArrayList<Header>());
        }

        protected Packet(int code) {
            this(code, null);
        }

        public int getCode() {
            return mCode;
        }

        public byte[] getExtras() {
            return mExtras;
        }

        /** Add a header to the packet */
        public Packet addHeader(Header header) {
            mHeaders.add(header);
            return this;
        }

        /** Return number of bytes remaining in packet to fill with headers */
        public int remaining(int mMaxPacketLen) {
            int remainingLength = mMaxPacketLen;
            remainingLength -= 3;
            remainingLength -= (mExtras != null ? mExtras.length : 0);
            for (Header header: mHeaders) {
                remainingLength -= header.length();
            }
            return remainingLength;
        }

        /** Return a packet or null if not possible */
        static Packet read(InputStream in, int extraBytesLen) {
            try {
                if (in.available() < 3) return null;
                byte top[] = new byte[3];
                in.mark(0xFFFF);
                in.read(top);
                ByteBuffer bb = ByteBuffer.wrap(top);

                int code = bb.get() & 0xFF;
                int packetLen = bb.getShort() & 0xFFFF;

                // Make sure we have all the data we'll need
                if (in.available() < packetLen - 3) {
                    in.reset();
                    return null;
                }

                byte extras[] = null;
                // Read extra bytes
                if (extraBytesLen > 0) {
                    extras = new byte[extraBytesLen];
                    in.read(extras);
                }

                // Read remaining headers if any
                int remainingLen = packetLen - 3 - extraBytesLen;
                bb = ByteBuffer.allocate(remainingLen);
                in.read(bb.array());

                List<Header> headers = new ArrayList<>();
                Header header;
                do {
                    header = Header.read(bb);
                    if (header != null) headers.add(header);
                } while(header != null);

                return new Packet(code, extras, headers);
            } catch (IOException e) {
                return null;
            }
        }

        /** Return the contents of this packet as a byte array */
        public byte[] getBytes() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            /** Write the current state of the packet to the output stream */
            int extrasLen = (mExtras != null ? mExtras.length : 0);
            ByteBuffer front = ByteBuffer.allocate(3 + extrasLen);
            front.put((byte)mCode);
            front.putShort((short)0);
            if (mExtras != null) front.put(mExtras);

            int headerLen = 0;
            for (Header header : mHeaders) {
                headerLen += header.length();
            }

            ByteBuffer headerBuffer = ByteBuffer.allocate(headerLen);
            for (Header header : mHeaders) {
                header.write(headerBuffer);
            }
            front.putShort(1, (short)(3 + extrasLen + headerBuffer.position()));

            try {
                out.write(front.array());
                out.write(headerBuffer.array(), 0, headerBuffer.position());
            } catch (IOException dummy) {
            }
            return out.toByteArray();
        }
    }
}
