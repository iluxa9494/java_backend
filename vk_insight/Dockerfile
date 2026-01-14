FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY . .
WORKDIR /app/js-second1
EXPOSE 8084
CMD ["node","mytest.js"]
