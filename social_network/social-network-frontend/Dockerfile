FROM node:18.13.0 AS build
WORKDIR /app
COPY package*.json ./
RUN yarn install
COPY . .
RUN yarn build

FROM nginx:latest AS serve
COPY --from=build /app/dist /usr/share/nginx/html
COPY ./frontnginx/server.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
