# syntax=docker/dockerfile:1.4

FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm install

FROM node:20-alpine
WORKDIR /app
ENV HOST=0.0.0.0
ENV PORT=4200
COPY --from=deps /app/node_modules ./node_modules
COPY . .
EXPOSE 4200
CMD ["npm","run","start","--","--host","0.0.0.0","--port","4200"]
