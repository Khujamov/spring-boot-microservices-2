FROM java:8
WORKDIR /app
ENV TRUSTSTORE_PASSWORD secret
ENV KEYSTORE_PASSWORD secret
ADD build/libs/chat-service-0.3.0.jar /app
ADD entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
EXPOSE 8280
