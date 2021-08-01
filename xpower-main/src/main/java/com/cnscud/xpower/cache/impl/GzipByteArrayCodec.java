package com.cnscud.xpower.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.redisson.client.codec.BaseCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class GzipByteArrayCodec extends BaseCodec {

    public static final GzipByteArrayCodec INSTANCE = new GzipByteArrayCodec();

    private final Encoder encoder = new Encoder() {
        @Override
        public ByteBuf encode(Object in) throws IOException {
            final byte[] src = (byte[]) in;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(src.length);
            try(GZIPOutputStream gzip = new GZIPOutputStream(baos)){
                gzip.write(src);
            }
            return Unpooled.wrappedBuffer(baos.toByteArray());
        }
    };

    private final Decoder<Object> decoder = new Decoder<Object>() {
        @Override
        public Object decode(ByteBuf buf, State state) {
            byte[] result = new byte[buf.readableBytes()];
            buf.readBytes(result);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(result.length);
            byte[] tmp = new byte[64];
            try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(result))) {
                int num = gzip.read(tmp);
                while (num > 0) {
                    bos.write(tmp, 0, num);
                    num = gzip.read(tmp);
                }
                return bos.toByteArray();
            } catch (IOException ex) {
                throw new RuntimeException("decode error", ex);
            }
        }
    };

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

}