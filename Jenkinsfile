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
                      .
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
