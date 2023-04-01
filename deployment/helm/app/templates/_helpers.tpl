{{/*
Expand the name of the chart.
*/}}
{{- define "app.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "app.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "app.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "app.labels" -}}
helm.sh/chart: {{ include "app.chart" . }}
{{ include "app.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "app.selectorLabels" -}}
app.kubernetes.io/name: {{ include "app.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "app.serviceAuthName" -}}
{{- if .Values.serviceAuth.create }}
{{- default (include "app.fullname" .) .Values.serviceAuth.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{- define "nginx.ingress.annotations.frontend" -}}
nginx.ingress.kubernetes.io/ssl-redirect: 'true'

# According to the RFCs of HTTP/1.1 and HTTP/2 Ingress must not forward
# any connection headers:
# https://tools.ietf.org/html/rfc7540#section-8.1.2.2
# https://tools.ietf.org/html/rfc7230#section-6.1
# In reality this only leads to errors in the shared cluster with Safari and curl
# when there is a Upgrade header in HTTP/2 over TLS connections:
# https://serverfault.com/a/937269
nginx.ingress.kubernetes.io/configuration-snippet: |
  proxy_hide_header Upgrade;
{{- end -}}

{{/*
Domain names of own applications.
*/}}
{{- define "gateway.domain" -}}
api.{{ required "ingress.domainSuffix missing" .Values.ingress.domainSuffix }}
{{- end -}}

{{- define "gateway.secondaryDomain" -}}
api.{{ .Values.ingress.secondaryDomainSuffix }}
{{- end -}}

{{- define "eureka.envVars" -}}
- name: EUREKA_HOST
  value: "eureka.octopus-backend.svc.cluster.local"
{{- end -}}

{{- define "redis.envVars" -}}
{{- if .Values.redis.dbUri }}
- name: REDIS_API
  value: {{ .Values.redis.address | quote }}
{{- else }}
- name: REDIS_USER
  value: {{ required "redis.user missing" .Values.redis.user }}
- name: REDIS_PASS
  value: {{ required "redis.pass missing" .Values.redis.pass }}
{{- end -}}
{{- end -}}

# Auth
{{- define "auth.envVars" -}}
- name: MYSQL_API
  value: {{ include "auth.dbUri" . | quote }}
- name: MYSQL_USER
  value: {{ required "mysql.user missing" .Values.mysql.user }}
- name: MYSQL_PASS
  value: {{ required "mysql.pass missing" .Values.mysql.pass }}
{{- end -}}

{{- define "auth.dbUri" -}}
{{ required "mysql.address missing" .Values.mysql.address }}
{{- end -}}

{{- define "message.envVars" -}}
- name: MONGODB_API
  value: {{ include "message.dbUri" . | quote }}
- name: MONGODB_USER
  value: {{ required "mongodb.user missing" .Values.mongodb.user }}
- name: MONGODB_PASS
  value: {{ required "mongodb.pass missing" .Values.mongodb.pass }}
{{- end -}}

{{- define "message.dbUri" -}}
{{ required "mongodb.srvAddress missing" .Values.mongodb.srvAddress }}
{{- end -}}

{{- define "kafka.envVars" -}}
- name: KAFKA_HOST
  value: kafka.octopus-backend.svc.cluster.local
- name: KAFKA_PORT
  value: "9092"
{{- end -}}