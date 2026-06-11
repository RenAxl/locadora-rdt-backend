pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    triggers {
        githubPush()
    }

    environment {
        APP_NAME = 'locadora-rdt-backend'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        COMPOSE_PROJECT_DIR = '/workspace/locadora-rdt/locadora-rdt-devops'
        GIT_SSH_URL = 'git@github.com:RenAxl/locadora-rdt-backend.git'
        GIT_AUTHOR_NAME = 'Jenkins CI'
        GIT_AUTHOR_EMAIL = 'jenkins@locadora-rdt.local'
    }

    stages {
        stage('Checkout') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'developer'
                    branch 'main'
                }
            }
            steps {
                checkout scm
                sh 'git status --short --branch'
            }
        }

        stage('Maven Clean Package') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'developer'
                    branch 'main'
                }
            }
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
            post {
                always {
                    junit allowEmptyResults: false, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive JAR') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'developer'
                    branch 'main'
                }
            }
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Docker Build - Dev') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'developer'
                }
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:${IMAGE_TAG} \
                      -t ${APP_NAME}:latest \
                      .
                '''
            }
        }

        stage('Merge Dev Into Main') {
            when {
                anyOf {
                    branch 'dev'
                    branch 'developer'
                }
            }
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'github-ssh-locadora-rdt-backend',
                    keyFileVariable: 'GIT_SSH_KEY',
                    usernameVariable: 'GIT_SSH_USER'
                )]) {
                    sh '''
                        set -e

                        mkdir -p "$WORKSPACE/.ssh"
                        ssh-keyscan github.com > "$WORKSPACE/.ssh/known_hosts"
                        chmod 700 "$WORKSPACE/.ssh"
                        chmod 600 "$WORKSPACE/.ssh/known_hosts"

                        export GIT_SSH_COMMAND="ssh -i $GIT_SSH_KEY -o UserKnownHostsFile=$WORKSPACE/.ssh/known_hosts -o StrictHostKeyChecking=yes"

                        git config user.name "${GIT_AUTHOR_NAME}"
                        git config user.email "${GIT_AUTHOR_EMAIL}"
                        git remote set-url origin "${GIT_SSH_URL}"

                        git fetch origin main ${BRANCH_NAME}
                        git checkout -B main origin/main
                        git merge --no-ff origin/${BRANCH_NAME} -m "Merge ${BRANCH_NAME} into main by Jenkins build ${BUILD_NUMBER}"
                        git push origin main
                    '''
                }
            }
        }

        stage('Docker Build - Main') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    docker build \
                      -t ${APP_NAME}:${IMAGE_TAG} \
                      -t ${APP_NAME}:latest \
                      .
                '''
            }
        }

        stage('Deploy Local Backend') {
            when {
                branch 'main'
            }
            steps {
                sh '''
                    IMAGE_TAG=${IMAGE_TAG} docker compose \
                      -f ${COMPOSE_PROJECT_DIR}/docker-compose.yml \
                      up -d --no-deps --force-recreate --no-build backend
                '''
            }
        }

        stage('Backend Health Check') {
            when {
                branch 'main'
            }
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
        failure {
            echo 'Pipeline FAILED. Merge/deploy nao sera executado apos falha em etapa anterior.'
        }
        success {
            echo 'Pipeline SUCCESS.'
        }
        always {
            cleanWs()
        }
    }
}
