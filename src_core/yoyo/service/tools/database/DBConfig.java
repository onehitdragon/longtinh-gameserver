package yoyo.service.tools.database;

import org.dom4j.Element;
import org.apache.commons.dbcp2.BasicDataSource;

import yoyo.service.base.AbsConfig;

public class DBConfig extends AbsConfig
{
    String dbDriverName;
    String dbPoolName;
    String proxDriverName;
    String proxpoolName;
    BasicDataSource ds;

    @Override
    public void init (Element element) throws Exception
    {
        Element eDb = element.element("db");

        proxDriverName = eDb.elementTextTrim("prox_driver");
        dbDriverName = eDb.elementTextTrim("db_driver");

        Class.forName(dbDriverName);

        dbPoolName = eDb.elementTextTrim("pool_name");
        proxpoolName = eDb.elementTextTrim("proxool_name") + "." + dbPoolName;

        ds =  new BasicDataSource();
        ds.setUsername(eDb.elementTextTrim("user_name"));
        ds.setPassword(eDb.elementTextTrim("password"));
        ds.setUrl(eDb.elementTextTrim("url"));
        ds.setMaxIdle(Integer.parseInt(eDb.elementTextTrim("maximum-connection-count")));
        ds.setMinIdle(Integer.parseInt(eDb.elementTextTrim("minimum-connection-count")));
        ds.setMaxWaitMillis(Integer.parseInt(eDb.elementTextTrim("maximum-active-time")));
        ds.setInitialSize(5);
    }
}
