# Android多渠道打包工具
非常快，亲测50个渠道不到5秒

# 使用方法
```Bash
java -jar channel.jar [-apkPath] [arg0] [-outdir] [arg1] [-password] [arg2]
-apkPath	APK文件目录（目录内筛选包含‘20’的文件）或者apk绝对路径
-outdir		输出路径（可选），默认输出到当前目录
-channelPath	渠道号文件，txt
-password	加密密钥（可选），长度8位以上，如果没有该参数，不加密
例如：
java -jar channel.jar -apkPath android.apk -channelPath channel.txt -password 12345678
```
# channel.txt格式
```Bash
1,腾讯应用宝
2,360应用中心
3,豌豆荚
4,百度应用中心
5,91手机助手
```
