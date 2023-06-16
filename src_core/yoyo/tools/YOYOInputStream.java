package yoyo.tools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class YOYOInputStream
{
    private DataInputStream dis;

    private ByteArrayInputStream bais;

    public YOYOInputStream(byte[] data)
    {
        bais = new ByteArrayInputStream(data);
        dis = new DataInputStream(bais);
    }

    public int readInt () throws IOException
    {
        return dis.readInt();
    }

    public byte readByte () throws IOException
    {
        return dis.readByte();
    }

    public void readFully (byte[] bytes, int start, int len) throws IOException
    {
        dis.readFully(bytes, start, len);
    }

    public short readShort () throws IOException
    {
        return dis.readShort();
    }

    public String readUTF () throws IOException
    {
        return dis.readUTF();
    }

    public long readLong () throws IOException
    {
        return dis.readLong();
    }

    public float readFloat () throws IOException
    {
        return dis.readFloat();
    }

    public double readDouble () throws IOException
    {
        return dis.readDouble();
    }

    public long skip (long value) throws IOException
    {
        return dis.skip(value);
    }

    public void mark (int pos) throws IOException
    {
        dis.mark(pos);
    }

    public void reset () throws IOException
    {
        dis.reset();
    }

    public void close () throws IOException
    {
        dis.close();
    }
}
