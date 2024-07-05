FROM node:lts-alpine
RUN npm install -g http-server
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
ARG VUE_APP_DKT_SERVER_BACKEND
ARG VUE_APP_DKT_SERVER_RASA
RUN VUE_APP_DKT_SERVER_BACKEND=$VUE_APP_DKT_SERVER_BACKEND \
    VUE_APP_DKT_SERVER_RASA=$VUE_APP_DKT_SERVER_RASA \
    npm run build
EXPOSE 5601
CMD ["http-server", "dist", "-p", "5601"]
