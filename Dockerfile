
#####################################
#  Node dependencies cache          #
#####################################

FROM        node:lts-slim as node-dependencies
RUN         apt-get update
RUN         apt-get install -y python3
RUN         apt-get install build-essential -y

#########################
#  Clojure compilation  #
#########################

FROM        clojure:temurin-21-tools-deps-jammy as wizard-compiler
RUN         curl -fsSL https://deb.nodesource.com/setup_21.x | bash -
RUN         apt-get install -y nodejs
RUN         npm install shadow-cljs webpack webpack-cli -g

# Copying source code

COPY        shadow-cljs.edn /root/shadow-cljs.edn
COPY        src /root/src
COPY        public   /root/public
COPY        resources   /root/resources
COPY        node_modules   /root/node_modules

WORKDIR      /root
CMD         ["node", "resources/server.js"]
