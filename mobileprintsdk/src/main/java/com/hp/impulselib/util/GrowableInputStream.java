package com.hp.impulselib.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Non-blocking InputStream that can have data added to the back anytime.
 * Supports mark and reset.
 */
public class GrowableInputStream extends InputStream {
    List<byte[]> mBuffers = new ArrayList<>();
    BufferPos readAt = new BufferPos();
    BufferPos markAt = null;

    private class BufferPos {
        int buffer = 0;
        int offset = 0;
        public BufferPos() {
        }
        public BufferPos(BufferPos other) {
            buffer = other.buffer;
            offset = other.offset;
        }

        public void align() {
            while (offset > 0 && offset >= mBuffers.get(buffer).length) {
                offset -= mBuffers.get(buffer).length;
                buffer++;
            }
        }
    }

    @Override
    public int read() throws IOException {
        readAt.align();
        byte aByte = mBuffers.get(readAt.buffer)[readAt.offset++];
        trim();
        return aByte;
    }

    // After reading, check to see if we can release any buffers
    private void trim() {
        while(readAt.buffer > 0 && (markAt == null || markAt.buffer > 0)) {
            readAt.buffer--;
            if (markAt != null) markAt.buffer--;
            mBuffers.remove(0);
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        readAt.align();
        markAt = new BufferPos(readAt);
    }

    @Override
    public void reset() throws IOException {
        readAt = markAt;
        markAt = null;
        trim();
    }

    @Override
    public int read(byte[] out, int outOffset, int outLength) throws IOException {
        if (outLength == 0) return 0;
        int written = 0;
        while(written < outLength) {
            readAt.align();

            // Check for end of input
            if (readAt.buffer >= mBuffers.size()) {
                written = written == 0 ? -1 : written;
                break;
            }

            // We're looking at some data so copy it
            byte from[] = mBuffers.get(readAt.buffer);
            int toWrite = Math.min(outLength - written, from.length - readAt.offset);
            System.arraycopy(from, readAt.offset, out, outOffset + written, toWrite);
            readAt.offset += toWrite;
            written += toWrite;
        }
        trim();
        return written;
    }

    /** Add a buffer to be made available to readers of the InputStream */
    public void add(byte data[]) {
        // TODO: Copy input?
        mBuffers.add(data);
    }

    @Override
    public int available() {
        int length = 0;
        readAt.align();
        int buffer = readAt.buffer;
        int offset = readAt.offset;
        while (buffer < mBuffers.size()) {
            length += mBuffers.get(buffer++).length - offset;
            offset = 0;
        }
        return length;
    }
}
