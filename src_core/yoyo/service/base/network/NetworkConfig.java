package yoyo.service.base.network;

import java.util.List;

import org.dom4j.Element;

import yoyo.service.base.AbsConfig;

public class NetworkConfig extends AbsConfig
{
    private byte gameId = 0;
    private int  serverCount = 0;

    ConfigInfo[] configs;
    public byte getGameId ()
    {
        return gameId;
    }

    public int getServerCount ()
    {
        return serverCount;
    }

    @Override
    public void init (Element element) throws Exception
    {
        List list = element.selectNodes("//networkservice/servers/*");
        if (list.size() <= 0)
        {
            throw new RuntimeException("not define server");
        }
        configs = new ConfigInfo[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            Element eServer = (Element) list.get(i);
            Element ePort = eServer.element("port");
            if (ePort == null)
            {
                throw new NullPointerException("can not find <port> tag");
            }
            String sPort = ePort.getTextTrim();
            if (sPort == null || sPort.equals(""))
            {
                throw new RuntimeException(eServer.getName() + " not define port");
            }
            Element eHandler = eServer.element("handler");
            if (eHandler == null)
            {
                throw new NullPointerException("can not find <handler> tag");
            }
            String sHandler = eHandler.getTextTrim();
            if (sHandler == null || sHandler.equals(""))
            {
                throw new RuntimeException(eServer.getName() + " not define handler");
            }
            Element eyoyo = eServer.element("yoyoserver");
            if (eyoyo == null)
            {
                throw new NullPointerException("can not find <yoyoserver> tag");
            }
            String sServer = eyoyo.getTextTrim();
            if (sServer == null || sServer.equals(""))
            {
                throw new RuntimeException(eServer.getName() + " not define yoyoserver");
            }
            Element eDecoder = eServer.element("decoder");
            String decoder = null;
            if (eDecoder != null)
            {
                decoder = eDecoder.getTextTrim();
            }
            Element eEncoder = eServer.element("encoder");
            String encoder = null;
            if (eEncoder != null)
            {
                encoder = eEncoder.getTextTrim();
            }
            configs[i] = new ConfigInfo();
            configs[i].port = Integer.parseInt(sPort);
            configs[i].process = sHandler;
            configs[i].server = sServer;
            configs[i].decoder = decoder;
            configs[i].encoder = encoder;
        }
        serverCount = configs.length;
    }

    public class ConfigInfo
    {
        private int port;
        private String process;
        private String server;
        private String decoder;
        private String encoder;
        public String getDecoder ()
        {
            return decoder;
        }
        public String getEncoder ()
        {
            return encoder;
        }
        public String getProcess ()
        {
            return process;
        }
        public int getPort ()
        {
            return port;
        }
        public String getServer ()
        {
            return server;
        }

        @Override
        public String toString ()
        {
            StringBuffer buf = new StringBuffer();
            buf.append("port=" + port + "\n");
            buf.append("server=" + server + "\n");
            buf.append("process=" + process + "\n");
            buf.append("encoder=" + encoder + "\n");
            buf.append("decoder=" + decoder + "\n");
            return buf.toString();
        }

    }
}
