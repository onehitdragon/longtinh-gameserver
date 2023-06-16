package hero.share.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AllPictureDataDict {
	
    private HashMap<String, byte[]> imageMap = new HashMap<String, byte[]>();
    
    private HashMap<String, byte[]> animationMap = new HashMap<String, byte[]>();
    
    private HashMap<String, byte[]> fileDataMap = new HashMap<String, byte[]>();
    
    private static AllPictureDataDict    instance;

    private AllPictureDataDict()
    {
        load();
    }

    public static AllPictureDataDict getInstance ()
    {
        if (null == instance)
        {
            instance = new AllPictureDataDict();
        }

        return instance;
    }

    private void load ()
    {
        ArrayList<File> files = readDirFiles(
        		ShareServiceImpl.getInstance().getConfig().getPicture());
        
        //加载图片
        for (int i = 0; i < files.size(); i++)
        {
            String imageURL = "";
            File file = files.get(i);
        	imageURL = file.getPath().toLowerCase().replace("\\", "/");
        	fileDataMap.put(imageURL, getImageBytes(file));
//            String imageURL = "";
//            File file = files.get(i);
//            if (file.getName().toLowerCase().endsWith(".png"))
//            {
//            	imageURL = file.getPath().toLowerCase();
//                imageMap.put(imageURL, getImageBytes(file));
//            } else if (file.getName().toLowerCase().endsWith(".anu")) {
//            	imageURL = file.getPath().toLowerCase();
//            	animationMap.put(imageURL, getImageBytes(file));
//			}
        }

        files = null;
    }

    
	public ArrayList<File> readDirFiles(String dirName) {
		ArrayList<File> fileList = new ArrayList<File>();
		try {
			File file = new File(dirName);
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					File tempFile = files[i];
					if (tempFile.getName().indexOf(".svn") == -1) {
						if (tempFile.isDirectory()) {
							String subDirName = tempFile.getPath();
							ArrayList<File> list = readDirFiles(subDirName);
							for (int j = 0; j < list.size(); j++) {
								fileList.add(list.get(j));
							}
						} else {
							if (!tempFile.isFile()) {
								continue;
							}
							fileList.add(tempFile);
						}
						if (i == (files.length - 1)) {
							return fileList;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return fileList;
	}
	
	
    private byte[] getImageBytes (File _imageFile)
    {
        byte[] rtnValue = null;

        try
        {
            DataInputStream dis = null;

            dis = new DataInputStream(new FileInputStream(_imageFile));
            int imgFileByteSize = dis.available();
            rtnValue = new byte[imgFileByteSize];

            for (int pos = 0; (pos = dis.read(rtnValue, pos, imgFileByteSize
                    - pos)) != -1;);

            dis.close();
        }
        catch (Exception e)
        {
            LogWriter.error(null, e);
        }

        return rtnValue;
    }
    
    public byte[] getFileBytes (String _fileURL)
    {
    	return fileDataMap.get(_fileURL);
    }

    public byte[] getImageBytes (String _imageURL)
    {
        return imageMap.get(_imageURL);
    }
    
    public byte[] getAnimationBytes (String _anuURL)
    {
        return animationMap.get(_anuURL);
    }

}
