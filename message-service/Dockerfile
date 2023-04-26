FROM node:19-alpine

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install glob rimraf --legacy-peer-deps

RUN npm install --only=prod --legacy-peer-deps

COPY . .

RUN npm run build

CMD ["node", "dist/main"]
