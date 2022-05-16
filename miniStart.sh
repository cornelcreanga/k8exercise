#!/usr/bin/zsh

minikube start  --cpus 4  --memory 8192  --insecure-registry=192.168.0.0/16

minikube addons enable metrics-server
kubectl create -f namespaces.yaml
helm repo add spark-operator https://googlecloudplatform.github.io/spark-on-k8s-operator
helm install sparkoperator spark-operator/spark-operator --namespace spark-operator --set sparkJobNamespace=spark-apps,enableWebhook=true --set image.tag=v1beta2-1.3.3-3.1.1

