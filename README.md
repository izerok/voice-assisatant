# 个人语音助手

## 项目介绍

基于百度语音识别API,ChatGPT-API,实现的个人语音助手,实现通过音量阈值自动唤醒,语音识别,语音合成,智能聊天,WebHook等功能

## 功能介绍

1. 语音识别
2. 语音合成
3. 智能聊天
4. WebHook

## 项目结构

```
├── README.md
├── pom.xml
├── Dockerfile
├── .gitignore
├── start.sh
├── src
│   ├── main
│   │   ├── java
│   │   │   └── api
│   │   │       ├── OutsideApi.java
│   │   │   └── audio
│   │   │       ├── AudioDevice.java
│   │   │       ├── AudioDeviceTypeEnum.java
│   │   │       ├── AudioDeviceUtil.java
│   │   │       ├── AudioPlayUtil.java
│   │   │       ├── AudioRecordUtil.java
│   │   │   └── config
│   │   │       ├── entity
│   │   │       │   ├── BaiduApiEntity.java
│   │   │       │   ├── DeviceConfigEntity.java
│   │   │       │   ├── OpenAiConfigEntity.java
│   │   │       │   ├── RecordConfigEntity.java
│   │   │       │   └── WebHookConfigEntity.java
│   │   │       ├── ConfigRel.java
│   │   │       ├── ConfigUtil.java
│   │   │       └── RealBean.java
│   │   │   └── threadutil
│   │   │       ├── MyThreadUtils.java
│   │   │   └── webhook
│   │   │       ├── WebHookUtil.java
│   │   │   └── Main.java
│   │   ├── resources
│   │   │   ├── logback.xml
```

## 本地启动

```
git clone 
配置config目录下的配置文件
启动IDEA项目
```

## Docker启动

```
docker build -t voice .
docker run -itd -v /software/voice:/config --device=/dev/snd:/dev/snd --name=voice voice
-------------------------
sh start.sh
```

## 项目配置

| key                    | description        | default value | required |
|------------------------|--------------------|---------------|----------|
| baiduAppId             | 百度语音识别appId        |               | true     |
| baiduApiKey            | 百度语音识别apiKey       |               | true     |
| baiduSecretKey         | 百度语音识别secretKey    |               | true     |
| openaiKeys             | openaiKeys 多个时以,隔开 |               | true     |
| inputSampleRate        | 输入采样率              | 16000         | false    |
| inputSampleSizeInBits  | 输入采样位数             | 16            | false    |
| inputAudioDeviceNum    | 输入设备编号             | 0             | false    |
| outputSampleRate       | 输出采样率              | 16000         | false    |
| outputSampleSizeInBits | 输出采样位数             | 16            | false    |
| outputAudioDeviceNum   | 输出设备编号             | 0             | false    |
| volumePercent          | 音量百分比              | 0.5           | false    |
| openaiHost             | openaiHost         |               | false    |
| maxRecordTime          | 最大录音时间             | 0             | false    |
| recordVolumePercent    | 录音音量百分比            | 0             | false    |
| lessRecordVolumeTime   | 低于录音音量百分比的时间       | 0             | false    |
| webHookUrl             | WebHookUrl         |               | false    |

config.json.demo

```json
{
  "openaiKeys": "sk-xxx",
  "openaiHost": "https://xxx/",
  "baiduAppId": "xxx",
  "baiduApiKey": "xxx",
  "baiduSecretKey": "xxx",
  "recordVolumePercent": 5,
  "volumePercent": 0.5
}
```




