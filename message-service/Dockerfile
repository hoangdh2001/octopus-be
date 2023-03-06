FROM node:19-alpine AS development

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install glob rimraf

RUN npm install --only=dev

COPY . .

RUN npm run build

CMD ["node", "dist/main"]
