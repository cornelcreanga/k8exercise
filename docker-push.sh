mvn clean package
docker build -f Dockerfile --tag k8exercise .
docker tag k8exercise:latest cornelcreanga/k8exercise:3.1.1
docker push cornelcreanga/k8exercise:3.1.1