pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        APP_NAME = 'locadora-rdt-backend'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_DIR = '/workspace/locadora-rdt/locadora-rdt-devops'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Maven') {
            agent {
                docker {
                    image 'maven:3.8.8-eclipse-temurin-11'
                    args '-v maven_repository:/root/.m2'
                    reuseNode true
                }
            }
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean package'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Prepare Docker Image') {
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:${IMAGE_TAG} \
                      -t ${APP_NAME}:latest \
                      -t ${APP_NAME}:0.0.1 \
                      .
                '''
            }
        }

        stage('Deploy Local Backend') {
            steps {
                sh '''
                    docker compose \
                      -f ${COMPOSE_PROJECT_DIR}/docker-compose.yml \
                      up -d --no-deps --force-recreate backend
                '''
            }
        }

        stage('Backend Health Check') {
            steps {
                sh '''
                    for attempt in $(seq 1 30); do
                      if docker run --rm --network locadora-rdt-network curlimages/curl:8.11.1 \
                        -fsS http://backend:8080/actuator/health; then
                        exit 0
                      fi

                      echo "Backend ainda nao esta pronto. Tentativa ${attempt}/30"
                      sleep 5
                    done

                    echo "Backend nao respondeu com sucesso no health check."
                    docker logs --tail 120 locadora-rdt-backend || true
                    exit 1
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
