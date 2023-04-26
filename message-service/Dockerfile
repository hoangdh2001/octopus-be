FROM node:19-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install glob rimraf --force

RUN npm install --only=dev

COPY . .

RUN npm run build

CMD ["node", "dist/main"]
