FROM node:6
RUN mkdir -p /frontend/app
WORKDIR /frontend/app
ADD package.json /frontend/app
RUN npm install
ADD . /frontend/app
EXPOSE 4200
CMD ["npm", "start"]
