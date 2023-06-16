package yoyo.service.base.network.wrap;


import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import yoyo.service.base.network.NetworkConfig.ConfigInfo;

public class YOYOCodecFactory implements ProtocolCodecFactory
{
    private ProtocolEncoder encoder;
    private ProtocolDecoder decoder;
    
    public ProtocolDecoder getDecoder (IoSession session) throws Exception
    {
        return decoder;
    }
    
    public ProtocolEncoder getEncoder (IoSession session) throws Exception
    {
        return encoder;
    }
    
    public YOYOCodecFactory(ConfigInfo config) throws Exception
    {
        decoder = (ProtocolDecoder) Class.forName(config.getDecoder()).newInstance();
        encoder = (ProtocolEncoder) Class.forName(config.getEncoder()).newInstance();
    }
}
