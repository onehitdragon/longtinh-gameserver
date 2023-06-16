package hero.charge.net;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * <p>
 * Copyright: DGFun CO., (c) 2008
 * </p>
 * 
 * @文件 ChargeCodeFactory.java
 * @创建者 DingChu
 * @版本 1.0
 * @时间 2010-6-22 上午10:24:34
 * @描述 ：
 */

public class ChargeCodeFactory implements ProtocolCodecFactory
{
    private ProtocolEncoder encoder;

    private ProtocolDecoder decoder;

    public ChargeCodeFactory () throws Exception
    {
        decoder = new ChargeDecoder();
        encoder = new ChargeEncoder();
    }

    public ProtocolDecoder getDecoder (IoSession session) throws Exception
    {
        return decoder;
    }

    public ProtocolEncoder getEncoder (IoSession session) throws Exception
    {
        return encoder;
    }
}
