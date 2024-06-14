######################################
#  Node dependencies frontend cache  #
######################################

FROM        node:lts-slim as node-dependencies
RUN         apt-get update
RUN         apt-get install -y python3
RUN         apt-get install build-essential -y
WORKDIR     /root 
RUN         npm install


#########################
#  Clojure compilation  #
#########################

FROM        clojure:temurin-21-tools-deps-jammy as wizard-compiler
RUN         curl -fsSL https://deb.nodesource.com/setup_21.x | bash -
RUN         apt-get update
RUN         apt-get install -y nodejs

RUN         npm install shadow-cljs -g
RUN         npm install esbuild -g
RUN         npm install babel -g
RUN         npm install postcss -g
RUN         npm install esbuild -g
RUN         npm install concurrently -g
COPY        shadow-cljs.edn /root/shadow-cljs.edn

COPY        package.json /root/package.json
COPY        deps.edn /root/deps.edn
COPY        postcss.config.cjs /root/postcss.config.cjs
COPY        src /root/src
COPY        public   /root/public
COPY        resources   /root/resources

WORKDIR      /root
CMD         ["npm", "install"]
CMD         ["npm", "run", "release"]
