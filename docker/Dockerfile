FROM openjdk

# Install.
#RUN \
#  sed -i 's/# \(.*multiverse$\)/\1/g' /etc/apt/sources.list && \
#  apt-get update && \
#  apt-get -y upgrade && \
#  apt-get install -y build-essential && \
#  apt-get install -y software-properties-common && \
#  apt-get install -y byobu curl git htop man unzip vim wget && \
#  rm -rf /var/lib/apt/lists/*

# Set environment variables.
#ENV HOME /root

# Define working directory.
#WORKDIR /opt/p2p/

ADD . /opt/p2p
WORKDIR /opt/p2p

# Define default command.
#CMD ["bash"]
#CMD ["java","-jar","-DpeerName=Peer1 p2p.jar -n Peer1 -b 50670"]
