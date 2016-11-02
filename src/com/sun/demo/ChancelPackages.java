package com.sun.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChancelPackages {
	
	/**
	 * 读取验证写入的渠道号
	 * @throws Exception
	 */
    public void f() throws Exception {
        String channel = MCPTool.readContent(new File("/Users/sun/Documents/workspace/channel/output/luojilabreader_2.3.1_20160929_025421_40.apk"), "12345678");
        System.out.print(channel);
    }
	
    public static void main(String[] args) throws Exception {
		String cmdApkPath  = "-apkPath";
		String cmdOutdir  = "-outdir";
		String cmdChannelPath = "-channelPath";
		String cmdPassword = "-password";
		String help = "用法：java -jar channel.jar [" + cmdApkPath + "] [arg0] [" + cmdOutdir + "] [arg1] [" + cmdPassword + "] [arg2]"
				+ "\n" + cmdApkPath + "	APK文件目录（目录内筛选包含‘20’的文件）或者apk绝对路径"
				+ "\n" + cmdOutdir + "		输出路径（可选），默认输出到当前目录"
				+ "\n" + cmdChannelPath + "	渠道号文件，txt"
				+ "\n" + cmdPassword + "	加密密钥（可选），长度8位以上，如果没有该参数，不加密" 
				+ "\n例如："
				+ "\njava -jar channel.jar -path dedao_android.apk -channelPath channel.txt -password 12345678";
		
		if (args.length == 0 || args[0] == null || args[0].trim().length() == 0 
				|| args[2].trim().length() == 0) {
			System.out.println(help);
		} else {
			Map<String, String> argsMap = new LinkedHashMap<String, String>();
			for (int i = 0; i < args.length; i += 2) {
				if (i + 1 < args.length) {
					if (args[i + 1].startsWith("-")) {
						throw new IllegalStateException("args is error, help: \n" + help);
					} else {
						argsMap.put(args[i], args[i + 1]);
					}
				}
			}
			
			String path = argsMap.get(cmdApkPath);
//			File path = argsMap.containsKey(cmdApkPath) ? new File(argsMap.get(cmdApkPath)) : null;
			File out = argsMap.containsKey(cmdOutdir) ? new File(argsMap.get(cmdOutdir)) : null;
			String channelPath = argsMap.get(cmdChannelPath);
			String password = argsMap.get(cmdPassword);
		
			// 拿到文件内的渠道号列表
	        Map<String, String> channel_lsit = ReadTxtLine(channelPath);
	        System.out.println("待生成的渠道号：");
	        System.out.println(channel_lsit + "\n");
	        
	        // 拿到待生成渠道的安装包
	        String apkName = getApkPath(path);
//	        String apkName = path.getAbsolutePath();
	        System.out.println("待生成渠道的安装包：");
	        System.out.println(apkName + "\n");
	        
	        // 安装包存放目录，当前目录下的output
	        File output = new File(out, "output");
	        
	        // 如果安装包是有效的，删除旧渠道包，并开始生成新的渠道包
	        File file = new File(apkName);
	        if(file != null && file.exists() && channel_lsit.size() > 0){
	        	deleteFile(output);
	            setZipComment(file, output, channel_lsit, password);
	        }
		}
    }
	
	/**
	 * 拿到新鲜出炉的安装包
	 * @return
	 */
	public static String getApkPath(String path) {
		File apkDir = new File(path);
		String apkPath = null;
		if(apkDir.exists() && apkDir.isDirectory()) {
			File[] files = apkDir.listFiles();
			for(File file: files) {
				if(file.getName().contains("20")) {
					apkPath = file.getAbsolutePath();
				} 
			}
		} else if(apkDir.exists() && apkDir.isFile() && apkDir.getName().contains(".apk")) {
			apkPath = apkDir.getAbsolutePath();
		} else {
			try {
				throw new Exception("安装包不对，请检查后再试");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return apkPath;
	}
	
	/**
	 * 删除旧安装包及output文件夹
	 * @param output
	 * @return
	 */
	public static boolean deleteFile(File output) {
        if(output.exists() && output.isDirectory()) {
        	System.out.println("开始删除旧渠道包：");
        	for (File file: output.listFiles()) { 
        		System.out.println("delete " + file.getName());
        		file.delete();
        	}
        } 
        
        return output.delete();
	}
	
	/**
	 * 生成渠道包
	 * @param file
	 * @param output
	 * @param channel_lsit
	 * @throws Exception
	 */
    public static void setZipComment(File file, File output, Map<String, String> channel_lsit, String password) throws Exception {
    	output.mkdirs();
    	System.out.println("\n万事俱备，开始打渠道包：");
    	for(Map.Entry<String, String> entry: channel_lsit.entrySet()){  
        	String chancel = entry.getKey();
        	String chancel_text = entry.getValue();
            String[] args = new String[]{"-path",file.getAbsolutePath(),"-outdir",output.getAbsolutePath(),"-contents",chancel,"-channel",chancel_text,"-password",password};
            MCPTool.main(args);
        }
    }
    
	/**
	 * 读取txt文件，拿到渠道号
	 * @param path
	 * @return
	 */
	private static Map<String, String> ReadTxtLine(String path){
		Map<String, String> map = new HashMap<String, String>();
        try {
            File file = new File(path);
            if(file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = "";
                while((lineTxt = bufferedReader.readLine()) != null){				//按行读取
                	map.put(lineTxt.split(",")[0], lineTxt.split(",")[1]);
                }
                read.close();
	        } else {
	            System.err.println(path + "找不到指定的文件");
	        }           
        } catch (Exception e) {
        	System.err.println(path + "读取文件内容出错");
            e.printStackTrace();
        }
        
        return map;
    }
}