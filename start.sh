cd /software/voice
docker build -t voice:latest .

docker stop voice
docker rm voice

docker run -itd -v /software/voice:/config --device=/dev/snd:/dev/snd --name=voice voice
