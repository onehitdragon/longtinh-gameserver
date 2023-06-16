//package hero.gm.tools.clienthandler;
//
//import java.io.IOException;
//
//import cn.digifun.gamemanager.Broadcast;
//import cn.digifun.gamemanager.Handler;
//import cn.digifun.gamemanager.core.GMProtocolID;
//import cn.digifun.gamemanager.message.ReConnectionResponse;
//
//public class ReConnectionHandler extends Handler
//{
//
//    public ReConnectionHandler()
//    {
//        super(GMProtocolID.REQUEST_RECONNECTION);
//    }
//
//    @Override
//    protected Handler newInstance ()
//    {
//        return new ReConnectionHandler();
//    }
//
//    @Override
//    protected void process () throws IOException
//    {
//        int sid = messageDataInputStream.readInt();
//
//        Broadcast.getInstance().send(this.message.getSessionID(),
//                new ReConnectionResponse(sid));
//    }
//
//}
