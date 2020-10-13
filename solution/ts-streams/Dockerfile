FROM confluentinc/cp-kafka-connect:6.0.0-1-ubi8
ENV CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components"
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-datagen:latest