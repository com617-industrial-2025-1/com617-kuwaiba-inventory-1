FROM neotropic/kuwaiba:v2.1-nightly

USER root

#  install unzip rsync telnet nano (busybox-extras) and some other basic tools
RUN apk add --no-cache busybox-extras nano zip unzip rsync curl

RUN mkdir -p /data-archive  && chown kuwaiba:kuwaiba /data-archive 
RUN mkdir -p /data-zip  && chown kuwaiba:kuwaiba /data-zip
RUN mkdir -p /data-overlay  && chown kuwaiba:kuwaiba /data-overlay
RUN mkdir -p /exported-reports && chown kuwaiba:kuwaiba /exported-reports
RUN mkdir -p /custom-config && chown kuwaiba:kuwaiba /custom-config

USER kuwaiba

