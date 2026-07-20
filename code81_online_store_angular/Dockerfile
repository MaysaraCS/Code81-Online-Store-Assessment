# ---- Build stage ----
FROM node:22-alpine AS build
WORKDIR /app

# Same layer-caching trick as the backend Dockerfile: install dependencies
# before copying the rest of the source, so editing a .ts file doesn't
# trigger a full npm install on the next rebuild.
COPY package*.json ./
RUN npm ci

COPY . .
RUN npx ng build

# ---- Serve stage ----
# The Angular build output is just static HTML/CSS/JS - it doesn't need
# Node.js to run, only a plain web server. nginx is the standard choice.
FROM nginx:alpine
COPY --from=build /app/dist/online-store-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
